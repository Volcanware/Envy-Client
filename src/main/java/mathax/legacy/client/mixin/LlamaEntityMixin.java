package mathax.legacy.client.mixin;

import mathax.legacy.client.systems.modules.movement.EntityControl;
import mathax.legacy.client.systems.modules.Modules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.passive.LlamaEntity;


@Mixin(LlamaEntity.class)
public class LlamaEntityMixin {
    @Inject(method = "canBeControlledByRider", at = @At("HEAD"), cancellable = true)
    public void canBeControlledByRider(CallbackInfoReturnable<Boolean> ci) {
        if (Modules.get().get(EntityControl.class).isActive()) ci.setReturnValue(true);
    }
}
