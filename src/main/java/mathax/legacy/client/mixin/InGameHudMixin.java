package mathax.legacy.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import mathax.legacy.client.MatHaxLegacy;
import mathax.legacy.client.events.render.Render2DEvent;
import mathax.legacy.client.systems.modules.render.MountHUD;
import mathax.legacy.client.systems.modules.render.NoRender;
import mathax.legacy.client.systems.modules.Modules;
import mathax.legacy.client.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
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
    @Shadow private int scaledWidth;

    @Shadow private int scaledHeight;

    @Shadow @Final private MinecraftClient client;

    @Shadow public abstract void clear();

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(MatrixStack matrixStack, float tickDelta, CallbackInfo info) {
        client.getProfiler().push("mathaxlegacy_render_2d");

        Utils.unscaledProjection();

        MatHaxLegacy.EVENT_BUS.post(Render2DEvent.get(scaledWidth, scaledHeight, tickDelta));

        Utils.scaledProjection();
        RenderSystem.applyModelViewMatrix();

        client.getProfiler().pop();
    }

    /*@Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"))
    public void redirectRenderCrosshair(InGameHud inGameHud, MatrixStack matrices, int x, int y, int u, int v, int width, int height) {
        CustomCrosshair customCrosshair = Modules.get().get(CustomCrosshair.class);

        if (!customCrosshair.isActive()) {
            inGameHud.drawTexture(matrices, x, y, u, v, width, height);
        }
    }*/

    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
    private void onRenderStatusEffectOverlay(CallbackInfo info) {
        if (Modules.get().get(NoRender.class).noPotionIcons()) info.cancel();
    }

    @Inject(method = "renderPortalOverlay", at = @At("HEAD"), cancellable = true)
    private void onRenderPortalOverlay(float f, CallbackInfo info) {
        if (Modules.get().get(NoRender.class).noPortalOverlay()) info.cancel();
    }

    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderOverlay(Lnet/minecraft/util/Identifier;F)V", ordinal = 0))
    private void onRenderPumpkinOverlay(Args args) {
        if (Modules.get().get(NoRender.class).noPumpkinOverlay()) args.set(1, 0f);
    }

    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderOverlay(Lnet/minecraft/util/Identifier;F)V", ordinal = 1))
    private void onRenderPowderedSnowOverlay(Args args) {
        if (Modules.get().get(NoRender.class).noPowderedSnowOverlay()) args.set(1, 0f);
    }

    @Inject(method = "renderVignetteOverlay", at = @At("HEAD"), cancellable = true)
    private void onRenderVignetteOverlay(Entity entity, CallbackInfo info) {
        if (Modules.get().get(NoRender.class).noVignette()) info.cancel();
    }

    @Inject(method = "renderScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    private void onRenderScoreboardSidebar(MatrixStack matrixStack, ScoreboardObjective scoreboardObjective, CallbackInfo info) {
        if (Modules.get().get(NoRender.class).noScoreboard()) info.cancel();
    }

    @Inject(method = "renderSpyglassOverlay", at = @At("HEAD"), cancellable = true)
    private void onRenderSpyglassOverlay(float scale, CallbackInfo info) {
        if (Modules.get().get(NoRender.class).noSpyglassOverlay.get()) info.cancel();
    }

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void onRenderCrosshair(MatrixStack matrices, CallbackInfo info) {
        if (Modules.get().get(NoRender.class).noCrosshair()) info.cancel();
    }

    @Inject(method = "renderHeldItemTooltip", at = @At("HEAD"), cancellable = true)
    private void onRenderHeldItemTooltip(MatrixStack matrices, CallbackInfo info) {
        if (Modules.get().get(NoRender.class).noHeldItemName()) info.cancel();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;hasJumpingMount()Z"))
    private boolean onSwitchBar(ClientPlayerEntity player) {
        if (!Modules.get().isActive(MountHUD.class) || !client.interactionManager.hasExperienceBar()) return player.hasJumpingMount();
        return player.hasJumpingMount() && client.options.keyJump.isPressed() || player.getMountJumpStrength() > 0;
    }

    @ModifyConstant(method = "renderMountHealth", constant = @Constant(intValue = 39))
    private int onRenderMountHelath(int yOffset) {
        return Modules.get().isActive(MountHUD.class) && client.interactionManager.hasStatusBars() ? yOffset + 10 : yOffset;
    }

    @ModifyVariable(method = "renderStatusBars", at = @At(value = "STORE", ordinal = 1), ordinal = 10)
    private int onRenderStatusBars(int y) {
        return Modules.get().isActive(MountHUD.class) && client.player.hasJumpingMount() ? y - 10 : y;
    }

    @ModifyArg(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;getHeartCount(Lnet/minecraft/entity/LivingEntity;)I", ordinal = 0))
    private LivingEntity modifyGetHeartCount(LivingEntity entity) {
        return Modules.get().isActive(MountHUD.class) ? null : entity;
    }
}
