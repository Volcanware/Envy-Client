package envy.client.utils.Jebus;

import envy.client.Envy;
import envy.client.utils.player.PlayerUtils;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.ArrayList;
import java.util.Comparator;

public class CityUtils {
    public static BlockPos getBreakPos(PlayerEntity target) {
        if (target == null) return null;

        ArrayList<BlockPos> blockPos = new ArrayList<>();
        BlockPos targetPos = target.getBlockPos();

        for (Direction direction : Direction.values()) {
            if (direction.equals(Direction.UP) || direction.equals(Direction.DOWN)) continue;

            if (Envy.mc.world.getBlockState(targetPos.offset(direction)).isOf(Blocks.OBSIDIAN)) blockPos.add(targetPos.offset(direction));
        }

        blockPos.sort(Comparator.comparingDouble(PlayerUtils::distanceTo));
        return blockPos.isEmpty() ? null : blockPos.get(0);
    }

    public static BlockPos getCrystalPos(BlockPos breakPos, boolean support) {
        if (breakPos == null) return null;
        ArrayList<BlockPos> blockPos = new ArrayList<>();

        for (Direction direction : Direction.values()) {
            if (direction.equals(Direction.UP) || direction.equals(Direction.DOWN)) continue;

            if (canCrystal(breakPos.offset(direction), support)) blockPos.add(breakPos.offset(direction));
        }

        blockPos.sort(Comparator.comparingDouble(PlayerUtils::distanceTo));
        return blockPos.isEmpty() ? null : blockPos.get(0).down();
    }

    private static boolean canCrystal(BlockPos blockPos, boolean support) {
        for (Entity entity : Envy.mc.world.getEntities()) {
            if (entity instanceof PlayerEntity || entity instanceof EndCrystalEntity || entity instanceof ItemEntity) {
                if (entity.getBlockPos().equals(blockPos)) return false;
            }
        }

        return Envy.mc.world.getBlockState(blockPos).isOf(Blocks.AIR) &&
            (support ? (Envy.mc.world.getBlockState(blockPos.down()).isOf(Blocks.AIR) || Envy.mc.world.getBlockState(blockPos.down()).isOf(Blocks.OBSIDIAN) || Envy.mc.world.getBlockState(blockPos.down()).isOf(Blocks.BEDROCK)) : (Envy.mc.world.getBlockState(blockPos.down()).isOf(Blocks.OBSIDIAN) || Envy.mc.world.getBlockState(blockPos.down()).isOf(Blocks.BEDROCK)));
    }

    public static Direction getDirection(BlockPos pos) {
        if (pos == null) return Direction.UP;
        Vec3d eyesPos = new Vec3d(Envy.mc.player.getX(), Envy.mc.player.getY() + Envy.mc.player.getEyeHeight(Envy.mc.player.getPose()), Envy.mc.player.getZ());        for (Direction direction : Direction.values()) {
            RaycastContext raycastContext = new RaycastContext(eyesPos, new Vec3d(pos.getX() + 0.5 + direction.getVector().getX() * 0.5,
                pos.getY() + 0.5 + direction.getVector().getY() * 0.5,
                pos.getZ() + 0.5 + direction.getVector().getZ() * 0.5), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, Envy.mc.player);
            BlockHitResult result = Envy.mc.world.raycast(raycastContext);
            if (result != null && result.getType() == HitResult.Type.BLOCK && result.getBlockPos().equals(pos)) {
                return direction;
            }
        }
        if ((double) pos.getY() > eyesPos.y) {
            return Direction.DOWN;
        }
        return Direction.UP;
    }


}
