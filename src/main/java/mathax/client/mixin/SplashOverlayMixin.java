package mathax.client.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceReload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.function.Consumer;

@Mixin(SplashOverlay.class)
public abstract class SplashOverlayMixin {
    @Mutable
    @Shadow
    @Final
    static Identifier LOGO;

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    @Final
    private boolean reloading;

    @Shadow
    @Final
    private ResourceReload reload;

    @Shadow
    private float progress;

    @Shadow
    private long reloadCompleteTime;

    @Shadow
    private long reloadStartTime;

    @Shadow
    @Final
    private Consumer<Optional<Throwable>> exceptionHandler;

    @Inject(method = "init(Lnet/minecraft/client/MinecraftClient;)V", at = @At("HEAD"), cancellable = true)
    private static void init(MinecraftClient client, CallbackInfo info) {
        LOGO = new Identifier("mathax", "textures/icons/icon.png");
        info.cancel();
    }

    @Inject(at = @At("TAIL"), method = "render")
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo info) {
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        long time = Util.getMeasuringTimeMs();
        if (reloading && reloadStartTime == -1L) reloadStartTime = time;
        float completeTime = reloadCompleteTime > -1L ? (float) (time - reloadCompleteTime) / 1000.0F : -1.0F;
        float g = reloadStartTime > -1L ? (float) (time - reloadStartTime) / 500.0F : -1.0F;

        int color;
        float floatTime;
        if (completeTime >= 1.0F) {
            if (client.currentScreen != null) client.currentScreen.render(matrices, 0, 0, delta);

            color = MathHelper.ceil((1.0F - MathHelper.clamp(completeTime - 1.0F, 0.0F, 1.0F)) * 255.0F);
            DrawableHelper.fill(matrices, 0, 0, width, height, withAlpha(0x1e1e2d, color));
            floatTime = 1.0F - MathHelper.clamp(completeTime - 1.0F, 0.0F, 1.0F);
        } else if (reloading) {
            if (client.currentScreen != null && g < 1.0F) client.currentScreen.render(matrices, mouseX, mouseY, delta);

            color = MathHelper.ceil(MathHelper.clamp(g, 0.15D, 1.0D) * 255.0D);
            DrawableHelper.fill(matrices, 0, 0, width, height, withAlpha(0x1e1e2d, color));
            floatTime = MathHelper.clamp(g, 0.0F, 1.0F);
        } else {
            color = 0x1e1e2d;
            float p = (float) (color >> 16 & 255) / 255.0F;
            float q = (float) (color >> 8 & 255) / 255.0F;
            float r = (float) (color & 255) / 255.0F;
            GlStateManager._clearColor(p, q, r, 1.0F);
            GlStateManager._clear(16384, MinecraftClient.IS_SYSTEM_MAC);
            floatTime = 1.0F;
        }

        color = (int) ((double) client.getWindow().getScaledWidth() * 0.5D);
        double res = Math.min((double) client.getWindow().getScaledWidth() * 0.75D, client.getWindow().getScaledHeight()) * 0.25D;
        int resCalculated = (int) ((res * 4.0D) * 0.5D);

        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.setShaderTexture(0, LOGO);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, floatTime);
        DrawableHelper.drawTexture(matrices, color - (resCalculated / 2), (int) (res * 0.5D), resCalculated, resCalculated, 0, 0, 512, 512, 512, 512);

        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();

        int x = (int) ((double) client.getWindow().getScaledHeight() * 0.8325D);

        progress = MathHelper.clamp(progress * 0.95F + reload.getProgress() * 0.050000012F, 0.0F, 1.0F);

        if (completeTime < 1.0F) renderProgressBar(matrices, width / 2 - resCalculated, x - 5, width / 2 + resCalculated, x + 5, 1.0F - MathHelper.clamp(completeTime, 0.0F, 1.0F), null);

        if (completeTime >= 2.0F) client.setOverlay(null);

        if (reloadCompleteTime == -1L && reload.isComplete() && (!reloading || g >= 2.0F)) {
            try {
                reload.throwException();
                exceptionHandler.accept(Optional.empty());
            } catch (Throwable throwable) {
                exceptionHandler.accept(Optional.of(throwable));
            }

            reloadCompleteTime = Util.getMeasuringTimeMs();
            if (client.currentScreen != null) client.currentScreen.init(client, client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight());
        }
    }

    @Inject(method = "renderProgressBar", at = @At("TAIL"))
    private void renderProgressBar(MatrixStack matrices, int x1, int y1, int x2, int y2, float opacity, CallbackInfo info) {
        int i = MathHelper.ceil((float) (x2 - x1 - 2) * progress);
        int color = withAlpha(0xe64c65, 255);

        DrawableHelper.fill(matrices, x1 + 2, y1 + 2, x1 + i, y2 - 2, color);
        DrawableHelper.fill(matrices, x1 + 1, y1, x2 - 1, y1 + 1, color);
        DrawableHelper.fill(matrices, x1 + 1, y2, x2 - 1, y2 - 1, color);
        DrawableHelper.fill(matrices, x1, y1, x1 + 1, y2, color);
        DrawableHelper.fill(matrices, x2, y1, x2 - 1, y2, color);
    }

    private static int withAlpha(int color, int alpha) {
        return color | alpha << 24;
    }

    @ModifyConstant(method = "render", constant = @Constant(floatValue = 500))
    private float getFadeInTime(float old) {
        return 0;
    }

    @ModifyConstant(method = "render", constant = @Constant(floatValue = 1000))
    private float getFadeOutTime(float old) {
        return 0;
    }
}
