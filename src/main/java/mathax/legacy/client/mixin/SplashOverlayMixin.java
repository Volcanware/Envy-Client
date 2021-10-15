package mathax.legacy.client.mixin;

import mathax.legacy.client.utils.splash.PreviewSplashOverlay;
import mathax.legacy.client.utils.splash.SplashUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.IntSupplier;

@Mixin(SplashOverlay.class)
public abstract class SplashOverlayMixin {

    // Logo

    @Shadow
    @Final
    static Identifier LOGO;

    private static final Identifier MATHAX_LOGO = new Identifier("mathaxlegacy", "textures/splash/splash.png");

    @Inject(method = "init(Lnet/minecraft/client/MinecraftClient;)V", at = @At("HEAD"), cancellable = true)
    private static void init(MinecraftClient client, CallbackInfo info) {
        client.getTextureManager().registerTexture(LOGO, new SplashUtils(MATHAX_LOGO));
        info.cancel();
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

    @Shadow
    private static int withAlpha(int color, int alpha) {
        return 0;
    }

    @Redirect(method = "renderProgressBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/BackgroundHelper$ColorMixer;getArgb(IIII)I"))
    private int progressBarBorderProxy(int a, int r, int g, int b, MatrixStack matrices, int x1, int y1, int x2, int y2, float opacity) {

        // Bar background
        DrawableHelper.fill(
            matrices, x1 + 1, y1 + 1, x2 - 1, y2 - 1,
            withAlpha(0x1e1e2d, a)
        );

        // Bar border
        return withAlpha(0xe64c65, a);
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
        if ((Object) this instanceof PreviewSplashOverlay previewScreen) {
            // Casting because SplashOverlayMixin doesn't extend PreviewSplashOverlay
            previewScreen.onDone();
        }
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
