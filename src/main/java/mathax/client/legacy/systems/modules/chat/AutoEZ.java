
package mathax.client.legacy.systems.modules.chat;

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
import mathax.client.legacy.systems.modules.combat.AnchorAura;
import mathax.client.legacy.systems.modules.combat.BedAura;
import mathax.client.legacy.systems.modules.combat.CrystalAura;
import mathax.client.legacy.systems.modules.combat.CEVBreaker;
import mathax.client.legacy.utils.Utils;
import mathax.client.legacy.utils.entity.EntityUtils;
import mathax.client.legacy.utils.placeholders.Placeholders;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;

public class AutoEZ extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    //private final SettingGroup sgTotemPops = settings.createGroup("Totem Pops");

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

    //Totem Pops

    /*private final Setting<Boolean> ignoreFriendsTotem = sgTotemPops.add(new BoolSetting.Builder()
        .name("ignore-friends")
        .description("Ignores friends.")
        .defaultValue(true)
        .build()
    );*/

    public AutoEZ() {
        super(Categories.Chat, "auto-EZ", "Announces when you kill someone.");
    }

    // KILL

    @EventHandler
    public void onPacketReadMessage(PacketEvent.Receive event) {
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
                                } else if (mc.player.distanceTo(player) < 6) {
                                    String message = getMessageStyle();
                                    String toSendMessage = Placeholders.apply(message).replace("%killedperson%", player.getName().getString());
                                    if (ignoreFriends.get() && Friends.get().isFriend(player)) return;
                                    if (EntityUtils.getGameMode(player).isCreative()) return;
                                    mc.player.sendChatMessage(toSendMessage.replace(Utils.getCoper(), Utils.getCoperReplacement()));
                                }
                            } else if (mc.player.distanceTo(player) < 8) {
                                String message = getMessageStyle();
                                String toSendMessage = Placeholders.apply(message).replace("%killedperson%", player.getName().getString());
                                if (ignoreFriends.get() && Friends.get().isFriend(player)) return;
                                if (EntityUtils.getGameMode(player).isCreative()) return;
                                mc.player.sendChatMessage(toSendMessage.replace(Utils.getCoper(), Utils.getCoperReplacement()));
                            }
                        } else if (mc.player.distanceTo(player) < 6) {
                            String message = getMessageStyle();
                            String toSendMessage = Placeholders.apply(message).replace("%killedperson%", player.getName().getString());
                            if (ignoreFriends.get() && Friends.get().isFriend(player)) return;
                            if (EntityUtils.getGameMode(player).isCreative()) return;
                            mc.player.sendChatMessage(toSendMessage.replace(Utils.getCoper(), Utils.getCoperReplacement()));
                        }
                    }
                }
            } else {
                for (PlayerEntity player : mc.world.getPlayers()) {
                    if (player == mc.player)
                        continue;
                    if (msg.contains(player.getName().getString())) {
                        if (msg.contains("bed") || msg.contains("[Intentional Game Design]") && Modules.get().isActive(BedAura.class)) {
                            if (mc.player.distanceTo(player) < Modules.get().get(BedAura.class).targetRange.get()) {
                                String message = getBedMessageStyle();
                                String toSendMessage = Placeholders.apply(message).replace("%killedperson%", player.getName().getString());
                                if (ignoreFriends.get() && Friends.get().isFriend(player)) return;
                                if (EntityUtils.getGameMode(player).isCreative()) return;
                                mc.player.sendChatMessage(toSendMessage.replace(Utils.getCoper(), Utils.getCoperReplacement()));
                            }
                        } else if (msg.contains("anchor") || msg.contains("[Intentional Game Design]") && Modules.get().isActive(AnchorAura.class)) {
                            if (mc.player.distanceTo(player) < Modules.get().get(AnchorAura.class).targetRange.get()) {
                                String message = getAnchorMessageStyle();
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
        String msg = "";
        switch (mode.get()) {
            case EZ -> msg = getMessage();
            case GG -> msg = getGgMessage();
            //case TROLL -> msg = getTrollMessage();
        }
        return msg;
    }

    public String getCrystalMessageStyle() {
        String msg = "";
        switch (mode.get()) {
            case EZ -> msg = getCrystalMessage();
            case GG -> msg = getGgCrystalMessage();
            //case TROLL -> msg = getTrollCrystalMessage();
        }
        return msg;
    }

    public String getBedMessageStyle() {
        String msg = "";
        switch (mode.get()) {
            case EZ -> msg = getBedMessage();
            case GG -> msg = getGgBedMessage();
            //case TROLL -> msg = getTrollBedMessage();
        }
        return msg;
    }

    public String getAnchorMessageStyle() {
        String msg = "";
        switch (mode.get()) {
            case EZ -> msg = getAnchorMessage();
            case GG -> msg = getGgAnchorMessage();
            //case TROLL -> msg = getTrollAnchorMessage();
        }
        return msg;
    }

    public String getMessage() {
        String msg = "%killedperson% just got ended by MatHax Legacy!";
        int randomNumber = Utils.random(1, 8);
        switch (randomNumber) {
            case 1: msg = "I am too good for %killedperson%! MatHax Legacy on top!";
            case 2: msg = "haha %killedperson% is a noob! MatHax Legacy on top!";
            case 3: msg = "I just EZZz'd %killedperson% using MatHax Legacy!";
            case 4: msg = "I just fucked %killedperson% using MatHax Legacy!";
            case 5: msg = "I just nae nae'd %killedperson% using MatHax Legacy!";
            case 6: msg = "Take the L nerd %killedperson%! You just got ended by MatHax Legacy!";
            case 7: msg = "%killedperson% just got raped by MatHax Legacy!";
            case 8: msg = "%killedperson% just got ended by MatHax Legacy!";
        }
        return msg;
    }

    public String getGgMessage() {
        String msg = "Nice fight %killedperson%! MatHax Legacy is so good!";
        int randomNumber = Utils.random(1, 4);
        switch (randomNumber) {
            case 1: msg = "Close fight %killedperson%, but MatHax Legacy helped me to win!";
            case 2: msg = "Good fight, %killedperson%! MatHax Legacy helped me alot!";
            case 3: msg = "GG %killedperson%! MatHax Legacy on top!";
            case 4: msg = "Nice fight %killedperson%! MatHax Legacy is so good!";
        }
        return msg;
    }

    public String getCrystalMessage() {
        String msg = "%killedperson% just got ended by MatHax Legacy crystal aura!";
        int randomNumber = Utils.random(1, 8);
        switch (randomNumber) {
            case 1: msg = "My crystal aura is too fast for %killedperson%! MatHax Legacy on top!";
            case 2: msg = "I just EZZz'd %killedperson% using MatHax Legacy crystal aura!";
            case 3: msg = "I just fucked %killedperson% using MatHax Legacy crystal aura!";
            case 4: msg = "haha %killedperson% is a noob! MatHax Legacy crystal aura on top!";
            case 5: msg = "I just nae nae'd %killedperson% using MatHax Legacy crystal aura!";
            case 6: msg = "Take the L nerd %killedperson%! You just got ended by MatHax Legacy crystal aura!";
            case 7: msg = "%killedperson% just got raped by MatHax Legacy crystal aura!";
            case 8: msg = "%killedperson% just got ended by MatHax Legacy crystal aura!";
        }
        return msg;
    }

    public String getGgCrystalMessage() {
        String msg = "Nice fight %killedperson%! MatHax Legacy crystal aura is so good!";
        int randomNumber = Utils.random(1, 4);
        switch (randomNumber) {
            case 1: msg = "Close fight %killedperson%, but MatHax Legacy crystal aura won!";
            case 2: msg = "Good fight, %killedperson%! MatHax Legacy crystal aura helped me!";
            case 3: msg = "GG %killedperson%! MatHax Legacy crystal aura on top!";
            case 4: msg = "Nice fight %killedperson%! MatHax Legacy crystal aura is so good!";
        }
        return msg;
    }

    public String getBedMessage() {
        String msg = "Take the L nerd %killedperson%! You just got ended by MatHax Legacy bed aura!";
        int randomNumber = Utils.random(1, 8);
        switch (randomNumber) {
            case 1: msg = "My bed aura is too fast for %killedperson%! MatHax Legacy on top!";
            case 2: msg = "I just EZZz'd %killedperson% using MatHax Legacy bed aura!";
            case 3: msg = "I just fucked %killedperson% using MatHax Legacy bed aura!";
            case 4: msg = "haha %killedperson% is a noob! MatHax Legacy bed aura on top!";
            case 5: msg = "I just nae nae'd %killedperson% using MatHax Legacy bed aura!";
            case 6: msg = "Take the L nerd %killedperson%! You just got ended by MatHax Legacy bed aura!";
            case 7: msg = "%killedperson% just got raped by MatHax Legacy bed aura!";
            case 8: msg = "%killedperson% just got ended by MatHax Legacy bed aura!";
        }
        return msg;
    }

    public String getGgBedMessage() {
        String msg = "Nice fight %killedperson%! MatHax Legacy bed aura is so good!";
        int randomNumber = Utils.random(1, 4);
        switch (randomNumber) {
            case 1: msg = "Close fight %killedperson%, but MatHax Legacy bed aura won!";
            case 2: msg = "Good fight, %killedperson%! MatHax Legacy bed aura helped me!";
            case 3: msg = "GG %killedperson%! MatHax Legacy bed aura on top!";
            case 4: msg = "Nice fight %killedperson%! MatHax Legacy bed aura is so good!";
        }
        return msg;
    }

    public String getAnchorMessage() {
        String msg = "Take the L nerd %killedperson%! You just got ended by MatHax Legacy anchor aura!";
        int randomNumber = Utils.random(1, 8);
        switch (randomNumber) {
            case 1: msg = "My anchor aura is too fast for %killedperson%! MatHax Legacy on top!";
            case 2: msg = "I just EZZz'd %killedperson% using MatHax Legacy anchor aura!";
            case 3: msg = "I just fucked %killedperson% using MatHax Legacy anchor aura!";
            case 4: msg = "haha %killedperson% is a noob! MatHax Legacy anchor aura on top!";
            case 5: msg = "I just nae nae'd %killedperson% using MatHax Legacy anchor aura!";
            case 6: msg = "Take the L nerd %killedperson%! You just got ended by MatHax Legacy anchor aura!";
            case 7: msg = "%killedperson% just got raped by MatHax Legacy anchor aura!";
            case 8: msg = "%killedperson% just got ended by MatHax Legacy anchor aura!";
        }
        return msg;
    }

    public String getGgAnchorMessage() {
        String msg = "Nice fight %killedperson%! MatHax Legacy anchor aura is so good!";
        int randomNumber = Utils.random(1, 4);
        switch (randomNumber) {
            case 1: msg = "Close fight %killedperson%, but MatHax Legacy anchor aura won!";
            case 2: msg = "Good fight, %killedperson%! MatHax Legacy anchor aura helped me!";
            case 3: msg = "GG %killedperson%! MatHax Legacy anchor aura on top!";
            case 4: msg = "Nice fight %killedperson%! MatHax Legacy anchor aura is so good!";
        }
        return msg;
    }

    public enum Mode {
        EZ,
        GG/*,
        TROLL*/
    }

    // TOTEM POP
/*
    @EventHandler
    private void onTotemPop() {
        //TODO: Totem pop detection
    }

    public String getTotemMessage() {
        String msg = "%killedperson%'s totem just got ended by MatHax Legacy!";
        int randomNumber = Utils.random(1, 8);
        switch (randomNumber) {
            case 1: msg = "I am too good for %killedperson%, pop more! MatHax Legacy on top!";
            case 2: msg = "haha %killedperson% is a noob, easy pops! MatHax Legacy on top!";
            case 3: msg = "I just EZZz'd %killedperson%'s totem using MatHax Legacy!";
            case 4: msg = "I just fucked %killedperson%'S totem using MatHax Legacy!";
            case 5: msg = "I just nae nae'd %killedperson%'s totem using MatHax Legacy!";
            case 6: msg = "Take the L nerd %killedperson%! Your totem just got ended by MatHax Legacy!";
            case 7: msg = "%killedperson%'s totem just got raped by MatHax Legacy!";
            case 8: msg = "%killedperson% just got popped by MatHax Legacy!";
        }
        return msg;
    }*/
}
