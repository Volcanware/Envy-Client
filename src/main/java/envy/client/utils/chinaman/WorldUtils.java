package envy.client.utils.chinaman;

import envy.client.Envy;
import envy.client.utils.player.FindItemResult;
import envy.client.utils.player.InvUtils;
import envy.client.utils.player.Rotations;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class WorldUtils {
    public static List<BlockPos> getSphere(BlockPos centerPos, int radius, int height) {
        ArrayList<BlockPos> blocks = new ArrayList<>();

        for (int i = centerPos.getX() - radius; i < centerPos.getX() + radius; i++) {
            for (int j = centerPos.getY() - height; j < centerPos.getY() + height; j++) {
                for (int k = centerPos.getZ() - radius; k < centerPos.getZ() + radius; k++) {
                    BlockPos pos = new BlockPos(i, j, k);
                    if (distanceBetween(centerPos, pos) <= radius && !blocks.contains(pos)) blocks.add(pos);
                }
            }
        }

        return blocks;
    }

    public static double distanceBetween(BlockPos pos1, BlockPos pos2) {
        double d = pos1.getX() - pos2.getX();
        double e = pos1.getY() - pos2.getY();
        double f = pos1.getZ() - pos2.getZ();
        return MathHelper.sqrt((float) (d * d + e * e + f * f));
    }

    public static boolean interact(BlockPos pos, FindItemResult findItemResult, boolean rotate) {
        if (!findItemResult.found()) return false;
        Runnable action = () -> {
            boolean wasSneaking = Envy.mc.player.input.sneaking;
            Envy.mc.player.input.sneaking = false;
            InvUtils.swap(findItemResult.slot(), true);
            Envy.mc.interactionManager.interactBlock(Envy.mc.player, Hand.MAIN_HAND, new BlockHitResult(Vec3d.ofCenter(pos), Direction.UP, pos, false));
            Envy.mc.player.swingHand(Hand.MAIN_HAND);
            InvUtils.swapBack();
            Envy.mc.player.input.sneaking = wasSneaking;
        };
        if (rotate) Rotations.rotate(Rotations.getYaw(pos), Rotations.getPitch(pos), -100, action);
        else action.run();
        return true;
    }
}
