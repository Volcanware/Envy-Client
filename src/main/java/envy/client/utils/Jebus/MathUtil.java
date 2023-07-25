package envy.client.utils.Jebus;

import envy.client.Envy;
import envy.client.utils.entity.EntityUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.util.concurrent.TimeUnit;

public class MathUtil {

    public static int intToTicks(int i) {
        return i * 20;
    }
    public static int ticksToInt(int i) {
        return i / 20;
    }


    public static double roundDouble(double d) {return Math.ceil(d);}

    public static long msPassed(Long start) {
        return System.currentTimeMillis() - start;
    }
    public static long secondsPassed(Long start) { return msToSeconds(msToSeconds(start));}
    public static long now() {return System.currentTimeMillis();}

    public static String timeElapsed(Long start) {return DurationFormatUtils.formatDuration(System.currentTimeMillis() - start, "HH:mm:ss");}
    public static String hoursElapsed(Long start) {return DurationFormatUtils.formatDuration(System.currentTimeMillis() - start, "HH");}
    public static String minutesElapsed(Long start) {return DurationFormatUtils.formatDuration(System.currentTimeMillis() - start, "mm");}
    public static String secondsElapsed(Long start) {return DurationFormatUtils.formatDuration(System.currentTimeMillis() - start, "ss");}
    public static String millisElapsed(Long start) {return Math.round(MathUtil.msPassed(start) * 100.0) / 100.0 + "ms";}

    public static long secondsToMS(int seconds) {return TimeUnit.SECONDS.toMillis(seconds);}

    public static long msToSeconds(long ms) {return TimeUnit.MILLISECONDS.toSeconds(ms);}
    public static int msToTicks(long ms) {return intToTicks((int) msToSeconds(ms));}

    public static Vec3d getVelocity(PlayerEntity player) {
        return player.getVelocity();
    }

    public static BlockPos offsetByVelocity(BlockPos pos, PlayerEntity player) {
        if (pos == null || player == null) return null;
        Vec3d velocity = getVelocity(player);
        return pos.add((int)velocity.x, (int)velocity.y, (int)velocity.z);
    }

    public static BlockPos generatePredict(BlockPos pos, PlayerEntity player, int ticks) {
        if (pos == null || player == null) return null;
        Vec3d velocity = getVelocity(player);
        Vec3i v = new Vec3i((int)velocity.x * ticks, (int)velocity.y * ticks, (int)velocity.z * ticks);
        return pos.add(v);
    }

    public static boolean intersects(Box box) { return EntityUtils.intersectsWithEntity(box, entity -> !entity.isSpectator());}
    public static boolean intersects(BlockPos pos) { return intersects(new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ())); }
    public static boolean intersectsAbove(BlockPos pos) { return intersects(new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY() + 1, pos.getZ())); }

    public static double[] directionSpeed(float speed) {
        float forward = Envy.mc.player.input.movementForward;
        float side = Envy.mc.player.input.movementSideways;
        float yaw = Envy.mc.player.prevYaw + (Envy.mc.player.getYaw() - Envy.mc.player.prevYaw);

        if (forward != 0.0f) {
            if (side > 0.0f) {
                yaw += ((forward > 0.0f) ? -45 : 45);
            } else if (side < 0.0f) {
                yaw += ((forward > 0.0f) ? 45 : -45);
            }
            side = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            } else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }

        final double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        final double posX = forward * speed * cos + side * speed * sin;
        final double posZ = forward * speed * sin - side * speed * cos;

        return new double[] {posX, posZ};
    }



}
