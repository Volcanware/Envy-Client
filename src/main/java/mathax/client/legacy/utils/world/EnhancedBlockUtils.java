package mathax.client.legacy.utils.world;

import mathax.client.legacy.MatHaxClientLegacy;
import mathax.client.legacy.bus.EventHandler;
import mathax.client.legacy.events.game.GameLeftEvent;
import mathax.client.legacy.mixininterface.IVec3d;
import mathax.client.legacy.utils.Utils;
import mathax.client.legacy.utils.player.Rotations;
import net.minecraft.block.*;
import net.minecraft.client.render.BlockBreakingInfo;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mathax.client.legacy.utils.Utils.mc;

public class EnhancedBlockUtils {
    static final boolean $assertionsDisabled;
    private static final ArrayList<BlockPos> blocks;
    private static final Vec3d hitPos;
    public static final Map<Integer, BlockBreakingInfo/*class_3191*/> breakingBlocks;

    public static boolean place(BlockPos blockPos, Hand hand, int n, boolean bl, int n2, boolean bl2, boolean bl3, boolean bl4, boolean bl5) {
        BlockPos blockPos1;
        Vec3d vec3d;
        if (n == -1 || !EnhancedBlockUtils.canPlace(blockPos, bl3)) {
            return false;
        }
        Direction direction = EnhancedBlockUtils.getPlaceSide(blockPos);
        Vec3d vec3d1 = vec3d = bl ? new Vec3d(0.0, 0.0, 0.0) : hitPos;
        if (direction == null) {
            direction = Direction.UP;
            blockPos1 = blockPos;
            ((IVec3d)vec3d).set(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
        } else {
            blockPos1 = blockPos.offset(direction.getOpposite());
            ((IVec3d)vec3d).set(blockPos1.getX() + 0.5 + direction.getOffsetX() * 0.5, blockPos1.getY() + 0.6 + direction.getOffsetY() * 0.5, blockPos1.getZ() + 0.5 + direction.getOffsetZ() * 0.5);
        }
        if (bl) {
            Direction direction1 = direction;
            Rotations.rotate(Rotations.getYaw(vec3d), Rotations.getPitch(vec3d), n2, () -> EnhancedBlockUtils.place2(n, vec3d, hand, direction1, blockPos1, bl2, bl4, bl5));
        } else {
            EnhancedBlockUtils.place(n, vec3d, hand, direction, blockPos1, bl2, bl4, bl5);
        }
        return true;
    }

    static {
        $assertionsDisabled = !EnhancedBlockUtils.class.desiredAssertionStatus();
        hitPos = new Vec3d(0.0, 0.0, 0.0);
        blocks = new ArrayList();
        breakingBlocks = new HashMap<Integer, BlockBreakingInfo/*class_3191*/>();
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent gameLeftEvent) {
        breakingBlocks.clear();
    }

    public static boolean isClickable(Block block) {
        boolean bl = block instanceof CraftingTableBlock || block instanceof AnvilBlock || block instanceof AbstractButtonBlock || block instanceof PressurePlateBlock || block instanceof BlockWithEntity || block instanceof FenceGateBlock || block instanceof DoorBlock || block instanceof NoteBlock || block instanceof TrapdoorBlock;
        return bl;
    }

    public static boolean obbyDoubleSurrounded(LivingEntity livingEntity) {
        BlockPos blockPos = livingEntity.getBlockPos();
        return EnhancedBlockUtils.isBlastRes(mc.world.getBlockState(livingEntity.getBlockPos().add(1, 0, 0)).getBlock()) && EnhancedBlockUtils.isBlastRes(mc.world.getBlockState(blockPos.add(-1, 0, 0)).getBlock()) && EnhancedBlockUtils.isBlastRes(mc.world.getBlockState(blockPos.add(0, 0, 1)).getBlock()) && EnhancedBlockUtils.isBlastRes(mc.world.getBlockState(blockPos.add(0, 0, -1)).getBlock()) && EnhancedBlockUtils.isBlastRes(mc.world.getBlockState(livingEntity.getBlockPos().add(1, 1, 0)).getBlock()) && EnhancedBlockUtils.isBlastRes(mc.world.getBlockState(blockPos.add(-1, 1, 0)).getBlock()) && EnhancedBlockUtils.isBlastRes(mc.world.getBlockState(blockPos.add(0, 1, 1)).getBlock()) && EnhancedBlockUtils.isBlastRes(mc.world.getBlockState(blockPos.add(0, 1, -1)).getBlock());
    }

    public static boolean isBlastRes(Block block) {
        if (block == Blocks.BEDROCK) {
            return true;
        }
        if (block == Blocks.OBSIDIAN) {
            return true;
        }
        if (block == Blocks.ENDER_CHEST) {
            return true;
        }
        if (block == Blocks.ANCIENT_DEBRIS) {
            return true;
        }
        if (block == Blocks.CRYING_OBSIDIAN) {
            return true;
        }
        if (block == Blocks.ENCHANTING_TABLE) {
            return true;
        }
        if (block == Blocks.NETHERITE_BLOCK) {
            return true;
        }
        if (block == Blocks.ANVIL) {
            return true;
        }
        if (block == Blocks.CHIPPED_ANVIL) {
            return true;
        }
        if (block == Blocks.DAMAGED_ANVIL) {
            return true;
        }
        return block == Blocks.RESPAWN_ANCHOR && Utils.getDimension() == Dimension.Nether;
    }

    public static boolean place(BlockPos blockPos, Hand hand, int n, boolean bl, int n2, boolean bl2) {
        return EnhancedBlockUtils.place(blockPos, hand, n, bl, n2, true, bl2, true, true);
    }

    public static boolean canPlace(BlockPos blockPos) {
        return EnhancedBlockUtils.canPlace(blockPos, true);
    }

    public static Direction rayTraceCheck(BlockPos blockPos, boolean bl) {
        Vec3d vec3d = new Vec3d(mc.player.getX(), mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ());
        for (Direction direction : Direction.values()) {
            RaycastContext raycastContext = new RaycastContext(vec3d, new Vec3d((double)blockPos.getX() + 0.5 + (double)direction.getVector().getX() * 0.5, (double)blockPos.getY() + 0.5 + (double)direction.getVector().getY() * 0.5, (double)blockPos.getZ() + 0.5 + (double)direction.getVector().getZ() * 0.5), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player);
            BlockHitResult blockHitResult = mc.world.raycast(raycastContext);
            if (blockHitResult == null || blockHitResult.getType() != HitResult.Type.BLOCK || !blockHitResult.getBlockPos().equals(blockPos)) continue;
            return direction;
        }
        if (bl) {
            if ((double)blockPos.getY() > vec3d.y) {
                return Direction.DOWN;
            }
            return Direction.UP;
        }
        return null;
    }

    public static boolean isSurrounded(LivingEntity livingEntity) {
        if (!$assertionsDisabled && mc.world == null) {
            throw new AssertionError();
        }
        return !mc.world.getBlockState(livingEntity.getBlockPos().add(1, 0, 0)).isAir() && !mc.world.getBlockState(livingEntity.getBlockPos().add(-1, 0, 0)).isAir() && !mc.world.getBlockState(livingEntity.getBlockPos().add(0, 0, 1)).isAir() && !mc.world.getBlockState(livingEntity.getBlockPos().add(0, 0, -1)).isAir();
    }

    public static boolean obbySurrounded(LivingEntity livingEntity) {
        BlockPos blockPos = livingEntity.getBlockPos();
        return EnhancedBlockUtils.isBlastRes(mc.world.getBlockState(livingEntity.getBlockPos().add(1, 0, 0)).getBlock()) && EnhancedBlockUtils.isBlastRes(mc.world.getBlockState(blockPos.add(-1, 0, 0)).getBlock()) && EnhancedBlockUtils.isBlastRes(mc.world.getBlockState(blockPos.add(0, 0, 1)).getBlock()) && EnhancedBlockUtils.isBlastRes(mc.world.getBlockState(blockPos.add(0, 0, -1)).getBlock());
    }

    public static boolean isRetard(LivingEntity livingEntity) {
        BlockPos blockPos = livingEntity.getBlockPos();
        return EnhancedBlockUtils.isBlastRes(mc.world.getBlockState(livingEntity.getBlockPos().add(1, 0, 0)).getBlock()) && EnhancedBlockUtils.isBlastRes(mc.world.getBlockState(blockPos.add(-1, 0, 0)).getBlock()) && EnhancedBlockUtils.isBlastRes(mc.world.getBlockState(blockPos.add(0, 0, 1)).getBlock()) && EnhancedBlockUtils.isBlastRes(mc.world.getBlockState(blockPos.add(0, 0, -1)).getBlock()) && EnhancedBlockUtils.isBlastRes(mc.world.getBlockState(livingEntity.getBlockPos().add(1, 1, 0)).getBlock()) && EnhancedBlockUtils.isBlastRes(mc.world.getBlockState(blockPos.add(-1, 1, 0)).getBlock()) && EnhancedBlockUtils.isBlastRes(mc.world.getBlockState(blockPos.add(0, 1, 1)).getBlock()) && EnhancedBlockUtils.isBlastRes(mc.world.getBlockState(blockPos.add(0, 1, -1)).getBlock()) && EnhancedBlockUtils.isBlastRes(mc.world.getBlockState(blockPos.add(0, -1, 0)).getBlock()) && EnhancedBlockUtils.isBlastRes(mc.world.getBlockState(blockPos.add(0, 2, 0)).getBlock());
    }

    public static boolean obbySurroundedWithAir(LivingEntity livingEntity) {
        BlockPos blockPos = livingEntity.getBlockPos();
        return EnhancedBlockUtils.isBlastRes(mc.world.getBlockState(livingEntity.getBlockPos().add(1, 0, 0)).getBlock()) && EnhancedBlockUtils.isBlastRes(mc.world.getBlockState(blockPos.add(-1, 0, 0)).getBlock()) && EnhancedBlockUtils.isBlastRes(mc.world.getBlockState(blockPos.add(0, 0, 1)).getBlock()) && EnhancedBlockUtils.isBlastRes(mc.world.getBlockState(blockPos.add(0, 0, -1)).getBlock()) && (EnhancedBlockUtils.isAir(mc.world.getBlockState(livingEntity.getBlockPos().add(1, 1, 0)).getBlock()) || EnhancedBlockUtils.isAir(mc.world.getBlockState(blockPos.add(-1, 1, 0)).getBlock()) || EnhancedBlockUtils.isAir(mc.world.getBlockState(blockPos.add(0, 1, 1)).getBlock()) || EnhancedBlockUtils.isAir(mc.world.getBlockState(blockPos.add(0, 1, -1)).getBlock()));
    }

    private static Direction getPlaceSide(BlockPos blockPos) {
        for (Direction direction : Direction.values()) {
            BlockPos blockPos1 = blockPos.offset(direction);
            Direction direction1 = direction.getOpposite();
            BlockState blockState = mc.world.getBlockState(blockPos1);
            if (blockState.isAir() || EnhancedBlockUtils.isClickable(blockState.getBlock()) || !blockState.getFluidState().isEmpty()) continue;
            return direction1;
        }
        return null;
    }

    private static void place(int n, Vec3d vec3d, Hand hand, Direction direction, BlockPos blockPos, boolean bl, boolean bl2, boolean bl3) {
        int n2 = mc.player.getInventory().selectedSlot;
        if (bl2) {
            mc.player.getInventory().selectedSlot = n;
        }
        boolean bl4 = mc.player.input.sneaking;
        mc.player.input.sneaking = false;
        mc.interactionManager.interactBlock(mc.player, mc.world, hand, new BlockHitResult(vec3d, direction, blockPos, false));
        if (bl) {
            mc.player.swingHand(hand);
        } else {
            mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(hand));
        }
        mc.player.input.sneaking = bl4;
        if (bl3) {
            mc.player.getInventory().selectedSlot = n2;
        }
    }

    public static double distanceBetween(BlockPos blockPos, BlockPos blockPos1) {
        double d = blockPos.getX() - blockPos1.getX();
        double d2 = blockPos.getY() - blockPos1.getY();
        double d3 = blockPos.getZ() - blockPos1.getZ();
        return MathHelper.sqrt(((float)(d * d + d2 * d2 + d3 * d3)));
    }

    public static boolean isAir(Block block) {
        return block == Blocks.AIR;
    }

    public static boolean canPlace(BlockPos blockPos, boolean bl) {
        if (blockPos == null) {
            return false;
        }
        if (World.isValid(blockPos)) {
            return false;
        }
        if (!mc.world.getBlockState(blockPos).getMaterial().isReplaceable()) {
            return false;
        }
        return !bl || mc.world.canPlace(Blocks.STONE.getDefaultState(), blockPos, ShapeContext.absent());
    }

    public static void init() {
        MatHaxClientLegacy.EVENT_BUS.subscribe(EnhancedBlockUtils.class);
    }

    public static boolean isIdkLmao(LivingEntity livingEntity) {
        if (!$assertionsDisabled && mc.world == null) {
            throw new AssertionError();
        }
        if (!$assertionsDisabled && mc.player == null) {
            throw new AssertionError();
        }
        int n = 0;
        int n2 = 0;
        if (EnhancedBlockUtils.isBurrowed(livingEntity)) {
            return false;
        }
        if (EnhancedBlockUtils.isBurrowed(mc.player) && livingEntity.getBlockPos().getX() == mc.player.getBlockPos().getX() && livingEntity.getBlockPos().getZ() == mc.player.getBlockPos().getZ() && livingEntity.getBlockPos().getY() - mc.player.getBlockPos().getY() <= 2) {
            return true;
        }
        if (!mc.world.getBlockState(livingEntity.getBlockPos().add(0, 2, 0)).isAir()) {
            return true;
        }
        if (!mc.world.getBlockState(livingEntity.getBlockPos().add(1, 0, 0)).isAir()) {
            ++n;
        }
        if (!mc.world.getBlockState(livingEntity.getBlockPos().add(-1, 0, 0)).isAir()) {
            ++n;
        }
        if (!mc.world.getBlockState(livingEntity.getBlockPos().add(0, 0, 1)).isAir()) {
            ++n;
        }
        if (!mc.world.getBlockState(livingEntity.getBlockPos().add(0, 0, -1)).isAir()) {
            ++n;
        }
        if (n == 3) {
            return true;
        }
        if (!mc.world.getBlockState(livingEntity.getBlockPos().add(1, 1, 0)).isAir()) {
            ++n2;
        }
        if (!mc.world.getBlockState(livingEntity.getBlockPos().add(-1, 1, 0)).isAir()) {
            ++n2;
        }
        if (!mc.world.getBlockState(livingEntity.getBlockPos().add(0, 1, 1)).isAir()) {
            ++n2;
        }
        if (!mc.world.getBlockState(livingEntity.getBlockPos().add(0, 1, -1)).isAir()) {
            ++n2;
        }
        return n < 4 && n2 == 4;
    }

    public static boolean isBurrowed(LivingEntity livingEntity) {
        return !mc.world.getBlockState(livingEntity.getBlockPos()).isAir();
    }

    private static void place2(int n, Vec3d vec3d, Hand hand, Direction direction, BlockPos blockPos, boolean bl, boolean bl2, boolean bl3) {
        EnhancedBlockUtils.place(n, vec3d, hand, direction, blockPos, bl, bl2, bl3);
    }

    public static List<BlockPos> getSphere(BlockPos blockPos, int n, int n2) {
        blocks.clear();
        for (int i = blockPos.getX() - n; i < blockPos.getX() + n; ++i) {
            for (int j = blockPos.getY() - n2; j < blockPos.getY() + n2; ++j) {
                for (int k = blockPos.getZ() - n; k < blockPos.getZ() + n; ++k) {
                    BlockPos blockPos1 = new BlockPos(i, j, k);
                    if (!(EnhancedBlockUtils.distanceBetween(blockPos, blockPos1) <= (double)n) || blocks.contains(blockPos1)) continue;
                    blocks.add(blockPos1);
                    if (null == null) continue;
                    return null;
                }
            }
            if (2 > 1) continue;
            return null;
        }
        return blocks;
    }
}
