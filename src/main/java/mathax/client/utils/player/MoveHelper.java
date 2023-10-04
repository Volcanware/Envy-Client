package mathax.client.utils.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;

import static mathax.client.MatHax.mc;

public class MoveHelper {
    private static final Map<LivingEntity, Float> airStrafeSpeedMultipliers = new HashMap<>();

    public static boolean hasMovement() {
        final Vec3d playerMovement = mc.player.getVelocity();
        return playerMovement.getX() != 0 || playerMovement.getY() != 0 || playerMovement.getZ() != 0;
    }

    public static double motionY(final double motionY) {
        final Vec3d vec3d = mc.player.getVelocity();
        mc.player.setVelocity(vec3d.x, motionY, vec3d.z);
        return motionY;
    }

    public static double motionYPlus(final double motionY) {
        final Vec3d vec3d = mc.player.getVelocity();
        mc.player.setVelocity(vec3d.x, vec3d.y + motionY, vec3d.z);
        return motionY;
    }

    public static double getDistanceToGround(Entity entity) {
        final double playerX = mc.player.getX();
        final int playerHeight = (int) Math.floor(mc.player.getY());
        final double playerZ = mc.player.getZ();

        for (int height = playerHeight; height > 0; height--) {
            final BlockPos checkPosition = BlockPos.ofFloored(playerX, height, playerZ);

            // Check if the block is solid
            if (!mc.world.isAir(checkPosition)) {
                return playerHeight - height;
            }
        }
        return 0;
    }

    public static float getAirStrafeSpeedMultiplier(LivingEntity entity) {
        return airStrafeSpeedMultipliers.containsKey(entity) ? airStrafeSpeedMultipliers.get(entity) : 1;
    }

    public static void setAirStrafeSpeedMultiplier(LivingEntity entity, float multiplier) {
        airStrafeSpeedMultipliers.put(entity, multiplier);
    }
    public static void setAirStrafeSpeed(LivingEntity entity, float value) {
        setAirStrafeSpeedMultiplier(entity, value / 0.02F); // 0.02F is the default value
    }
}
