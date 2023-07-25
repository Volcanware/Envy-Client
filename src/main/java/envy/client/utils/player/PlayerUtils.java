package envy.client.utils.player;

import baritone.api.BaritoneAPI;
import baritone.api.utils.Rotation;
import envy.client.Envy;
import envy.client.events.movement.EventMove;
import envy.client.mixininterface.IVec3d;
import envy.client.systems.config.Config;
import envy.client.systems.enemies.Enemies;
import envy.client.systems.friends.Friends;
import envy.client.systems.modules.Modules;
import envy.client.systems.modules.movement.NoFall;
import envy.client.utils.Utils;
import envy.client.utils.entity.EntityUtils;
import envy.client.utils.misc.BaritoneUtils;
import envy.client.utils.misc.text.TextUtils;
import envy.client.utils.render.color.Color;
import envy.client.utils.world.BlockUtils;
import envy.client.utils.world.Dimension;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.PotionItem;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.RaycastContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static envy.client.utils.Utils.WHITE;

public class PlayerUtils {
    private static final Vec3d hitPos = new Vec3d(0.0, 0.0, 0.0);
    private static final double diagonal = 1 / Math.sqrt(2);
    private static final Vec3d horizontalVelocity = new Vec3d(0, 0, 0);

    private static final Color color = new Color();

    public static void swingHand(boolean offhand) {
        if (offhand) Envy.mc.player.swingHand(Hand.OFF_HAND);
        else Envy.mc.player.swingHand(Hand.MAIN_HAND);
    }

    public static Rotation getNeededRotations(Vec3d vec) {
        Vec3d eyesPos = getEyesPos();

        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float)-Math.toDegrees(Math.atan2(diffY, diffXZ));

        return new Rotation(yaw, pitch);
    }

    public static Vec3d getEyesPos() {
        return new Vec3d(Envy.mc.player.getX(), Envy.mc.player.getY() + Envy.mc.player.getEyeHeight(Envy.mc.player.getPose()), Envy.mc.player.getZ());
    }

    public static boolean isPlayerMoving(PlayerEntity player) {
        return player.forwardSpeed != 0 || player.sidewaysSpeed != 0;
    }


    public static double[] directionSpeed(double speed) {
        float forward = Envy.mc.player.input.movementForward;
        float side = Envy.mc.player.input.movementSideways;
        float yaw = Envy.mc.player.prevYaw + (Envy.mc.player.getYaw() - Envy.mc.player.prevYaw);

        if (forward != 0.0f) {
            if (side > 0.0f) yaw += ((forward > 0.0f) ? -45 : 45);
            else if (side < 0.0f) yaw += ((forward > 0.0f) ? 45 : -45);
            side = 0.0f;
            if (forward > 0.0f) forward = 1.0f;
            else if (forward < 0.0f) forward = -1.0f;
        }

        final double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        final double posX = forward * speed * cos + side * speed * sin;
        final double posZ = forward * speed * sin - side * speed * cos;

        return new double[] {
            posX,
            posZ
        };
    }

    // Place Block Main Hand

    public static boolean placeBlockMainHand(BlockPos pos) {
        return placeBlockMainHand(pos, false, 0, true);
    }

    public static boolean placeBlockMainHand(BlockPos pos, boolean oldPlacement, int slot) {
        return placeBlockMainHand(pos, oldPlacement, slot, true);
    }

    public static boolean placeBlockMainHand(BlockPos pos, boolean oldPlacement, int slot, boolean rotate) {
        return placeBlockMainHand(pos, oldPlacement, slot, rotate, true);
    }

    public static boolean placeBlockMainHand(BlockPos pos, boolean oldPlacement, int slot, boolean rotate, boolean swing) {
        return placeBlockMainHand(pos, oldPlacement, slot, rotate, swing, false);
    }

    public static boolean placeBlockMainHand(BlockPos pos, boolean oldPlacement, int slot, boolean rotate, boolean swing, final Boolean airPlace) {
        return placeBlockMainHand(pos, oldPlacement, slot, rotate, swing, airPlace, false);
    }

    public static boolean placeBlockMainHand(BlockPos pos, boolean oldPlacement, int slot, boolean rotate, boolean swing, boolean airPlace, boolean ignoreEntity) {
        return placeBlockMainHand(pos, oldPlacement, slot, rotate, swing, airPlace, ignoreEntity, null);
    }

    public static boolean placeBlockMainHand(BlockPos pos, boolean oldPlacement, int slot, boolean rotate, boolean swing, boolean airPlace, boolean ignoreEntity, Direction overrideSide) {
        return placeBlock2(Hand.MAIN_HAND, pos, oldPlacement, slot, rotate, swing, airPlace, ignoreEntity, overrideSide);
    }

    // Place Block

    public static boolean placeBlock(BlockPos blockPos, Hand hand, boolean bl) {
        return PlayerUtils.placeBlock(blockPos, hand, true, bl);
    }

    public static boolean placeBlock(BlockPos blockPos, int n, Hand hand, boolean bl) {
        if (n == -1) return false;

        int n2 = Envy.mc.player.getInventory().selectedSlot;
        Envy.mc.player.getInventory().selectedSlot = n;
        boolean bl2 = placeBlock(blockPos, hand, true, bl);
        Envy.mc.player.getInventory().selectedSlot = n2;

        return bl2;
    }

    public static boolean placeBlock(BlockPos blockPos, Hand hand, boolean bl, boolean bl2) {
        if (!BlockUtils.canPlace(blockPos)) return false;

        for (Direction direction : Direction.values()) {
            BlockPos blockPos1 = blockPos.offset(direction);
            Direction direction1 = direction.getOpposite();
            if (Envy.mc.world.getBlockState(blockPos1).isAir() || BlockUtils.isClickable(Envy.mc.world.getBlockState(blockPos1).getBlock())) continue;
            ((IVec3d)hitPos).set(blockPos1.getX() + 0.5 + direction1.getVector().getX() * 0.5, blockPos1.getY() + 0.5 + direction1.getVector().getY() * 0.5, blockPos1.getZ() + 0.5 + direction1.getVector().getZ() * 0.5);
            boolean bl3 = Envy.mc.player.input.sneaking;
            Envy.mc.player.input.sneaking = false;
            Envy.mc.interactionManager.interactBlock(Envy.mc.player, hand, new BlockHitResult(hitPos, direction1, blockPos1, false));
            if (bl) Envy.mc.player.swingHand(hand);
            Envy.mc.player.input.sneaking = bl3;
            return true;
        }

        if (!bl2) return false;

        ((IVec3d)hitPos).set(blockPos);
        Envy.mc.interactionManager.interactBlock(Envy.mc.player, hand, new BlockHitResult(hitPos, Direction.UP, blockPos, false));
        if (bl) Envy.mc.player.swingHand(hand);
        return true;
    }

    public static boolean placeBlock2(Hand hand, BlockPos pos) {
        placeBlock2(hand, pos, false, 0, true, false);
        return true;
    }

    public static boolean placeBlock2(Hand hand, BlockPos pos, boolean oldPlacement, int slot) {
        placeBlock2(hand, pos, oldPlacement, slot, true, false);
        return true;
    }

    public static boolean placeBlock2(Hand hand, BlockPos pos, boolean oldPlacement, int slot, boolean rotate) {
        placeBlock2(hand, pos, oldPlacement, slot, rotate, false);
        return true;
    }

    public static boolean placeBlock2(Hand hand, BlockPos pos, boolean oldPlacement, int slot, boolean rotate, boolean swing) {
        placeBlock2(hand, pos, oldPlacement, slot, rotate, swing, false);
        return true;
    }

    public static boolean placeBlock2(Hand hand, BlockPos pos, boolean oldPlacement, int slot, boolean rotate, boolean swing, boolean airPlace) {
        placeBlock2(hand, pos, oldPlacement, slot, rotate, swing, airPlace, false);
        return true;
    }

    public static boolean placeBlock2(Hand hand, BlockPos pos, boolean oldPlacement, int slot, boolean rotate, boolean swing, boolean airPlace, boolean ignoreEntity) {
        placeBlock2(hand, pos, oldPlacement, slot, rotate, swing, airPlace, ignoreEntity, null);
        return true;
    }

    public static boolean placeBlock2(Hand hand, BlockPos pos, boolean oldPlacement, int slot, boolean rotate, boolean swing, boolean airPlace, boolean ignoreEntity, Direction overrideSide) {
        if (ignoreEntity && !Envy.mc.world.getBlockState(pos).getMaterial().isReplaceable()) return false;
        else if (!Envy.mc.world.getBlockState(pos).getMaterial().isReplaceable() || !Envy.mc.world.canPlace(Blocks.OBSIDIAN.getDefaultState(), pos, ShapeContext.absent())) return false;

        final Vec3d eyesPos = new Vec3d(Envy.mc.player.getX(), Envy.mc.player.getY() + Envy.mc.player.getEyeHeight(Envy.mc.player.getPose()), Envy.mc.player.getZ());
        Vec3d hitVec = null;
        BlockPos neighbor = null;
        Direction side2 = null;
        if (overrideSide != null) {
            neighbor = pos.offset(overrideSide.getOpposite());
            side2 = overrideSide;
        }

        final Direction[] values = Direction.values();
        final int length = values.length;
        int i = 0;
        while (i < length) {
            final Direction side3 = values[i];
            if (overrideSide == null) {
                neighbor = pos.offset(side3);
                side2 = side3.getOpposite();
                if (Envy.mc.world.getBlockState(neighbor).isAir() || Envy.mc.world.getBlockState(neighbor).getBlock() instanceof FluidBlock) {
                    neighbor = null;
                    side2 = null;
                    ++i;
                    continue;
                }
            }

            hitVec = new Vec3d(neighbor.getX(), neighbor.getY(), neighbor.getZ()).add(0.5, 0.5, 0.5).add(new Vec3d(side2.getUnitVector()).multiply(0.5));
            break;
        }

        if (airPlace) {
            if (hitVec == null) hitVec = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
            if (neighbor == null) neighbor = pos;
            if (side2 == null) side2 = Direction.UP;
        }

        else if (hitVec == null || neighbor == null || side2 == null) return false;
        final double diffX = hitVec.x - eyesPos.x;
        final double diffY = hitVec.y - eyesPos.y;
        final double diffZ = hitVec.z - eyesPos.z;
        final double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        final float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        final float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        final float[] rotations = {
            Envy.mc.player.getYaw() + MathHelper.wrapDegrees(yaw - Envy.mc.player.getYaw()),
            Envy.mc.player.getPitch() + MathHelper.wrapDegrees(pitch - Envy.mc.player.getPitch())
        };

        if (oldPlacement) {
            BlockUtils.place(pos, hand, slot, rotate, 0, swing, !ignoreEntity, true);
            return true;
        }

        if (rotate) Envy.mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(rotations[0], rotations[1], Envy.mc.player.isOnGround()));
        Envy.mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(Envy.mc.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
        Envy.mc.interactionManager.interactBlock(Envy.mc.player, hand, new BlockHitResult(hitVec, side2, neighbor, false));
        if (swing) Envy.mc.player.swingHand(hand);
        Envy.mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(Envy.mc.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));

        return true;
    }

    public static Color getPlayerColor(PlayerEntity entity, Color defaultColor) {
        if (Friends.get().isFriend(entity)) return color.set(Friends.get().color).a(defaultColor.a);
        if (Enemies.get().isEnemy(entity)) return color.set(Enemies.get().color).a(defaultColor.a);
        if (!color.set(TextUtils.getMostPopularColor(entity.getDisplayName())).equals(WHITE) && Config.get().useTeamColor.get()) return color.set(color).a(defaultColor.a);

        return defaultColor;
    }

    public static Vec3d getHorizontalVelocity(double bps) {
        float yaw = Envy.mc.player.getYaw();

        if (BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing()) {
            Rotation target = BaritoneUtils.getTarget();
            if (target != null) yaw = target.getYaw();
        }

        Vec3d forward = Vec3d.fromPolar(0, yaw);
        Vec3d right = Vec3d.fromPolar(0, yaw + 90);
        double velX = 0;
        double velZ = 0;

        boolean a = false;
        if (Envy.mc.player.input.pressingForward) {
            velX += forward.x / 20 * bps;
            velZ += forward.z / 20 * bps;
            a = true;
        }

        if (Envy.mc.player.input.pressingBack) {
            velX -= forward.x / 20 * bps;
            velZ -= forward.z / 20 * bps;
            a = true;
        }

        boolean b = false;
        if (Envy.mc.player.input.pressingRight) {
            velX += right.x / 20 * bps;
            velZ += right.z / 20 * bps;
            b = true;
        }

        if (Envy.mc.player.input.pressingLeft) {
            velX -= right.x / 20 * bps;
            velZ -= right.z / 20 * bps;
            b = true;
        }

        if (a && b) {
            velX *= diagonal;
            velZ *= diagonal;
        }

        ((IVec3d) horizontalVelocity).setXZ(velX, velZ);
        return horizontalVelocity;
    }

    public static void centerPlayer() {
        double x = MathHelper.floor(Envy.mc.player.getX()) + 0.5;
        double z = MathHelper.floor(Envy.mc.player.getZ()) + 0.5;
        Envy.mc.player.setPosition(x, Envy.mc.player.getY(), z);
        Envy.mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(Envy.mc.player.getX(), Envy.mc.player.getY(), Envy.mc.player.getZ(), Envy.mc.player.isOnGround()));
    }

    public static void centerPlayer(BlockPos lastPos) {
        double xPos = Envy.mc.player.getPos().x;
        double zPos = Envy.mc.player.getPos().z;

        if (Math.abs(lastPos.getX() + 0.5 - Envy.mc.player.getPos().x) >= 0.2) {
            int xDir = (lastPos.getX() + 0.5 - Envy.mc.player.getPos().x > 0.0) ? 1 : -1;
            xPos += 0.3 * xDir;
        }

        if (Math.abs(lastPos.getZ() + 0.5 - Envy.mc.player.getPos().z) >= 0.2) {
            int zDir = (lastPos.getZ() + 0.5 - Envy.mc.player.getPos().z > 0.0) ? 1 : -1;
            zPos += 0.3 * zDir;
        }

        Envy.mc.player.setVelocity(0.0, 0.0, 0.0);
        Envy.mc.player.setPosition(xPos, Envy.mc.player.getY(), zPos);
        Envy.mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(Envy.mc.player.getX(), Envy.mc.player.getY(), Envy.mc.player.getZ(), Envy.mc.player.isOnGround()));
    }

    public static boolean canSeeEntity(Entity entity) {
        Vec3d vec1 = new Vec3d(0, 0, 0);
        Vec3d vec2 = new Vec3d(0, 0, 0);

        ((IVec3d) vec1).set(Envy.mc.player.getX(), Envy.mc.player.getY() + Envy.mc.player.getStandingEyeHeight(), Envy.mc.player.getZ());
        ((IVec3d) vec2).set(entity.getX(), entity.getY(), entity.getZ());
        boolean canSeeFeet = Envy.mc.world.raycast(new RaycastContext(vec1, vec2, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, Envy.mc.player)).getType() == HitResult.Type.MISS;

        ((IVec3d) vec2).set(entity.getX(), entity.getY() + entity.getStandingEyeHeight(), entity.getZ());
        boolean canSeeEyes = Envy.mc.world.raycast(new RaycastContext(vec1, vec2, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, Envy.mc.player)).getType() == HitResult.Type.MISS;

        return canSeeFeet || canSeeEyes;
    }

    public static float[] calculateAngle(Vec3d target) {
        Vec3d eyesPos = new Vec3d(Envy.mc.player.getX(), Envy.mc.player.getY() + Envy.mc.player.getEyeHeight(Envy.mc.player.getPose()), Envy.mc.player.getZ());

        double dX = target.x - eyesPos.x;
        double dY = (target.y - eyesPos.y) * -1.0D;
        double dZ = target.z - eyesPos.z;

        double dist = Math.sqrt(dX * dX + dZ * dZ);

        return new float[]{(float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(dZ, dX)) - 90.0D), (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(dY, dist)))};
    }

    public static float[] calculateYaw(Vec3d target) {
        Vec3d eyesPos = new Vec3d(Envy.mc.player.getX(), Envy.mc.player.getY() + Envy.mc.player.getEyeHeight(Envy.mc.player.getPose()), Envy.mc.player.getZ());

        double dX = target.x - eyesPos.x;
        double dY = (target.y - eyesPos.y) * -1.0D;
        double dZ = target.z - eyesPos.z;

        double dist = Math.sqrt(dX * dX + dZ * dZ);

        return new float[]{(float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(dZ, dX)) - 90.0D), (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(dY, dist)))};
    }

    public static float[] calculatePitch(Vec3d target) {
        Vec3d eyesPos = new Vec3d(Envy.mc.player.getX(), Envy.mc.player.getY() + Envy.mc.player.getEyeHeight(Envy.mc.player.getPose()), Envy.mc.player.getZ());

        double dX = target.x - eyesPos.x;
        double dY = (target.y - eyesPos.y) * -1.0D;
        double dZ = target.z - eyesPos.z;

        double dist = Math.sqrt(dX * dX + dZ * dZ);

        return new float[]{(float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(dZ, dX)) - 90.0D), (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(dY, dist)))};
    }

    public static boolean shouldPause(boolean ifBreaking, boolean ifEating, boolean ifDrinking) {
        if (ifBreaking && Envy.mc.interactionManager.isBreakingBlock()) return true;
        if (ifEating && (Envy.mc.player.isUsingItem() && (Envy.mc.player.getMainHandStack().getItem().isFood() || Envy.mc.player.getOffHandStack().getItem().isFood()))) return true;
        return ifDrinking && (Envy.mc.player.isUsingItem() && (Envy.mc.player.getMainHandStack().getItem() instanceof PotionItem || Envy.mc.player.getOffHandStack().getItem() instanceof PotionItem));
    }

    public static boolean isMoving() {
        return Envy.mc.player.forwardSpeed != 0 || Envy.mc.player.sidewaysSpeed != 0;
    }

    public static boolean isSprinting() {
        return Envy.mc.player.isSprinting() && (Envy.mc.player.forwardSpeed != 0 || Envy.mc.player.sidewaysSpeed != 0);
    }

    public static ArrayList<Vec3d> selfTrapPositions = new ArrayList<Vec3d>() {{
        add(new Vec3d(1, 1, 0));
        add(new Vec3d(-1, 1, 0));
        add(new Vec3d(0, 1, 1));
        add(new Vec3d(0, 1, -1));
    }};

    public static BlockPos getSelfTrapBlock(PlayerEntity p, Boolean escapePrevention) {
        BlockPos tpos = p.getBlockPos();
        List<BlockPos> selfTrapBlocks = new ArrayList<>();
        if (!escapePrevention && BlockUtils.isTrapBlock(tpos.up(2))) return tpos.up(2);
        for (Vec3d stp : selfTrapPositions) {
            BlockPos stb = tpos.add(stp.x, stp.y, stp.z);
            if (BlockUtils.isTrapBlock(stb)) selfTrapBlocks.add(stb);
        }
        if (selfTrapBlocks.isEmpty()) return null;
        return selfTrapBlocks.get(new Random().nextInt(selfTrapBlocks.size()));
    }

    public static boolean isInHole(PlayerEntity p) {
        BlockPos pos = p.getBlockPos();
        return !Envy.mc.world.getBlockState(pos.add(1, 0, 0)).isAir() && !Envy.mc.world.getBlockState(pos.add(-1, 0, 0)).isAir() && !Envy.mc.world.getBlockState(pos.add(0, 0, 1)).isAir() && !Envy.mc.world.getBlockState(pos.add(0, 0, -1)).isAir() && !Envy.mc.world.getBlockState(pos.add(0, -1, 0)).isAir();
    }

    public static boolean isInHole2(boolean doubles) {
        if (!Utils.canUpdate()) return false;

        BlockPos blockPos = Envy.mc.player.getBlockPos();
        int air = 0;

        for (Direction direction : Direction.values()) {
            if (direction == Direction.UP) continue;

            BlockState state = Envy.mc.world.getBlockState(blockPos.offset(direction));

            if (state.getBlock().getBlastResistance() < 600) {
                if (!doubles || direction == Direction.DOWN) return false;

                air++;

                for (Direction dir : Direction.values()) {
                    if (dir == direction.getOpposite() || dir == Direction.UP) continue;

                    BlockState blockState1 = Envy.mc.world.getBlockState(blockPos.offset(direction).offset(dir));

                    if (blockState1.getBlock().getBlastResistance() < 600) return false;
                }
            }
        }

        return air < 2;
    }

    public static boolean isSurrounded(PlayerEntity target) {
        return !Envy.mc.world.getBlockState(target.getBlockPos().add(1, 0, 0)).isAir() && !Envy.mc.world.getBlockState(target.getBlockPos().add(-1, 0, 0)).isAir() && !Envy.mc.world.getBlockState(target.getBlockPos().add(0, 0, 1)).isAir() && !Envy.mc.world.getBlockState(target.getBlockPos().add(0, 0, -1)).isAir();
    }

    public static boolean isNoob(PlayerEntity target) {
        int count = 0;
        int count2 = 0;

        if (isBurrowed(target)) return false;
        if (isBurrowed(Envy.mc.player) && target.getBlockPos().getX() == Envy.mc.player.getBlockPos().getX() && target.getBlockPos().getZ() == Envy.mc.player.getBlockPos().getZ() && target.getBlockPos().getY() - Envy.mc.player.getBlockPos().getY() <= 2) return true;
        if (!Envy.mc.world.getBlockState(target.getBlockPos().add(0, 2, 0)).isAir()) return true;
        if (!Envy.mc.world.getBlockState(target.getBlockPos().add(1, 0, 0)).isAir()) ++count;
        if (!Envy.mc.world.getBlockState(target.getBlockPos().add(-1, 0, 0)).isAir()) ++count;
        if (!Envy.mc.world.getBlockState(target.getBlockPos().add(0, 0, 1)).isAir()) ++count;
        if (!Envy.mc.world.getBlockState(target.getBlockPos().add(0, 0, -1)).isAir()) ++count;
        if (count == 3) return true;
        if (!Envy.mc.world.getBlockState(target.getBlockPos().add(1, 1, 0)).isAir()) ++count2;
        if (!Envy.mc.world.getBlockState(target.getBlockPos().add(-1, 1, 0)).isAir()) ++count2;
        if (!Envy.mc.world.getBlockState(target.getBlockPos().add(0, 1, 1)).isAir()) ++count2;
        if (!Envy.mc.world.getBlockState(target.getBlockPos().add(0, 1, -1)).isAir()) ++count2;
        return count < 4 && count2 == 4;
    }

    public static boolean isBurrowed(PlayerEntity target) {
        return !Envy.mc.world.getBlockState(target.getBlockPos()).isAir();
    }

    public static boolean isBurrowed(PlayerEntity p, boolean holeCheck) {
        BlockPos pos = p.getBlockPos();
        if (holeCheck && !isInHole(p)) return false;
        return BlockUtils.getBlock(pos) == Blocks.ENDER_CHEST || BlockUtils.getBlock(pos) == Blocks.OBSIDIAN || BlockUtils.isAnvilBlock(pos);
    }

    public static boolean isWebbed(PlayerEntity player) {
        BlockPos pos = player.getBlockPos();
        if (BlockUtils.isWeb(pos)) return true;
        return BlockUtils.isWeb(pos.up());
    }

    public static void mineWeb(PlayerEntity p, int swordSlot) {
        if (p == null || swordSlot == -1) return;
        BlockPos pos = p.getBlockPos();
        BlockPos webPos = null;
        if (BlockUtils.isWeb(pos)) webPos = pos;
        if (BlockUtils.isWeb(pos.up())) webPos = pos.up();
        if (BlockUtils.isWeb(pos.up(2))) webPos = pos.up(2);
        if (webPos == null) return;
        InvUtils.updateSlot(swordSlot);
        doRegularMine(webPos);
    }

    public static void doPacketMine(BlockPos targetPos) {
        Envy.mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, targetPos, Direction.UP));
        swingHand(false);
        Envy.mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, targetPos, Direction.UP));
    }

    public static void doRegularMine(BlockPos targetPos) {
        Envy.mc.interactionManager.updateBlockBreakingProgress(targetPos, Direction.UP);
        Vec3d hitPos = new Vec3d(targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5);
        Rotations.rotate(Rotations.getYaw(hitPos), Rotations.getPitch(hitPos), 50, () -> swingHand(false));
    }

    public static double possibleHealthReductions() {
        return possibleHealthReductions(true, true);
    }

    public static double possibleHealthReductions(boolean entities, boolean fall) {
        double damageTaken = 0;

        if (entities) {
            for (Entity entity : Envy.mc.world.getEntities()) {
                // Check for end crystals
                if (entity instanceof EndCrystalEntity && damageTaken < DamageUtils.crystalDamage(Envy.mc.player, entity.getPos())) damageTaken = DamageUtils.crystalDamage(Envy.mc.player, entity.getPos());

                    // Check for players holding swords
                else if (entity instanceof PlayerEntity && damageTaken < DamageUtils.getSwordDamage((PlayerEntity) entity, true)) {
                    if (!Friends.get().isFriend((PlayerEntity) entity) && Envy.mc.player.getPos().distanceTo(entity.getPos()) < 5) {
                        if (((PlayerEntity) entity).getActiveItem().getItem() instanceof SwordItem) damageTaken = DamageUtils.getSwordDamage((PlayerEntity) entity, true);
                    }
                }
            }

            // Check for beds if in nether
            if (PlayerUtils.getDimension() != Dimension.Overworld) {
                for (BlockEntity blockEntity : Utils.blockEntities()) {
                    BlockPos bp = blockEntity.getPos();
                    Vec3d pos = new Vec3d(bp.getX(), bp.getY(), bp.getZ());

                    if (blockEntity instanceof BedBlockEntity && damageTaken < DamageUtils.bedDamage(Envy.mc.player, pos)) damageTaken = DamageUtils.bedDamage(Envy.mc.player, pos);
                }
            }
        }

        // Check for fall distance with water check
        if (fall) {
            if (!Modules.get().isActive(NoFall.class) && Envy.mc.player.fallDistance > 3) {
                double damage = Envy.mc.player.fallDistance * 0.5;

                if (damage > damageTaken && !EntityUtils.isAboveWater(Envy.mc.player)) damageTaken = damage;
            }
        }

        return damageTaken;
    }

    public static double distanceTo(Entity entity) {
        return distanceTo(entity.getX(), entity.getY(), entity.getZ());
    }

    public static double distanceTo(BlockPos blockPos) {
        return distanceTo(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static double distanceTo(Vec3d vec3d) {
        return distanceTo(vec3d.getX(), vec3d.getY(), vec3d.getZ());
    }

    public static double distanceTo(double x, double y, double z) {
        if (Envy.mc.player == null) return 0;
        float f = (float) (Envy.mc.player.getX() - x);
        float g = (float) (Envy.mc.player.getY() - y);
        float h = (float) (Envy.mc.player.getZ() - z);
        return MathHelper.sqrt(f * f + g * g + h * h);
    }

    public static double distanceToCamera(double x, double y, double z) {
        Camera camera = Envy.mc.gameRenderer.getCamera();
        return Math.sqrt(camera.getPos().squaredDistanceTo(x, y, z));
    }

    public static double distanceToCamera(Entity entity) {
        return distanceToCamera(entity.getX(), entity.getY(), entity.getZ());
    }

    public static Dimension getDimension() {
        if (Envy.mc.world == null) return Dimension.Overworld;

        return switch (Envy.mc.world.getRegistryKey().getValue().getPath()) {
            case "the_nether" -> Dimension.Nether;
            case "the_end" -> Dimension.End;
            default -> Dimension.Overworld;
        };
    }

    public static GameMode getGameMode() {
        PlayerListEntry playerListEntry = Envy.mc.getNetworkHandler().getPlayerListEntry(Envy.mc.player.getUuid());
        if (playerListEntry == null) return GameMode.SPECTATOR;
        return playerListEntry.getGameMode();
    }

    public static double getTotalHealth() {
        return Envy.mc.player.getHealth() + Envy.mc.player.getAbsorptionAmount();
    }

    public static boolean isAlive() {
        return Envy.mc.player.isAlive() && !Envy.mc.player.isDead();
    }

    public static int getPing() {
        if (Envy.mc.getNetworkHandler() == null) return 0;

        PlayerListEntry playerListEntry = Envy.mc.getNetworkHandler().getPlayerListEntry(Envy.mc.player.getUuid());
        if (playerListEntry == null) return 0;
        return playerListEntry.getLatency();
    }

    public static void setMoveSpeed(EventMove event, final double speed) {
        double forward = Envy.mc.player.input.movementForward;
        double strafe = Envy.mc.player.input.movementSideways;
        float yaw = Envy.mc.player.getYaw();
        if (forward == 0.0 && strafe == 0.0) {
            event.setX(0.0);
            event.setZ(0.0);
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            event.setX(forward * speed * Math.cos(Math.toRadians(yaw + 90.0f)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0f)));
            event.setZ(forward * speed * Math.sin(Math.toRadians(yaw + 90.0f)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0f)));
        }
    }

    public static BlockPos roundBlockPos(final Vec3d vec) {
        return new BlockPos(vec.x, Math.round(vec.y), vec.z);
    }
}
