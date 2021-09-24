package mathax.legacy.client.systems.modules.movement;

import mathax.legacy.client.events.packets.PacketEvent;
import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.bus.EventHandler;
import mathax.legacy.client.utils.entity.blink.BlinkPlayerCloneManager;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

import java.util.ArrayList;
import java.util.List;

public class Blink extends Module {
    private final List<PlayerMoveC2SPacket> packets = new ArrayList<>();
    private int timer = 0;

    public Blink() {
        super(Categories.Movement, Items.TINTED_GLASS, "blink", "Allows you to essentially teleport while suspending motion updates");
    }

    @Override
    public void onActivate() {
        BlinkPlayerCloneManager.add();
    }

    @Override
    public void onDeactivate() {
        synchronized (packets) {
            packets.forEach(p -> mc.player.networkHandler.sendPacket(p));
            packets.clear();
            timer = 0;
        }

        BlinkPlayerCloneManager.clear();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        timer++;
    }

    @EventHandler
    private void onSendPacket(PacketEvent.Send event) {
        if (!(event.packet instanceof PlayerMoveC2SPacket)) return;
        event.cancel();

        synchronized (packets) {
            PlayerMoveC2SPacket p = (PlayerMoveC2SPacket) event.packet;
            PlayerMoveC2SPacket prev = packets.size() == 0 ? null : packets.get(packets.size() - 1);

            if (prev != null &&
                    p.isOnGround() == prev.isOnGround() &&
                    p.getYaw(-1) == prev.getYaw(-1) &&
                    p.getPitch(-1) == prev.getPitch(-1) &&
                    p.getX(-1) == prev.getX(-1) &&
                    p.getY(-1) == prev.getY(-1) &&
                    p.getZ(-1) == prev.getZ(-1)
            ) return;

            packets.add(p);
        }
    }

    @Override
    public String getInfoString() {
        return String.format("%.1f", timer / 20f);
    }
}
