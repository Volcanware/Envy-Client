package envy.client.utils.algorithms.extra;

import envy.client.Envy;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

import static envy.client.utils.player.PlayerUtils.isMoving;

public class MovementUtils {
	public static double getSpeed() {
		return Math.sqrt(Envy.mc.player.getVelocity().x * Envy.mc.player.getVelocity().x + Envy.mc.player.getVelocity().z * Envy.mc.player.getVelocity().z);
	}
	public static void strafe(float speed) {
		double yaw = direction();
		double sin = -Math.sin(yaw) * speed;
		double cos = Math.cos(yaw) * speed;
		Envy.mc.player.getVelocity().add(cos, 0, sin);
	}

    public static void VulcanMoveStrafe(float speed) {
        if (!isMoving()) {
            return;
        }
        double direction = getDirection();
        double x = -Math.sin(direction) * speed;
        double z = Math.cos(direction) * speed;

        Vec3d motion = new Vec3d(x, Envy.mc.player.getVelocity().y, z);
        Envy.mc.player.setVelocity(motion);
    }

    public static void Vulcanstrafe() {
        strafe(getSpeed());
    }

    public static void strafe(double speed) {
		double yaw = direction();
		double sin = -Math.sin(yaw) * speed;
		double cos = Math.cos(yaw) * speed;
		Envy.mc.player.getVelocity().add(cos, 0, sin);
	}
	public static double direction() {
		float yaw = Envy.mc.player.getYaw();
		if (Envy.mc.player.input.movementForward < 0) yaw += 180;
		float forward = 1;
		if (Envy.mc.player.input.movementForward < 0) forward -= 0.5; else if (Envy.mc.player.input.movementForward > 0) forward += 0.5;
		if (Envy.mc.player.input.movementSideways > 0) yaw -= 90 * forward;
		if (Envy.mc.player.input.movementSideways < 0) yaw += 90 * forward;
		return Math.toRadians(yaw);
	}
    private static double getDirection() {
        double rotationYaw = MinecraftClient.getInstance().player.getYaw();
        if (MinecraftClient.getInstance().player.input.movementForward < 0) {
            rotationYaw += 180;
        }
        double forward = 1;
        if (MinecraftClient.getInstance().player.input.movementForward < 0) {
            forward = -0.5;
        } else if (MinecraftClient.getInstance().player.input.movementForward > 0) {
            forward = 0.5;
        }
        if (MinecraftClient.getInstance().player.input.movementSideways > 0) {
            rotationYaw -= 90 * forward;
        }
        if (MinecraftClient.getInstance().player.input.movementSideways < 0) {
            rotationYaw += 90 * forward;
        }
        return Math.toRadians(rotationYaw);
    }
}
