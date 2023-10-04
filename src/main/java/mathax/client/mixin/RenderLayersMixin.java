package mathax.client.mixin;

import mathax.client.systems.modules.Modules;
import mathax.client.systems.modules.render.Xray;
import mathax.client.systems.modules.render.WallHack;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderLayers.class)
public class RenderLayersMixin {
    @Inject(method = "getBlockLayer", at = @At("HEAD"), cancellable = true)
    private static void onGetBlockLayer(BlockState state, CallbackInfoReturnable<RenderLayer> info) {
        if (Modules.get() == null) return;

        int alpha = Xray.getAlpha(state, null);
        if (alpha > 0 && alpha < 255) info.setReturnValue(RenderLayer.getTranslucent());
    }
}
