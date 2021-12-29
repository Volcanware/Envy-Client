package mathax.legacy.client.systems.modules.movement;

import mathax.legacy.client.eventbus.EventHandler;
import mathax.legacy.client.events.entity.player.PlayerMoveEvent;
import mathax.legacy.client.events.packets.PacketEvent;
import mathax.legacy.client.events.world.ChunkOcclusionEvent;
import mathax.legacy.client.mixin.PlayerPositionLookS2CPacketAccessor;
import mathax.legacy.client.mixininterface.IVec3d;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.settings.DoubleSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.utils.misc.TimeVec;
import mathax.legacy.client.utils.player.PlayerUtils;
import mathax.legacy.client.utils.world.BlockUtils;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShapes;

import mathax.legacy.client.events.world.CollisionShapeEvent;
import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.systems.modules.Module;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class Phase extends Module {
    private final ArrayList<PlayerMoveC2SPacket> packets = new ArrayList<>();
    private final Map<Integer, TimeVec> posLooks = new ConcurrentHashMap<>();

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private static final Random random = new Random();

    private int teleportId = 0;
    private int ticksExisted;

    double speedX = 0;
    double speedY = 0;
    double speedZ = 0;

    // General

    private final Setting<Double> speed = sgGeneral.add(new DoubleSetting.Builder()
        .name("speed")
        .description("At which speed to travel.")
        .defaultValue(1)
        .min(0)
        .build()
    );

    public Phase() {
        super(Categories.Movement, Items.BEDROCK, "phase", "Allows you to phase trough walls.");
    }

    @Override
    public void onActivate() {
        mc.worldRenderer.reload();
        packets.clear();
        posLooks.clear();
        teleportId = -1;
        ticksExisted = 0;
    }

    @EventHandler
    public void isCube(CollisionShapeEvent event) {
        if (BlockUtils.distance(mc.player.getX(), mc.player.getY(), mc.player.getZ(), event.pos.getX(), event.pos.getY(), event.pos.getZ()) > 3) return;

        event.shape = VoxelShapes.empty();
    }

    @EventHandler
    public void onChunkOcclusion(ChunkOcclusionEvent event) {
        event.cancel();
    }

    @EventHandler
    public void onPostTick(TickEvent.Post event) {
        if (ticksExisted % 20 == 0) {
            posLooks.forEach((tp, timeVec3d) -> {
                if (System.currentTimeMillis() - timeVec3d.getTime() > TimeUnit.SECONDS.toMillis(30L)) posLooks.remove(tp);
            });
        }

        ticksExisted++;

        mc.player.setVelocity(0.0D, 0.0D, 0.0D);

        if (teleportId <= 0) {
            PlayerMoveC2SPacket startingOutOfBoundsPos = new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + randomLimitedVertical(), mc.player.getZ(), mc.player.isOnGround());
            packets.add(startingOutOfBoundsPos);
            mc.getNetworkHandler().sendPacket(startingOutOfBoundsPos);
        }

        double[] dir = PlayerUtils.directionSpeed(speed.get().floatValue());

        speedX = dir[0];
        speedY = 0;
        speedZ = dir[1];

        Vec3d newPos = new Vec3d(mc.player.getX() + speedX, mc.player.getY(), mc.player.getZ() + speedZ);
        Vec3d blockCenter = new Vec3d(Math.floor(mc.player.getX()), Math.floor(mc.player.getY()), Math.floor(mc.player.getZ())).add(0.5, 0, 0.5);

        Vec3d min = newPos.subtract(0.3, 0, 0.3);
        Vec3d max = newPos.add(0.3, 0, 0.3);

        Vec3i minI = new Vec3i(Math.floor(min.x), Math.floor(min.y), Math.floor(min.z));
        Vec3i maxI = new Vec3i(Math.floor(max.x), Math.floor(max.y), Math.floor(max.z));

        if (!minI.equals(maxI) && newPos.distanceTo(blockCenter) > mc.player.getPos().distanceTo(blockCenter)) {
            dir = PlayerUtils.directionSpeed(0.062f);
            speedX = dir[0];
            speedY = 0;
            speedZ = dir[1];

            PlayerMoveC2SPacket move = new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX() + speedX, mc.player.getY() + speedY, mc.player.getZ() + speedZ, mc.player.isOnGround());
            packets.add(move);
            mc.getNetworkHandler().sendPacket(move);
            PlayerMoveC2SPacket extremeMove = new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX() + speedX, mc.player.getY() + randomLimitedVertical(), mc.player.getZ() + speedZ, mc.player.isOnGround());
            packets.add(extremeMove);
            mc.getNetworkHandler().sendPacket(extremeMove);
            teleportId++;
            mc.getNetworkHandler().sendPacket(new TeleportConfirmC2SPacket(teleportId));
            posLooks.put(teleportId, new TimeVec(mc.player.getX(), mc.player.getY(), mc.player.getZ(), System.currentTimeMillis()));
        } else {
            PlayerMoveC2SPacket move = new PlayerMoveC2SPacket.PositionAndOnGround(newPos.x, newPos.y, newPos.z, mc.player.isOnGround());
            packets.add(move);
            mc.getNetworkHandler().sendPacket(move);
        }

        mc.player.setVelocity(speedX, speedY, speedZ);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        mc.player.noClip = true;

        ((IVec3d) event.movement).set(speedX, speedY, speedZ);
    }

    @EventHandler
    public void onSendPacket(PacketEvent.Send event) {
        if (event.packet instanceof PlayerMoveC2SPacket && !(event.packet instanceof PlayerMoveC2SPacket.PositionAndOnGround)) event.cancel();
        if (event.packet instanceof PlayerMoveC2SPacket packet) {
            if (this.packets.contains(packet)) {
                this.packets.remove(packet);
                return;
            }

            event.cancel();
        }
    }

    @EventHandler
    public void onReceive(PacketEvent.Receive event) {
        if (event.packet instanceof PlayerPositionLookS2CPacket) {
            PlayerPositionLookS2CPacket packet = (PlayerPositionLookS2CPacket) event.packet;
            if (mc.player.isAlive()) {
                if (this.teleportId <= 0) this.teleportId = packet.getTeleportId();
                else {
                    if (mc.world.isPosLoaded(mc.player.getBlockX(), mc.player.getBlockZ())) {
                        if (posLooks.containsKey(packet.getTeleportId())) {
                            TimeVec vec = posLooks.get(packet.getTeleportId());
                            if (vec.x == packet.getX() && vec.y == packet.getY() && vec.z == packet.getZ()) {
                                posLooks.remove(packet.getTeleportId());
                                event.setCancelled(true);
                                return;
                            }
                        }
                    }
                }
            }

            ((PlayerPositionLookS2CPacketAccessor) event.packet).setYaw(mc.player.getYaw());
            ((PlayerPositionLookS2CPacketAccessor) event.packet).setPitch(mc.player.getPitch());
            packet.getFlags().remove(PlayerPositionLookS2CPacket.Flag.X_ROT);
            packet.getFlags().remove(PlayerPositionLookS2CPacket.Flag.Y_ROT);
            teleportId = packet.getTeleportId();
        }
    }

    private double randomLimitedVertical() {
        int randomValue = random.nextInt(22);
        randomValue += 70;
        if (random.nextBoolean()) return randomValue;
        return -randomValue;
    }
}
