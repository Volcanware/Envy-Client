package envy.client.utils.player;

import envy.client.Envy;
import envy.client.eventbus.EventHandler;
import envy.client.events.entity.player.PlayerMoveEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

public class PathFinder {
    private final static int PATH_AHEAD = 3;
    private final static int QUAD_1 = 1, QUAD_2 = 2, SOUTH = 0, NORTH = 180;
    private final ArrayList<PathBlock> path = new ArrayList<>(PATH_AHEAD);
    private Entity target;
    private PathBlock currentPathBlock;

    public PathBlock getNextPathBlock() {
        PathBlock nextBlock = new PathBlock(new BlockPos(getNextStraightPos()));
        if (isSolidFloor(nextBlock.blockPos) && isAirAbove(nextBlock.blockPos)) return nextBlock;
        else if (!isSolidFloor(nextBlock.blockPos) && isAirAbove(nextBlock.blockPos)) {
            int drop = getDrop(nextBlock.blockPos);
            if (drop < 3) nextBlock = new PathBlock(new BlockPos(nextBlock.blockPos.getX(), nextBlock.blockPos.getY() - drop, nextBlock.blockPos.getZ()));
        }

        return nextBlock;
    }

    public int getDrop(BlockPos pos) {
        int drop = 0;
        while (!isSolidFloor(pos) && drop < 3) {
            drop++;
            pos = new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ());
        }

        return drop;
    }

    public boolean isAirAbove(BlockPos blockPos) {
        if (!getBlockStateAtPos(blockPos.getX(), blockPos.getY(), blockPos.getZ()).isAir()) return false;
        return getBlockStateAtPos(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ()).isAir();
    }

    public Vec3d getNextStraightPos() {
        Vec3d nextPos = new Vec3d(Envy.mc.player.getX(), Envy.mc.player.getY(), Envy.mc.player.getZ());
        double multiplier = 1.0;
        while (nextPos == Envy.mc.player.getPos()) {
            nextPos = new Vec3d((int) (Envy.mc.player.getX() + multiplier * Math.cos(Math.toRadians(Envy.mc.player.getYaw()))), (int) (Envy.mc.player.getY()), (int) (Envy.mc.player.getZ() + multiplier * Math.sin(Math.toRadians(Envy.mc.player.getYaw()))));
            multiplier += .1;
        }

        return nextPos;
    }

    public int getYawToTarget() {
        if (target == null || Envy.mc.player == null) return Integer.MAX_VALUE;
        Vec3d tPos = target.getPos();
        Vec3d pPos = Envy.mc.player.getPos();
        int yaw = 0;
        int direction = getDirection();
        double tan = (tPos.z - pPos.z) / (tPos.x - pPos.x);
        if (direction == QUAD_1) yaw = (int) (Math.PI / 2 - Math.atan(tan));
        else if (direction == QUAD_2) yaw = (int) (-1 * Math.PI / 2 - Math.atan(tan));
        else return direction;

        return yaw;
    }

    public int getDirection() {
        if (target == null || Envy.mc.player == null) return 0;
        Vec3d targetPos = target.getPos();
        Vec3d playerPos = Envy.mc.player.getPos();
        if (targetPos.x == playerPos.x && targetPos.z > playerPos.z) return SOUTH;
        if (targetPos.x == playerPos.x && targetPos.z < playerPos.z) return NORTH;
        if (targetPos.x < playerPos.x) return QUAD_1;
        if (targetPos.x > playerPos.x) return QUAD_2;

        return 0;
    }

    public BlockState getBlockStateAtPos(BlockPos pos) {
        if (Envy.mc.world != null) return Envy.mc.world.getBlockState(pos);
        return null;
    }

    public BlockState getBlockStateAtPos(int x, int y, int z) {
        if (Envy.mc.world != null) return Envy.mc.world.getBlockState(new BlockPos(x, y, z));
        return null;
    }

    public Block getBlockAtPos(BlockPos pos) {
        if (Envy.mc.world != null) return Envy.mc.world.getBlockState(pos).getBlock();
        return null;
    }

    public boolean isSolidFloor(BlockPos blockPos) {
        return isAir(getBlockAtPos(blockPos));
    }

    public boolean isAir(Block block) {
        return block == Blocks.AIR;
    }

    public boolean isWater(Block block) {
        return block == Blocks.WATER;
    }

    public void lookAtDestination(PathBlock pathBlock) {
        if (Envy.mc.player != null) Envy.mc.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(pathBlock.blockPos.getX(), pathBlock.blockPos.getY() + Envy.mc.player.getStandingEyeHeight(), pathBlock.blockPos.getZ()));
    }

    @EventHandler
    private void moveEventListener(PlayerMoveEvent event) {
        if (target != null && Envy.mc.player != null) {
            if (Envy.mc.player.distanceTo(target) > 3) {
                if (currentPathBlock == null) currentPathBlock = getNextPathBlock();
                if (Envy.mc.player.getPos().distanceTo(new Vec3d(currentPathBlock.blockPos.getX(), currentPathBlock.blockPos.getY(), currentPathBlock.blockPos.getZ())) < .1) currentPathBlock = getNextPathBlock();
                lookAtDestination(currentPathBlock);
                if (!Envy.mc.options.forwardKey.isPressed()) Envy.mc.options.forwardKey.setPressed(true);
            } else {
                if (Envy.mc.options.forwardKey.isPressed()) Envy.mc.options.forwardKey.setPressed(false);
                path.clear();
                currentPathBlock = null;
            }
        }
    }

    public void initiate(Entity entity) {
        target = entity;
        if (target != null) currentPathBlock = getNextPathBlock();
        Envy.EVENT_BUS.subscribe(this);
    }

    public void disable() {
        target = null;
        path.clear();
        if (Envy.mc.options.forwardKey.isPressed()) Envy.mc.options.forwardKey.setPressed(false);
        Envy.EVENT_BUS.unsubscribe(this);
    }

    public class PathBlock {
        public final Block block;
        public final BlockPos blockPos;
        public final BlockState blockState;
        public double yaw;

        public PathBlock(Block b, BlockPos pos, BlockState state) {
            block = b;
            blockPos = pos;
            blockState = state;
        }

        public PathBlock(Block b, BlockPos pos) {
            block = b;
            blockPos = pos;
            blockState = getBlockStateAtPos(blockPos);
        }

        public PathBlock(BlockPos pos) {
            blockPos = pos;
            block = getBlockAtPos(pos);
            blockState = getBlockStateAtPos(blockPos);
        }
    }
}
