package mathax.client.mixin;

import mathax.client.systems.modules.Modules;
import mathax.client.systems.modules.misc.NoSignatures;
import net.minecraft.client.util.ProfileKeys;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.encryption.Signer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ProfileKeys.class)
public class ProfileKeysMixin {
    @Inject(method = "getSigner", at = @At("HEAD"), cancellable = true)
    private void onGetSigner(CallbackInfoReturnable<Signer> infoReturnable) {
        if (Modules.get().isActive(NoSignatures.class)) infoReturnable.setReturnValue(null);
    }

    @Inject(method = "getPublicKey", at = @At("HEAD"), cancellable = true)
    private void onGetPublicKey(CallbackInfoReturnable<Optional<PlayerPublicKey>> infoReturnable) {
        if (Modules.get().isActive(NoSignatures.class)) infoReturnable.setReturnValue(Optional.empty());
    }

    @Inject(method = "getPublicKeyData", at = @At("HEAD"), cancellable = true)
    private void onGetPublicKeyData(CallbackInfoReturnable<Optional<PlayerPublicKey.PublicKeyData>> infoReturnable) {
        if (Modules.get().isActive(NoSignatures.class)) infoReturnable.setReturnValue(Optional.empty());
    }
}
