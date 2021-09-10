package mathax.legacy.client.mixin;

import mathax.legacy.client.utils.Utils;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Mixin(SplashTextResourceSupplier.class)
public class SplashTextResourceSupplierMixin {
    private final Random random = new Random();

    private final List<String> mathaxSplashes = getMatHaxSplashes();

    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    private void onApply(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(mathaxSplashes.get(random.nextInt(mathaxSplashes.size())));
    }

    private static List<String> getMatHaxSplashes() {
        return Arrays.asList(

            // SPLASHES
            Formatting.RED + "MatHax on top!",
            Formatting.GRAY + "Matejko06 " + Formatting.RED + "based god",
            Formatting.GRAY + "GeekieCoder " + Formatting.RED + "based god",
            Formatting.RED + "MatHaxClient.xyz",
            Formatting.RED + "MatHaxClient.xyz/Discord",

            // MEME SPLASHES
            Formatting.YELLOW + "cope",
            Formatting.YELLOW + "IntelliJ IDEa",
            Formatting.YELLOW + "I <3 nns",
            Formatting.YELLOW + "haha 69",
            Formatting.YELLOW + "420 XDDDDDD",
            Formatting.YELLOW + "ayy",
            Formatting.YELLOW + "too ez",

            // COPER [Taken from API when connected to internet, else using username of logged player.]
            Formatting.GRAY + Utils.getCoper() + Formatting.YELLOW + " coping hard",
            Formatting.GRAY + Utils.getCoper() + Formatting.YELLOW + "? More like " + Formatting.GRAY + Utils.getCoperReplacement()
        );
    }
}
