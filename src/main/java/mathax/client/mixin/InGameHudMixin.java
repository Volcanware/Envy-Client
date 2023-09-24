package mathax.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import mathax.client.MatHax;
import mathax.client.events.render.Render2DEvent;
import mathax.client.systems.modules.Modules;
import mathax.client.utils.Utils;
import mathax.client.systems.modules.render.Freecam;
import mathax.client.systems.modules.render.MountHUD;
import mathax.client.systems.modules.render.NoRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.LivingEntity;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow
    private int scaledWidth;

    @Shadow
    private int scaledHeight;

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    public abstract void clear();

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, float tickDelta, CallbackInfo info) {
        client.getProfiler().push("mathax_render_2d");

        Utils.unscaledProjection();

        MatHax.EVENT_BUS.post(Render2DEvent.get(context, scaledWidth, scaledHeight, tickDelta));

        Utils.scaledProjection();
        RenderSystem.applyModelViewMatrix();

        client.getProfiler().pop();
    }

    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
    private void onRenderStatusEffectOverlay(CallbackInfo info) {
        if (Modules.get().get(NoRender.class).noPotionIcons()) info.cancel();
    }

    @Inject(method = "renderPortalOverlay", at = @At("HEAD"), cancellable = true)
    private void onRenderPortalOverlay(DrawContext context, float nauseaStrength, CallbackInfo ci) {
        if (Modules.get().get(NoRender.class).noPortalOverlay()) ci.cancel();
    }

    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderOverlay(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/util/Identifier;F)V", ordinal = 0))
    private void onRenderPumpkinOverlay(Args args) {
        if (Modules.get().get(NoRender.class).noPumpkinOverlay()) args.set(2, 0f);
    }

    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderOverlay(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/util/Identifier;F)V", ordinal = 1))
    private void onRenderPowderedSnowOverlay(Args args) {
        if (Modules.get().get(NoRender.class).noPowderedSnowOverlay()) args.set(2, 0f);
    }

    @Inject(method = "renderVignetteOverlay", at = @At("HEAD"), cancellable = true)
    private void onRenderVignetteOverlay(DrawContext context, Entity entity, CallbackInfo ci) {
        if (Modules.get().get(NoRender.class).noVignette()) ci.cancel();
    }

    @Inject(method = "renderScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    private void onRenderScoreboardSidebar(DrawContext context, ScoreboardObjective objective, CallbackInfo ci) {
        if (Modules.get().get(NoRender.class).noScoreboard()) ci.cancel();
    }

    @Inject(method = "renderSpyglassOverlay", at = @At("HEAD"), cancellable = true)
    private void onRenderSpyglassOverlay(DrawContext context, float scale, CallbackInfo ci) {
        if (Modules.get().get(NoRender.class).noSpyglassOverlay()) ci.cancel();
    }

    @Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/Perspective;isFirstPerson()Z"))
    private boolean alwaysRenderCrosshairInFreecam(Perspective perspective) {
        if (Modules.get().isActive(Freecam.class)) return true;
        return perspective.isFirstPerson();
    }

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void onRenderCrosshair(DrawContext context, CallbackInfo ci) {
        if (Modules.get().get(NoRender.class).noCrosshair()) ci.cancel();
    }

    @Inject(method = "renderHeldItemTooltip", at = @At("HEAD"), cancellable = true)
    private void onRenderHeldItemTooltip(DrawContext context, CallbackInfo ci) {
        if (Modules.get().get(NoRender.class).noHeldItemName()) ci.cancel();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getJumpingMount()Lnet/minecraft/entity/JumpingMount;"))
    private JumpingMount onSwitchBar(ClientPlayerEntity player) {
        if (!Modules.get().isActive(MountHUD.class) || !client.interactionManager.hasExperienceBar()) return player.getJumpingMount();
        return client.options.jumpKey.isPressed() || player.getMountJumpStrength() > 0? player.getJumpingMount() : null;
    }

    @ModifyConstant(method = "renderMountHealth", constant = @Constant(intValue = 39))
    private int onRenderMountHelath(int yOffset) {
        return Modules.get().isActive(MountHUD.class) && client.interactionManager.hasStatusBars() ? yOffset + 10 : yOffset;
    }

    @ModifyVariable(method = "renderStatusBars", at = @At(value = "STORE", ordinal = 1), ordinal = 10)
    private int onRenderStatusBars(int y) {
        return Modules.get().isActive(MountHUD.class) && client.player.getJumpingMount() != null ? y - 10 : y;
    }

    @ModifyArg(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;getHeartCount(Lnet/minecraft/entity/LivingEntity;)I", ordinal = 0))
    private LivingEntity modifyGetHeartCount(LivingEntity entity) {
        return Modules.get().isActive(MountHUD.class) ? null : entity;
    }
}
