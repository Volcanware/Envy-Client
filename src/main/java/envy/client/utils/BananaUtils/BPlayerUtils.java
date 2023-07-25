package envy.client.utils.BananaUtils;

import envy.client.Envy;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class BPlayerUtils {
    public static double distanceFromEye(Entity entity) {
        double feet = distanceFromEye(entity.getX(), entity.getY(), entity.getZ());
        double head = distanceFromEye(entity.getX(), entity.getY() + entity.getHeight(), entity.getZ());
        return Math.min(head, feet);
    }

    public static double distanceFromEye(BlockPos blockPos) {
        return distanceFromEye(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static double distanceFromEye(Vec3d vec3d) {
        return distanceFromEye(vec3d.getX(), vec3d.getY(), vec3d.getZ());
    }

    public static double distanceFromEye(double x, double y, double z) {
        double f = (Envy.mc.player.getX() - x);
        double g = (Envy.mc.player.getY() + Envy.mc.player.getEyeHeight(Envy.mc.player.getPose()) - y);
        double h = (Envy.mc.player.getZ() - z);
        return Math.sqrt(f * f + g * g + h * h);
    }

    public static double[] directionSpeed(float speed) {
        float forward = Envy.mc.player.input.movementForward;
        float side = Envy.mc.player.input.movementSideways;
        float yaw = Envy.mc.player.prevYaw + (Envy.mc.player.getYaw() - Envy.mc.player.prevYaw);

        if (forward != 0.0F) {
            if (side > 0.0F) {
                yaw += ((forward > 0.0F) ? -45 : 45);
            } else if (side < 0.0F) {
                yaw += ((forward > 0.0F) ? 45 : -45);
            }

            side = 0.0F;

            if (forward > 0.0F) {
                forward = 1.0F;
            } else if (forward < 0.0F) {
                forward = -1.0F;
            }
        }

        double sin = Math.sin(Math.toRadians(yaw + 90.0F));
        double cos = Math.cos(Math.toRadians(yaw + 90.0F));
        double dx = forward * speed * cos + side * speed * sin;
        double dz = forward * speed * sin - side * speed * cos;

        return new double[]{dx, dz};
    }

    public static Direction direction(float yaw) {
        yaw = yaw % 360;
        if (yaw < 0) yaw += 360;

        if (yaw >= 315 || yaw < 45) return Direction.SOUTH;
        else if (yaw >= 45 && yaw < 135) return Direction.WEST;
        else if (yaw >= 135 && yaw < 225) return Direction.NORTH;
        else if (yaw >= 225 && yaw < 315) return Direction.EAST;

        return Direction.SOUTH;
    }
}

