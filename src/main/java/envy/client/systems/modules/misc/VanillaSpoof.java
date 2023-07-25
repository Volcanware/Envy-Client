package envy.client.systems.modules.misc;

import envy.client.Envy;
import envy.client.eventbus.EventHandler;
import envy.client.events.packets.PacketEvent;
import envy.client.mixin.CustomPayloadC2SPacketAccessor;
import envy.client.settings.Setting;
import envy.client.settings.SettingGroup;
import envy.client.settings.StringSetting;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import io.netty.buffer.Unpooled;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;

public class VanillaSpoof extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<String> brand = sgGeneral.add(new StringSetting.Builder()
        .name("brand")
        .description("Specify the brand that will be send to the server.")
        .defaultValue("vanilla")
        .build()
    );

    public VanillaSpoof() {
        super(Categories.Misc, Items.COMMAND_BLOCK, "vanilla-spoof", "Spoofs the client name when connecting to a server.");

        Envy.EVENT_BUS.subscribe(new Listener());
    }

    private class Listener {
        @EventHandler
        private void onPacketSend(PacketEvent.Send event) {
            if (!isActive() || !(event.packet instanceof CustomPayloadC2SPacket)) return;

            CustomPayloadC2SPacketAccessor packet = (CustomPayloadC2SPacketAccessor) event.packet;
            Identifier id = packet.getChannel();

            if (id.equals(CustomPayloadC2SPacket.BRAND)) packet.setData(new PacketByteBuf(Unpooled.buffer()).writeString(brand.get()));
            else if (StringUtils.containsIgnoreCase(packet.getData().toString(StandardCharsets.UTF_8), "fabric") && brand.get().equalsIgnoreCase("fabric")) event.cancel();
        }
    }
}
