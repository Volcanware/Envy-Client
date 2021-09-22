
package mathax.legacy.client.systems.modules.chat;

import mathax.legacy.client.bus.EventHandler;
import mathax.legacy.client.events.packets.PacketEvent;
import mathax.legacy.client.settings.*;
import mathax.legacy.client.systems.friends.Friends;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.systems.modules.Modules;
import mathax.legacy.client.systems.modules.combat.*;
import mathax.legacy.client.utils.Utils;
import mathax.legacy.client.utils.entity.EntityUtils;
import mathax.legacy.client.utils.misc.Placeholders;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.*;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class AutoEZ extends Module {
    private final Random random = new Random();

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    //General

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("Determines what message style to use.")
        .defaultValue(Mode.EZ)
        .build()
    );

    private final Setting<Boolean> ignoreFriends = sgGeneral.add(new BoolSetting.Builder()
        .name("ignore-friends")
        .description("Ignores friends.")
        .defaultValue(true)
        .build()
    );

    public AutoEZ() {
        super(Categories.Chat, Items.LIGHTNING_ROD, "auto-EZ", "Announces in chat when you kill someone.");
    }

    // KILL

    @EventHandler
    public void onPacketReadMessage(PacketEvent.Receive event) {
        if (event.packet instanceof GameMessageS2CPacket) {
            String msg = ((GameMessageS2CPacket) event.packet).getMessage().getString();
            for (PlayerEntity player : mc.world.getPlayers()) {
                if (player == mc.player)
                    continue;
                if (player.getName().getString().equals(mc.getSession().getUsername())) return;
                if (msg.contains(player.getName().getString())) {
                    if (msg.contains("by " + mc.getSession().getUsername()) || msg.contains("whilst fighting " + mc.getSession().getUsername()) || msg.contains(mc.getSession().getUsername() + " sniped") || msg.contains(mc.getSession().getUsername() + " annaly fucked") || msg.contains(mc.getSession().getUsername() + " destroyed") || msg.contains(mc.getSession().getUsername() + " killed") || msg.contains(mc.getSession().getUsername() + " fucked") || msg.contains(mc.getSession().getUsername() + " separated") || msg.contains(mc.getSession().getUsername() + " punched") || msg.contains(mc.getSession().getUsername() + " shoved")) {
                        if (msg.contains("end crystal") || msg.contains("end-crystal")) {
                            if (Modules.get().isActive(CrystalAura.class)) {
                                if (!Modules.get().isActive(CEVBreaker.class)) {
                                    if (mc.player.distanceTo(player) < Modules.get().get(CrystalAura.class).targetRange.get()) {
                                        String message = getMessageStyle();
                                        String toSendMessage = Placeholders.apply(message).replace("%killedperson%", player.getName().getString());
                                        if (ignoreFriends.get() && Friends.get().isFriend(player)) return;
                                        if (EntityUtils.getGameMode(player).isCreative()) return;
                                        mc.player.sendChatMessage(toSendMessage.replace(Utils.getCoper(), Utils.getCoperReplacement()));
                                    }
                                } else {
                                    if (mc.player.distanceTo(player) < 5) {
                                        String message = getMessageStyle();
                                        String toSendMessage = Placeholders.apply(message).replace("%killedperson%", player.getName().getString());
                                        if (ignoreFriends.get() && Friends.get().isFriend(player)) return;
                                        if (EntityUtils.getGameMode(player).isCreative()) return;
                                        mc.player.sendChatMessage(toSendMessage.replace(Utils.getCoper(), Utils.getCoperReplacement()));
                                    }
                                }
                            } else {
                                if (mc.player.distanceTo(player) < 7) {
                                    String message = getMessageStyle();
                                    String toSendMessage = Placeholders.apply(message).replace("%killedperson%", player.getName().getString());
                                    if (ignoreFriends.get() && Friends.get().isFriend(player)) return;
                                    if (EntityUtils.getGameMode(player).isCreative()) return;
                                    mc.player.sendChatMessage(toSendMessage.replace(Utils.getCoper(), Utils.getCoperReplacement()));
                                }
                            }
                        } else {
                            if (mc.player.distanceTo(player) < 8) {
                                String message = getMessageStyle();
                                String toSendMessage = Placeholders.apply(message).replace("%killedperson%", player.getName().getString());
                                if (ignoreFriends.get() && Friends.get().isFriend(player)) return;
                                if (EntityUtils.getGameMode(player).isCreative()) return;
                                mc.player.sendChatMessage(toSendMessage.replace(Utils.getCoper(), Utils.getCoperReplacement()));
                            }
                        }
                    } else {
                        if ((msg.contains("bed") || msg.contains("[Intentional Game Design]")) && (Modules.get().isActive(BedAura.class))) {
                            if ((mc.player.distanceTo(player) < Modules.get().get(BedAura.class).targetRange.get())) {
                                String message = getMessageStyle();
                                String toSendMessage = Placeholders.apply(message).replace("%killedperson%", player.getName().getString());
                                if (ignoreFriends.get() && Friends.get().isFriend(player)) return;
                                if (EntityUtils.getGameMode(player).isCreative()) return;
                                mc.player.sendChatMessage(toSendMessage.replace(Utils.getCoper(), Utils.getCoperReplacement()));
                            }
                        } else if ((msg.contains("anchor") || msg.contains("[Intentional Game Design]")) && Modules.get().isActive(AnchorAura.class)) {
                            if (mc.player.distanceTo(player) < Modules.get().get(AnchorAura.class).targetRange.get()) {
                                String message = getMessageStyle();
                                String toSendMessage = Placeholders.apply(message).replace("%killedperson%", player.getName().getString());
                                if (ignoreFriends.get() && Friends.get().isFriend(player)) return;
                                if (EntityUtils.getGameMode(player).isCreative()) return;
                                mc.player.sendChatMessage(toSendMessage.replace(Utils.getCoper(), Utils.getCoperReplacement()));
                            }
                        }
                    }
                }
            }
        }
    }

    public String getMessageStyle() {
        switch (mode.get()) {
            case EZ:
                return getMessage().get(random.nextInt(getMessage().size()));
            case GG:
                return getGGMessage().get(random.nextInt(getGGMessage().size()));
        }
        return "";
    }

    private static List<String> getMessage() {
        return Arrays.asList(
            "%killedperson% just got raped by MatHax Legacy!",
            "%killedperson% just got ended by MatHax Legacy!",
            "haha %killedperson% is a noob! MatHax Legacy on top!",
            "I just EZZz'd %killedperson% using MatHax Legacy!",
            "I just fucked %killedperson% using MatHax Legacy!",
            "I just nae nae'd %killedperson% using MatHax Legacy!",
            "Take the L nerd %killedperson%! You just got ended by MatHax Legacy!",
            "I am too good for %killedperson%! MatHax Legacy on top!"
        );
    }

    private static List<String> getGGMessage() {
        return Arrays.asList(
            "GG %killedperson%! MatHax Legacy is so op!",
            "Close fight %killedperson%, but i won!",
            "Good fight, %killedperson%! MatHax Legacy on top!",
            "Nice fight %killedperson%! I really enjoyed it!"
        );
    }

    public enum Mode {
        EZ,
        GG
    }
}
