package envy.client.mixin;

import envy.client.Envy;
import envy.client.events.world.BlockUpdateEvent;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldChunk.class)
public class WorldChunkMixin {
    @Shadow @Final World world;

    @Inject(method = "setBlockState", at = @At("TAIL"))
    private void onSetBlockState(BlockPos pos, BlockState state, boolean moved, CallbackInfoReturnable<BlockState> info) {
        if (world.isClient) Envy.EVENT_BUS.post(BlockUpdateEvent.get(pos, info.getReturnValue(), state));
    }
}
