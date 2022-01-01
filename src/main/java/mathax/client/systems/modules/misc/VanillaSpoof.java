package mathax.client.systems.modules.misc;

import io.netty.buffer.Unpooled;
import mathax.client.MatHax;
import mathax.client.eventbus.EventHandler;
import mathax.client.events.packets.PacketEvent;
import mathax.client.mixin.ICustomPayloadC2SPacketAccessor;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;

import java.nio.charset.StandardCharsets;

public class VanillaSpoof extends Module {
    public VanillaSpoof() {
        super(Categories.Misc, Items.COMMAND_BLOCK, "vanilla-spoof", "When connecting to a server it spoofs the client name to be 'vanilla'.");

        MatHax.EVENT_BUS.subscribe(new Listener());
    }

    private class Listener {
        @EventHandler
        private void onPacketSend(PacketEvent.Send event) {
            if (!isActive()) return;
            if (event.packet instanceof CustomPayloadC2SPacket packet) {
                ICustomPayloadC2SPacketAccessor accessor = (ICustomPayloadC2SPacketAccessor) packet;
                if (accessor.getChannel().equals(CustomPayloadC2SPacket.BRAND)) accessor.setData(new PacketByteBuf(Unpooled.buffer()).writeString("vanilla"));
                else if (accessor.getData().toString(StandardCharsets.UTF_8).toLowerCase().contains("fabric")) event.setCancelled(true);
            }
        }
    }
}
