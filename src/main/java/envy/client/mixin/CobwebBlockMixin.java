package envy.client.mixin;

import envy.client.Envy;
import envy.client.events.entity.player.CobwebEntityCollisionEvent;
import net.minecraft.block.BlockState;
import net.minecraft.block.CobwebBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CobwebBlock.class)
public class CobwebBlockMixin {
    @Inject(method = "onEntityCollision", at = @At("HEAD"), cancellable = true)
    private void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo info) {
        CobwebEntityCollisionEvent event = Envy.EVENT_BUS.post(CobwebEntityCollisionEvent.get(state, pos));

        if (event.isCancelled()) info.cancel();
    }
}
