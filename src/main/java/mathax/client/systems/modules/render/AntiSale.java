package mathax.client.systems.modules.render;

import baritone.api.event.events.ChatEvent;
import com.mojang.brigadier.Message;
import mathax.client.eventbus.EventHandler;
import mathax.client.systems.modules.Categories;
import mathax.client.utils.misc.ChatUtils;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import mathax.client.systems.modules.Modules;
import mathax.client.systems.modules.Module;
import mathax.client.events.packets.PacketEvent;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;

public class AntiSale extends Module {

    private TitleS2CPacket packet;

    @EventHandler
    public void onPacketSend(PacketEvent.Receive event) {
        if (event.packet instanceof TitleS2CPacket) {
            packet = (TitleS2CPacket) event.packet;
            if (packet.getTitle().getString().contains("SALE") || packet.getTitle().getString().contains("sale") || packet.getTitle().getString().contains("Sale")) {
                event.cancel();
            }
        }
        if (event.packet instanceof GameMessageS2CPacket) {
            GameMessageS2CPacket packet = (GameMessageS2CPacket) event.packet;
            if (packet.toString().contains("SALE") || packet.toString().contains("sale") || packet.toString().contains("Sale")  || packet.toString().contains("LOWBALLING") || packet.toString().contains("lowballing") || packet.toString().contains("Lowballing") || packet.toString().contains("LowBalling") || packet.toString().contains("LoWbAlLiNg") || packet.toString().contains("lOwBaLlInG") || packet.toString().contains("Giveaway") || packet.toString().contains("Free") || packet.toString().contains("Tebex") || packet.toString().contains("$") || packet.toString().contains("%") || packet.toString().contains("Selling") || packet.toString().contains("Free Rank") || packet.toString().contains("Webstore") || packet.toString().contains("Sell") || packet.toString().contains("cheap") || packet.toString().contains("check ah") || packet.toString().contains("Rank Upgrade") || packet.toString().contains("AltShop") || packet.toString().contains("Shop")) {
                event.cancel();
            }
        }
    }
    public AntiSale() {
        super(Categories.Misc, Items.BARRIER, "Anti-Sale", "AD blocker For BlockGame");
    }
}