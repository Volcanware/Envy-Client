package mathax.legacy.client.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public final class DisableVanillaTitle{
    @Inject(method = "updateWindowTitle()V", at = @At("HEAD"), cancellable = true)
    private void updateTitle(final CallbackInfo info){
        info.cancel();
    }
}
