
package mathax.client.legacy.systems.modules.chat;

import mathax.client.legacy.MatHaxClientLegacy;
import mathax.client.legacy.bus.EventHandler;
import mathax.client.legacy.events.packets.PacketEvent;
import mathax.client.legacy.settings.BoolSetting;
import mathax.client.legacy.settings.EnumSetting;
import mathax.client.legacy.settings.Setting;
import mathax.client.legacy.settings.SettingGroup;
import mathax.client.legacy.systems.friends.Friends;
import mathax.client.legacy.systems.modules.Categories;
import mathax.client.legacy.systems.modules.Module;
import mathax.client.legacy.systems.modules.Modules;
import mathax.client.legacy.systems.modules.combat.BedAura;
import mathax.client.legacy.systems.modules.combat.CrystalAura;
import mathax.client.legacy.systems.modules.combat.CEVBreaker;
import mathax.client.legacy.utils.Utils;
import mathax.client.legacy.utils.entity.EntityUtils;
import mathax.client.legacy.utils.misc.placeholders.Placeholders;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;

public class AutoEZ extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

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
        super(Categories.Chat, "auto-EZ", "Announces when you kill someone.");
    }

    @EventHandler
    public void onPacketRead(PacketEvent.Receive event) {
        if (event.packet instanceof GameMessageS2CPacket) {
            String msg = ((GameMessageS2CPacket) event.packet).getMessage().getString();
            if (msg.contains("by " + mc.getSession().getUsername()) || msg.contains("whilst fighting " + mc.getSession().getUsername()) || msg.contains(mc.getSession().getUsername() + " sniped") || msg.contains(mc.getSession().getUsername() + " annaly fucked") || msg.contains(mc.getSession().getUsername() + " destroyed") || msg.contains(mc.getSession().getUsername() + " killed") || msg.contains(mc.getSession().getUsername() + " fucked") || msg.contains(mc.getSession().getUsername() + " separated") || msg.contains(mc.getSession().getUsername() + " punched") || msg.contains(mc.getSession().getUsername() + " shoved")) {
                for (PlayerEntity player : mc.world.getPlayers()) {
                    if (player == mc.player)
                        continue;
                    if (msg.contains(player.getName().getString())) {
                        if (msg.contains("end crystal") || msg.contains("end-crystal")) {
                            if (Modules.get().isActive(CrystalAura.class)) {
                                if (!Modules.get().isActive(CEVBreaker.class)) {
                                    if (mc.player.distanceTo(player) < Modules.get().get(CrystalAura.class).targetRange.get()) {
                                        String message = getCrystalMessageStyle();
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
                            if (mc.player.distanceTo(player) < 6) {
                                String message = getMessageStyle();
                                String toSendMessage = Placeholders.apply(message).replace("%killedperson%", player.getName().getString());
                                if (ignoreFriends.get() && Friends.get().isFriend(player)) return;
                                if (EntityUtils.getGameMode(player).isCreative()) return;
                                mc.player.sendChatMessage(toSendMessage.replace(Utils.getCoper(), Utils.getCoperReplacement()));
                            }
                        }
                    }
                }
            } else if (msg.contains("[Intentional Game Design]")) {
                for (PlayerEntity player : mc.world.getPlayers()) {
                    if (player == mc.player)
                        continue;
                    if (msg.contains(player.getName().getString())) {
                        if (Modules.get().isActive(BedAura.class)) {
                            if (mc.player.distanceTo(player) < Modules.get().get(BedAura.class).targetRange.get()) {
                                String message = getBedMessageStyle();
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
        if (mode.get() == Mode.GG) {
            return getGgMessage();
        } else {
            return getMessage();
        }
    }

    public String getCrystalMessageStyle() {
        if (mode.get() == Mode.GG) {
            return getGgCrystalMessage();
        } else {
            return getCrystalMessage();
        }
    }

    public String getBedMessageStyle() {
        if (mode.get() == Mode.GG) {
            return getGgBedMessage();
        } else {
            return getBedMessage();
        }
    }

    public String getMessage() {
        int randomNumber = Utils.random(0, 5);
        if (randomNumber == 1) {
            return "haha %killedperson% is a noob! MatHax Legacy on top!";
        } else if (randomNumber == 2) {
            return "I just EZZz'd %killedperson% using MatHax Legacy!";
        } else if (randomNumber == 3) {
            return "I just fucked %killedperson% using MatHax Legacy!";
        } else if (randomNumber == 4) {
            return "I just nae nae'd %killedperson% using MatHax Legacy!";
        } else if (randomNumber == 5) {
            return "Take the L nerd %killedperson%! You just got ended by MatHax Legacy!";
        } else {
            return "%killedperson% just got ended by MatHax Legacy!";
        }
    }

    public String getGgMessage() {
        int randomNumber = Utils.random(0, 3);
        if (randomNumber == 1) {
            return "Close fight %killedperson%, but MatHax Legacy helped me to win!";
        } else if (randomNumber == 2) {
            return "Good fight, %killedperson%! MatHax Legacy helped me alot!";
        } else if (randomNumber == 3) {
            return "GG %killedperson%! MatHax Legacy on top!";
        } else {
            return "Nice fight %killedperson%! MatHax Legacy is so good!";
        }
    }

    public String getCrystalMessage() {
        int randomNumber = Utils.random(0, 6);
        if (randomNumber == 1) {
            return "My crystal aura is too fast for %killedperson%! MatHax Legacy on top!";
        } else if (randomNumber == 2) {
            return "I just EZZz'd %killedperson% using MatHax Legacy crystal aura!";
        } else if (randomNumber == 3) {
            return "I just fucked %killedperson% using MatHax Legacy crystal aura!";
        } else if (randomNumber == 4) {
            return "haha %killedperson% is a noob! MatHax Legacy crystal aura on top!";
        } else if (randomNumber == 5) {
            return "I just nae nae'd %killedperson% using MatHax Legacy crystal aura!";
        } else if (randomNumber == 6) {
            return "Take the L nerd %killedperson%! You just got ended by MatHax Legacy crystal aura!";
        } else {
            return "%killedperson% just got ended by MatHax Legacy crystal aura!";
        }
    }

    public String getGgCrystalMessage() {
        int randomNumber = Utils.random(0, 3);
        if (randomNumber == 1) {
            return "Close fight %killedperson%, but MatHax Legacy crystal aura won!";
        } else if (randomNumber == 2) {
            return "Good fight, %killedperson%! MatHax Legacy crystal aura helped me!";
        } else if (randomNumber == 3) {
            return "GG %killedperson%! MatHax Legacy crystal aura on top!";
        } else {
            return "Nice fight %killedperson%! MatHax Legacy crystal aura is so good!";
        }
    }

    public String getBedMessage() {
        int randomNumber = Utils.random(0, 6);
        if (randomNumber == 1) {
            return "My bed aura is too fast for %killedperson%! MatHax Legacy on top!";
        } else if (randomNumber == 2) {
            return "I just EZZz'd %killedperson% using MatHax Legacy bed aura!";
        } else if (randomNumber == 3) {
            return "I just fucked %killedperson% using MatHax Legacy bed aura!";
        } else if (randomNumber == 4) {
            return "haha %killedperson% is a noob! MatHax Legacy bed aura on top!";
        } else if (randomNumber == 5) {
            return "I just nae nae'd %killedperson% using MatHax Legacy bed aura!";
        } else if (randomNumber == 6) {
            return "Take the L nerd %killedperson%! You just got ended by MatHax Legacy bed aura!";
        } else {
            return "%killedperson% just got ended by MatHax Legacy bed aura!";
        }
    }

    public String getGgBedMessage() {
        int randomNumber = Utils.random(0, 3);
        if (randomNumber == 1) {
            return "Close fight %killedperson%, but MatHax Legacy bed aura won!";
        } else if (randomNumber == 2) {
            return "Good fight, %killedperson%! MatHax Legacy bed aura helped me!";
        } else if (randomNumber == 3) {
            return "GG %killedperson%! MatHax Legacy bed aura on top!";
        } else {
            return "Nice fight %killedperson%! MatHax Legacy bed aura is so good!";
        }
    }

    public enum Mode {
        EZ,
        GG
    }
}
