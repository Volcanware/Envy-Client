package envy.client.mixin;

import net.minecraft.client.item.CompassAnglePredicateProvider;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CompassAnglePredicateProvider.class)
public class CompassAnglePredicateProviderMixin {
    /*@Redirect(method = "method_43213", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getBodyYaw()F"))
    private float callLivingEntityGetYaw(Entity entity) {
        if (Modules.get().isActive(Freecam.class)) return mc.gameRenderer.getCamera().getYaw();
        return entity.getYaw();
    }*/

    /*@Inject(method = "getAngleTo", at = @At("HEAD"), cancellable = true)
    private void onGetAngleToPos(Entity entity, long time, BlockPos pos, CallbackInfoReturnable<Float> info) {
        if (Modules.get().isActive(Freecam.class)) {
            Camera camera = mc.gameRenderer.getCamera();
            info.setReturnValue((float) Math.atan2(pos.getZ() - camera.getPos().z, pos.getX() - camera.getPos().x));
        }
    }*/
}
