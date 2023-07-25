package envy.client.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import envy.client.Envy;
import envy.client.mixininterface.IMatrix4f;
import envy.client.systems.modules.Modules;
import envy.client.systems.modules.render.NoBob;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class RenderUtils {
    public static Vec3d center;

    // Items
    public static void drawItem(ItemStack itemStack, int x, int y, double scale, boolean overlay) {
        //RenderSystem.disableDepthTest();

        MatrixStack matrices = RenderSystem.getModelViewStack();

        matrices.push();
        matrices.scale((float) scale, (float) scale, 1);

        Envy.mc.getItemRenderer().renderGuiItemIcon(itemStack, (int) (x / scale), (int) (y / scale));
        if (overlay) Envy.mc.getItemRenderer().renderGuiItemOverlay(Envy.mc.textRenderer, itemStack, (int) (x / scale), (int) (y / scale), null);

        matrices.pop();
        //RenderSystem.enableDepthTest();
    }

    public static void drawItem(ItemStack itemStack, int x, int y, boolean overlay) {
        drawItem(itemStack, x, y, 1, overlay);
    }

    public static void updateScreenCenter() {
        Vec3d pos = new Vec3d(0, 0, 1);

        if (Envy.mc.options.getBobView().getValue()) {
            if (!Modules.get().isActive(NoBob.class)) {
                MatrixStack bobViewMatrices = new MatrixStack();

                bobView(bobViewMatrices);
                bobViewMatrices.peek().getPositionMatrix().invert();

                pos = ((IMatrix4f) (Object) bobViewMatrices.peek().getPositionMatrix()).mul(pos);
            }
        }

        center = new Vec3d(pos.x, -pos.y, pos.z).rotateX(-(float) Math.toRadians(Envy.mc.gameRenderer.getCamera().getPitch())).rotateY(-(float) Math.toRadians(Envy.mc.gameRenderer.getCamera().getYaw())).add(Envy.mc.gameRenderer.getCamera().getPos());
    }

    private static void bobView(MatrixStack matrices) {
        Entity cameraEntity = Envy.mc.getCameraEntity();

        if (cameraEntity instanceof PlayerEntity playerEntity) {
            float f = Envy.mc.getTickDelta();
            float g = playerEntity.horizontalSpeed - playerEntity.prevHorizontalSpeed;
            float h = -(playerEntity.horizontalSpeed + g * f);
            float i = MathHelper.lerp(f, playerEntity.prevStrideDistance, playerEntity.strideDistance);

            matrices.translate(-(MathHelper.sin(h * 3.1415927f) * i * 0.5), -(-Math.abs(MathHelper.cos(h * 3.1415927f) * i)), 0);
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(MathHelper.sin(h * 3.1415927f) * i * 3));
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(Math.abs(MathHelper.cos(h * 3.1415927f - 0.2f) * i) * 5));
        }
    }
}
