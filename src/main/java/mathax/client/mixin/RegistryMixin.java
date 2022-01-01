package mathax.client.mixin;

import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Supplier;

@Mixin(Registry.class)
public class RegistryMixin<T> {
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/Bootstrap;ensureBootstrapped(Ljava/util/function/Supplier;)V"))
    private void init(Supplier<String> callerGetter) {}
}
