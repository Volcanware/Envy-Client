package mathax.legacy.client.mixin.canvas;

import grondag.canvas.render.world.CanvasWorldRenderer;
import mathax.legacy.client.systems.modules.Modules;
import mathax.legacy.client.systems.modules.render.BlockSelection;
import mathax.legacy.client.systems.modules.render.Fullbright;
import mathax.legacy.client.utils.Utils;
import mathax.legacy.client.utils.render.Outlines;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CanvasWorldRenderer.class, remap = false)
public class CanvasWorldRendererMixin {
    @ModifyVariable(method = "renderWorld", at = @At("LOAD"), name = "blockOutlines")
    private boolean renderWorld_blockOutlines(boolean blockOutlines) {
        if (Modules.get().isActive(BlockSelection.class)) return false;
        return blockOutlines;
    }

    @Inject(method = "renderWorld", at = @At("HEAD"))
    private void onRenderHead(MatrixStack viewMatrixStack, float tickDelta, long frameStartNanos, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f projectionMatrix, CallbackInfo info) {
        Utils.minimumLightLevel = Modules.get().get(Fullbright.class).getMinimumLightLevel();
        Outlines.beginRender();
    }

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BufferBuilderStorage;getOutlineVertexConsumers()Lnet/minecraft/client/render/OutlineVertexConsumerProvider;", shift = At.Shift.AFTER))
    private void onRender(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo info) {
        Outlines.endRender();
    }
}
