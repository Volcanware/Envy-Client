package mathax.client.mixin.sodium;

import mathax.client.systems.modules.Modules;
import mathax.client.systems.modules.render.Xray;
import me.jellysquid.mods.sodium.client.render.occlusion.BlockOcclusionCache;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockOcclusionCache.class, remap = false)
public class SodiumBlockOcclusionCacheMixin {
    @Inject(method = "shouldDrawSide", at = @At("RETURN"), cancellable = true)
    private void shouldDrawSide(BlockState state, BlockView view, BlockPos pos, Direction facing, CallbackInfoReturnable<Boolean> info) {
        Xray xray = Modules.get().get(Xray.class);

        if (xray.isActive()) info.setReturnValue(xray.modifyDrawSide(state, view, pos, facing, info.getReturnValueZ()));
    }
}
