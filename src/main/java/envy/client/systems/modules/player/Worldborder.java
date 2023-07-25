package envy.client.systems.modules.player;

import envy.client.eventbus.EventHandler;
import envy.client.events.packets.PacketEvent;
import envy.client.settings.SettingGroup;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.*;


public class Worldborder extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public Worldborder() {
        super(Categories.Player, Items.DIAMOND_BOOTS, "WorldBorder", "Let you disable the worldborder client-side");
    }

    @EventHandler
    void onReceivedPacket(PacketEvent.Receive event) {
        if ((event.packet instanceof WorldBorderCenterChangedS2CPacket || event.packet instanceof WorldBorderSizeChangedS2CPacket
            || event.packet instanceof WorldBorderInitializeS2CPacket
            || event.packet instanceof WorldBorderInterpolateSizeS2CPacket
            || event.packet instanceof WorldBorderWarningBlocksChangedS2CPacket)) {
            event.cancel();
        }
    }
}

