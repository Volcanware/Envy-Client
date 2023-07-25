package envy.client.utils.player;

import envy.client.Envy;
import envy.client.eventbus.EventHandler;
import envy.client.events.world.TickEvent;
import envy.client.utils.algorithms.extra.RotationUtils;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ShadyRotation {
    private static float pitchDifference;
    public static float yawDifference;
    private static int ticks = -1;
    private static int tickCounter = 0;
    private static Runnable callback = null;

    public static boolean running = false;

    private static boolean client = false;

    public static class Rotation {
        public float pitch;
        public float yaw;

        public Rotation(float pitch, float yaw) {
            this.pitch = pitch;
            this.yaw = yaw;
        }
    }


    private static double wrapAngleTo180(double angle) {
        return angle - Math.floor(angle / 360 + 0.5) * 360;
    }

    private static float wrapAngleTo180(float angle) {
        return (float) (angle - Math.floor(angle / 360 + 0.5) * 360);
    }

    public static Rotation getRotationToBlock(BlockPos block) {
        double diffX = block.getX() - Envy.mc.player.getPos().x + 0.5;
        double diffY = block.getY() - Envy.mc.player.getPos().y + 0.5 - Envy.mc.player.getEyeY();
        double diffZ = block.getZ() - Envy.mc.player.getPos().z + 0.5;
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float pitch = (float) -Math.atan2(dist, diffY);
        float yaw = (float) Math.atan2(diffZ, diffX);
        pitch = (float) wrapAngleTo180((pitch * 180F / Math.PI + 90)*-1);
        yaw = (float) wrapAngleTo180((yaw * 180 / Math.PI) - 90);

        return new Rotation(pitch, yaw);
    }

    public static Rotation getRotationToEntity(Entity entity) {
        double diffX = entity.getPos().x - Envy.mc.player.getPos().x;
        double diffY = entity.getPos().y + entity.getEyePos().y - Envy.mc.player.getPos().y - Envy.mc.player.getEyeY();
        double diffZ = entity.getPos().z - Envy.mc.player.getPos().z;
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float pitch = (float) -Math.atan2(dist, diffY);
        float yaw = (float) Math.atan2(diffZ, diffX);
        pitch = (float) wrapAngleTo180((pitch * 180F / Math.PI + 90)*-1);
        yaw = (float) wrapAngleTo180((yaw * 180 / Math.PI) - 90);

        return new Rotation(pitch, yaw);
    }

    public static Rotation vec3ToRotation(Vec3d vec) {
        double diffX = vec.x - Envy.mc.player.getPos().x;
        double diffY = vec.y - Envy.mc.player.getPos().y - Envy.mc.player.getEyeY();
        double diffZ = vec.z - Envy.mc.player.getPos().z;
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float pitch = (float) -Math.atan2(dist, diffY);
        float yaw = (float) Math.atan2(diffZ, diffX);
        pitch = (float) wrapAngleTo180((pitch * 180F / Math.PI + 90)*-1);
        yaw = (float) wrapAngleTo180((yaw * 180 / Math.PI) - 90);

        return new Rotation(pitch, yaw);
    }
    public static void smoothLook(Rotation rotation, int ticks, boolean client, Runnable callback) {
        ShadyRotation.client = client;
        if(ticks == 0) {
            look(rotation, client);
            callback.run();
            return;
        }

        ShadyRotation.callback = callback;

        pitchDifference = wrapAngleTo180(rotation.pitch - Envy.mc.player.getPitch());
        yawDifference = wrapAngleTo180(rotation.yaw - Envy.mc.player.getYaw());

        ShadyRotation.ticks = ticks * 20;
        ShadyRotation.tickCounter = 0;
    }

    public static void smoothLook(RotationUtils.Rotation rotation, int ticks, boolean client, Runnable callback) {
        ShadyRotation.client = client;
        if(ticks == 0) {
            look(new Rotation(rotation.getPitch(), rotation.getYaw()), client);
            callback.run();
            return;
        }

        ShadyRotation.callback = callback;
        if (client) {
            pitchDifference = wrapAngleTo180(rotation.getPitch() - Envy.mc.player.getPitch());
            yawDifference = wrapAngleTo180(rotation.getYaw() - Envy.mc.player.getYaw());
        } else {
            pitchDifference = wrapAngleTo180(rotation.getPitch() - Rotations.serverPitch);
            yawDifference = wrapAngleTo180(rotation.getYaw() - Rotations.serverYaw);
        }

        ShadyRotation.ticks = ticks * 20;
        ShadyRotation.tickCounter = 0;
    }

    public static void smartLook(Rotation rotation, int ticksPer180, boolean client, Runnable callback) {
        ShadyRotation.client = client;
        float rotationDifference = Math.max(
            Math.abs(rotation.pitch - Envy.mc.player.getPitch()),
            Math.abs(rotation.yaw - Envy.mc.player.getYaw())
        );
        smoothLook(rotation, (int) (rotationDifference / 180 * ticksPer180), client, callback);
    }

    public static void look(Rotation rotation, boolean client) {
        Rotations.serverYaw = rotation.yaw;
        Rotations.serverPitch = rotation.pitch;
        if (client) {
            Envy.mc.player.setPitch(rotation.pitch);
            Envy.mc.player.setYaw(rotation.yaw);
        }
    }

    public void Init() {
        Envy.EVENT_BUS.subscribe(this);
    }

    private void rotatorWorker() {
        if(Envy.mc.player == null) return;
        if(tickCounter < ticks) {
            running = true;
            if (client) {
                Envy.mc.player.setPitch(Envy.mc.player.getPitch() + pitchDifference / ticks);
                Envy.mc.player.setYaw(Envy.mc.player.getYaw() + yawDifference / ticks);
            }
            else {
                Rotations.setCamRotation(Rotations.serverYaw + yawDifference / ticks, Rotations.serverPitch + pitchDifference / ticks);
                //Rotations.serverYaw = (Rotations.serverYaw + yawDifference / ticks);
                //Rotations.serverPitch = (Rotations.serverPitch + pitchDifference / ticks);
            }
            tickCounter++;
        }
        else
        {
            running = false;
        }
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        rotatorWorker();
    }

    @EventHandler
    public void onTick2(TickEvent.Post event) {
        rotatorWorker();
    }
}

