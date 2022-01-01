package mathax.client.mixin.sodium;

import mathax.client.systems.modules.Modules;
import mathax.client.systems.modules.world.Ambience;
import me.jellysquid.mods.sodium.client.model.light.LightPipeline;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuadColorProvider;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuadView;
import me.jellysquid.mods.sodium.client.render.pipeline.FluidRenderer;
import me.jellysquid.mods.sodium.client.util.color.ColorARGB;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(value = FluidRenderer.class, remap = false)
public class SodiumFluidRendererMixin {
    @Final
    @Shadow(remap = false)
    private int[] quadColors;

    /**
     * @author Walaryne
     */
    @Inject(method = "calculateQuadColors", at = @At("TAIL"), cancellable = true, remap = false)
    private void onCalculateQuadColors(ModelQuadView quad, BlockRenderView world, BlockPos pos, LightPipeline lighter, Direction dir, float brightness, ModelQuadColorProvider<FluidState> handler, FluidState fluidState, CallbackInfo info) {
        Ambience ambience = Modules.get().get(Ambience.class);

        if (ambience.isActive() && ambience.customLavaColor.get() && fluidState.isIn(FluidTags.LAVA)) Arrays.fill(quadColors, ColorARGB.toABGR(ambience.lavaColor.get().getPacked()));
    }
}
