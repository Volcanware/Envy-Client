package mathax.client.systems.modules.misc;

import mathax.client.eventbus.EventHandler;
import mathax.client.events.packets.PacketEvent;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import mathax.client.systems.modules.Modules;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import org.apache.commons.compress.harmony.unpack200.bytecode.forms.SuperFieldRefForm;

import static mathax.client.MatHax.mc;

public class AutoGG extends Module {

    private TitleS2CPacket packet;

    public AutoGG() {
        super(Categories.Chat, Items.PAPER, "AutoGG", "Automatically says GG in chat when you win a game.");
    }

    @EventHandler
    public void onPacketSend(PacketEvent.Receive event) {
        if (event.packet instanceof TitleS2CPacket) {
            packet = (TitleS2CPacket) event.packet;
            if (packet.getTitle().getString().contains("Game") || packet.getTitle().getString().contains("Win") || packet.getTitle().getString().contains("Lose") || packet.getTitle().getString().contains("game") || packet.getTitle().getString().contains("win") || packet.getTitle().getString().contains("lose") || packet.getTitle().getString().contains("GAME") || packet.getTitle().getString().contains("WIN") || packet.getTitle().getString().contains("LOSE") || packet.getTitle().getString().contains("Victory")) {
                mc.player.sendChatMessage("gg");
            }
        }
    }
}
