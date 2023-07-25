package envy.client.mixin;

import envy.client.Envy;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/** This mixin is only active when fabric-resource-loader mod is not present */
@Mixin(ReloadableResourceManagerImpl.class)
public class ReloadableResourceManagerImplMixin {
    @Inject(method = "getResource", at = @At("HEAD"), cancellable = true)
    private void onGetResource(Identifier id, CallbackInfoReturnable<Optional<Resource>> info) {
        if (id.getNamespace().equals("envy")) info.setReturnValue(Optional.of(new Resource("envy", () -> Envy.class.getResourceAsStream("/assets/envy/" + id.getPath()))));
    }
}
