package envy.client.systems.modules.movement.elytrafly.modes;

import envy.client.Envy;
import envy.client.events.packets.PacketEvent;
import envy.client.systems.modules.movement.elytrafly.ElytraFlightMode;
import envy.client.systems.modules.movement.elytrafly.ElytraFlightModes;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class Packet extends ElytraFlightMode {
    private final Vec3d vec3d = new Vec3d(0,0,0);

    public Packet() {
        super(ElytraFlightModes.Packet);
    }

    @Override
    public void onDeactivate() {
        Envy.mc.player.getAbilities().flying = false;
        Envy.mc.player.getAbilities().allowFlying = false;
    }

    @Override
    public void onTick() {
        if (Envy.mc.player.getInventory().getArmorStack(2).getItem() != Items.ELYTRA || Envy.mc.player.fallDistance <= 0.2 || Envy.mc.options.sneakKey.isPressed()) return;

        if (Envy.mc.options.forwardKey.isPressed()) {
            vec3d.add(0, 0, elytraFly.horizontalSpeed.get());
            vec3d.rotateY(-(float) Math.toRadians(Envy.mc.player.getYaw()));
        } else if (Envy.mc.options.backKey.isPressed()) {
            vec3d.add(0, 0, elytraFly.horizontalSpeed.get());
            vec3d.rotateY((float) Math.toRadians(Envy.mc.player.getYaw()));
        }

        if (Envy.mc.options.jumpKey.isPressed()) vec3d.add(0, elytraFly.verticalSpeed.get(), 0);
        else if (!Envy.mc.options.jumpKey.isPressed()) vec3d.add(0, -elytraFly.verticalSpeed.get(), 0);

        Envy.mc.player.setVelocity(vec3d);
        Envy.mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(Envy.mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
        Envy.mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
    }

    @Override
    public void onPacketSend(PacketEvent.Send event) {
        if (event.packet instanceof PlayerMoveC2SPacket) Envy.mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(Envy.mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
    }

    @Override
    public void onPlayerMove() {
        Envy.mc.player.getAbilities().flying = true;
        Envy.mc.player.getAbilities().setFlySpeed(elytraFly.horizontalSpeed.get().floatValue() / 20);
    }
}
