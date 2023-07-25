package envy.client.utils.player;

import envy.client.Envy;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class MoveHelper {

    public static boolean hasMovement() {
        final Vec3d playerMovement = Envy.mc.player.getVelocity();
        return playerMovement.getX() != 0 || playerMovement.getY() != 0 || playerMovement.getZ() != 0;
    }

    public static double motionY(final double motionY) {
        final Vec3d vec3d = Envy.mc.player.getVelocity();
        Envy.mc.player.setVelocity(vec3d.x, motionY, vec3d.z);
        return motionY;
    }

    public static double motionYPlus(final double motionY) {
        final Vec3d vec3d = Envy.mc.player.getVelocity();
        Envy.mc.player.setVelocity(vec3d.x, vec3d.y + motionY, vec3d.z);
        return motionY;
    }

    public static double getDistanceToGround(Entity entity) {
        final double playerX = Envy.mc.player.getX();
        final int playerHeight = (int) Math.floor(Envy.mc.player.getY());
        final double playerZ = Envy.mc.player.getZ();

        for (int height = playerHeight; height > 0; height--) {
            final BlockPos checkPosition = new BlockPos(playerX, height, playerZ);

            // Check if the block is solid
            if (!Envy.mc.world.isAir(checkPosition)) {
                return playerHeight - height;
            }
        }
        return 0;
    }
}
