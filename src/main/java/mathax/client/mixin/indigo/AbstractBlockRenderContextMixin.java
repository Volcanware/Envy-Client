package mathax.client.mixin.indigo;

import link.infra.indium.renderer.mesh.MutableQuadViewImpl;
import link.infra.indium.renderer.render.AbstractBlockRenderContext;
import link.infra.indium.renderer.render.BlockRenderInfo;
import mathax.client.systems.modules.render.Xray;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AbstractBlockRenderContext.class, remap = false)
public class AbstractBlockRenderContextMixin {
    @Final @Shadow(remap = false) protected BlockRenderInfo blockInfo;

    @Inject(method = "renderQuad", at = @At(value = "INVOKE", target = "Llink/infra/indium/renderer/render/AbstractBlockRenderContext;bufferQuad(Llink/infra/indium/renderer/mesh/MutableQuadViewImpl;Lme/jellysquid/mods/sodium/client/render/chunk/terrain/material/Material;)V"), cancellable = true)
    private void onBufferQuad(MutableQuadViewImpl quad, boolean isVanilla, CallbackInfo ci) {
        int alpha = Xray.getAlpha(blockInfo.blockState, blockInfo.blockPos);

        if (alpha == 0) ci.cancel();
        else if (alpha != -1) {
            for (int i = 0; i < 4; i++) {
                quad.color(i, rewriteQuadAlpha(quad.color(i), alpha));
            }
        }
    }

    @Unique
    private int rewriteQuadAlpha(int color, int alpha) {
        return ((alpha & 0xFF) << 24) | (color & 0x00FFFFFF);
    }
}
