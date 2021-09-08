package mathax.client.legacy.mixin;

import mathax.client.legacy.utils.splash.PreviewSplashOverlay;
import mathax.client.legacy.utils.splash.SplashUtils;
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

    @Shadow
    @Final
    static Identifier LOGO;

    private static final Identifier MATHAX_LOGO = new Identifier("mathaxlegacy", "splash/splash.png");

    /** Descriptor for {@link SplashOverlay#fill(MatrixStack, int, int, int, int, int)} */
    private static final String FILL_DESC = "Lnet/minecraft/client/gui/screen/SplashOverlay;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V";

    @Shadow
    private static int withAlpha(int color, int alpha) {
        return 0;
    }

    // Logo
    @Inject(method = "init(Lnet/minecraft/client/MinecraftClient;)V", at = @At("HEAD"), cancellable = true)
    private static void init(MinecraftClient client, CallbackInfo callbackInfo) {
        SplashUtils.reset();
        SplashUtils.init();
        client.getTextureManager().registerTexture(LOGO, new SplashUtils(MATHAX_LOGO));
        //TODO: Fix
        //client.getTextureManager().registerTexture(LOGO, new ResourceTexture(new Identifier("mathaxlegacy", "splash/splash.png")));
        callbackInfo.cancel();
    }

    @Mutable
    @Shadow
    @Final
    private static IntSupplier BRAND_ARGB;

    // Background
    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void adjustBg(CallbackInfo ci) {
        BRAND_ARGB = () -> 0x1e1e2d;
    }

    // Progress bar
    /** Changes the progress bar border color and draws its background */
    @Redirect(method = "renderProgressBar",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/BackgroundHelper$ColorMixer;getArgb(IIII)I"))
    private int progressBarBorderProxy(int a, int r, int g, int b, MatrixStack matrices, int x1, int y1, int x2, int y2, float opacity) {

        // Bar background
        DrawableHelper.fill(
            matrices, x1 + 1, y1 + 1, x2 - 1, y2 - 1,
            withAlpha(0x1e1e2d, a)
        );

        // Bar border
        return withAlpha(0xe64c65, a);
    }

    /** Modifies the bar color */
    @ModifyArg(method = "renderProgressBar",
        at = @At(value = "INVOKE", target = FILL_DESC, ordinal = 0), index = 5)
    private int adjustBarColor(int color) {
        return 0x1e1e2d | color;
    }

    /** Calls {@link PreviewSplashOverlay#onDone()} when the screen disappears */
    @Inject(method = "render",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setOverlay(Lnet/minecraft/client/gui/screen/Overlay;)V"))
    private void onSetOverlay(CallbackInfo info) {
        if ((Object) this instanceof PreviewSplashOverlay previewScreen) {
            // Casting because SplashOverlayMixin doesn't extend PreviewSplashOverlay
            previewScreen.onDone();
        }
    }

    @ModifyConstant(method = "render", constant = @Constant(floatValue = 500))
    private float getFadeInTime(float old) {
        return 0;
    }

    @ModifyConstant(method = "render", constant = @Constant(floatValue = 1000))
    private float getFadeOutTime(float old) {
        return 1500;
    }
}
