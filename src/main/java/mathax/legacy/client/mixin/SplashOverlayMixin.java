package mathax.legacy.client.mixin;

import mathax.legacy.client.utils.splash.PreviewSplashOverlay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.SplashOverlay;
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
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.IntSupplier;

import static net.minecraft.client.gui.DrawableHelper.fill;

// TODO: Rewrite

@Mixin(SplashOverlay.class)
public abstract class SplashOverlayMixin {
    @Shadow
    private float progress;

    @Shadow
    private long reloadCompleteTime;

    @Shadow
    @Final
    private ResourceReload reload;

    @Mutable
    @Shadow
    @Final
    static Identifier LOGO;

    @Shadow
    @Final
    private MinecraftClient client;

    private static final Identifier MATHAX_LOGO = new Identifier("mathaxlegacy", "textures/splash/splash.png");

    @Inject(method = "init(Lnet/minecraft/client/MinecraftClient;)V", at = @At("HEAD"), cancellable = true)
    private static void init(MinecraftClient client, CallbackInfo info) {
        LOGO = MATHAX_LOGO;
        info.cancel();
    }

    // Render

    @Inject(method = "render", at = @At("TAIL"))
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();
        long l = Util.getMeasuringTimeMs();
        float f = this.reloadCompleteTime > -1L ? (float) (l - this.reloadCompleteTime) / 1000.0F : -1.0F;
        int x = (int) ((double) client.getWindow().getScaledHeight() * 0.8325D);
        float y = this.reload.getProgress();
        double d = Math.min((double) client.getWindow().getScaledWidth() * 0.75D, client.getWindow().getScaledHeight()) * 0.25D;
        double e = d * 4.0D;
        int w = (int) (e * 0.5D);
        this.progress = MathHelper.clamp(this.progress * 0.95F + y * 0.050000012F, 0.0F, 1.0F);
        if (f < 1.0F) this.renderProgressBar(matrices, width / 2 - w, x - 5, width / 2 + w, x + 5, 1.0F - MathHelper.clamp(f, 0.0F, 1.0F), null);
    }

    // Background

    @Mutable
    @Shadow
    @Final
    private static IntSupplier BRAND_ARGB;

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void adjustBg(CallbackInfo info) {
        BRAND_ARGB = () -> 0x1e1e2d;
    }

    // Progress bar

    @Inject(at = @At("TAIL"), method = "renderProgressBar")
    private void renderProgressBar(MatrixStack matrices, int x1, int y1, int x2, int y2, float opacity, CallbackInfo ci) {
        int i = MathHelper.ceil((float) (x2 - x1 - 2) * this.progress);

        int j = Math.round(opacity * 255.0F);
        int k = 0xe64c65 | 255 << 24;
        int kk = 0xe64c65 | 255 << 24;
        fill(matrices, x1 + 2, y1 + 2, x1 + i, y2 - 2, k);
        fill(matrices, x1 + 1, y1, x2 - 1, y1 + 1, kk);
        fill(matrices, x1 + 1, y2, x2 - 1, y2 - 1, kk);
        fill(matrices, x1, y1, x1 + 1, y2, kk);
        fill(matrices, x2, y1, x2 - 1, y2, kk);
    }

    // Bar color

    private static final String FILL_DESC = "Lnet/minecraft/client/gui/screen/SplashOverlay;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V";

    @ModifyArg(method = "renderProgressBar", at = @At(value = "INVOKE", target = FILL_DESC, ordinal = 0), index = 5)
    private int adjustBarColor(int color) {
        return 0x1e1e2d | color;
    }

    // Done

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setOverlay(Lnet/minecraft/client/gui/screen/Overlay;)V"))
    private void onSetOverlay(CallbackInfo info) {
        // Casting because SplashOverlayMixin doesn't extend PreviewSplashOverlay
        if ((Object) this instanceof PreviewSplashOverlay previewScreen) previewScreen.onDone();
    }

    // Fade

    @ModifyConstant(method = "render", constant = @Constant(floatValue = 500))
    private float getFadeInTime(float old) {
        return 0;
    }

    @ModifyConstant(method = "render", constant = @Constant(floatValue = 1000))
    private float getFadeOutTime(float old) {
        return 1500;
    }
}
