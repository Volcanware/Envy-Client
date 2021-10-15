package mathax.legacy.client.mixin;

import mathax.legacy.client.MatHaxLegacy;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Random;

@Mixin(SplashTextResourceSupplier.class)
public class SplashTextResourceSupplierMixin {
    private final Random random = new Random();

    private final List<String> mathaxSplashes = MatHaxLegacy.getMatHaxSplashes();

    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    private void onApply(CallbackInfoReturnable<String> info) {
        info.setReturnValue(mathaxSplashes.get(random.nextInt(mathaxSplashes.size())));
    }
}
