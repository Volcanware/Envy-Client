
package mathax.client.legacy.systems.modules.chat;

import mathax.client.legacy.bus.EventHandler;
import mathax.client.legacy.events.game.GameJoinedEvent;
import mathax.client.legacy.events.packets.PacketEvent;
import mathax.client.legacy.settings.*;
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
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.*;

public class AutoEZ extends Module {
    private int totemsPopped = 0;

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

    /*private final Setting<Boolean> totemsEnabled = sgTotemPops.add(new BoolSetting.Builder()
        .name("enabled")
        .description("Toggles totem pop messages.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Double> totemsSendDelay = sgTotemPops.add(new DoubleSetting.Builder()
        .name("send-delay")
        .description("Amount of pops between messages.")
        .defaultValue(2)
        .min(2)
        .sliderMin(2)
        .sliderMax(100)
        .build()
    );

    private final Setting<Boolean> totemsIgnoreFriends = sgTotemPops.add(new BoolSetting.Builder()
        .name("ignore-friends")
        .description("Ignores friends.")
        .defaultValue(true)
        .build()
    );*/

    public AutoEZ() {
        super(Categories.Chat, Items.LIGHTNING_ROD, "auto-EZ", "Announces when you kill someone.");
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
                                        String message = getCrystalMessageStyle();
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
                        if ((msg.contains("bed") || msg.contains("[Intentional Game Design]")) && Modules.get().isActive(BedAura.class)) {
                            if (mc.player.distanceTo(player) < Modules.get().get(BedAura.class).targetRange.get()) {
                                String message = getBedMessageStyle();
                                String toSendMessage = Placeholders.apply(message).replace("%killedperson%", player.getName().getString());
                                if (ignoreFriends.get() && Friends.get().isFriend(player)) return;
                                if (EntityUtils.getGameMode(player).isCreative()) return;
                                mc.player.sendChatMessage(toSendMessage.replace(Utils.getCoper(), Utils.getCoperReplacement()));
                            }
                        } else if ((msg.contains("anchor") || msg.contains("[Intentional Game Design]")) && Modules.get().isActive(AnchorAura.class)) {
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
        switch (mode.get()) {
            case EZ: return getMessage();
            case GG: return getGgMessage();
            //case TROLL -> return getTrollMessage();
        }
        return "";
    }

    public String getCrystalMessageStyle() {
        switch (mode.get()) {
            case EZ: return getCrystalMessage();
            case GG: return getGgCrystalMessage();
            //case TROLL -> return getTrollCrystalMessage();
        }
        return "";
    }

    public String getBedMessageStyle() {
        switch (mode.get()) {
            case EZ: return getBedMessage();
            case GG: return getGgBedMessage();
            //case TROLL -> return getTrollBedMessage();
        }
        return "";
    }

    public String getAnchorMessageStyle() {
        switch (mode.get()) {
            case EZ: return getAnchorMessage();
            case GG: return getGgAnchorMessage();
            //case TROLL -> return getTrollAnchorMessage();
        }
        return "";
    }

    public String getMessage() {
        int randomNumber = Utils.random(1, 8);
        switch (randomNumber) {
            case 1: return "I am too good for %killedperson%! MatHax Legacy on top!";
            case 2: return "haha %killedperson% is a noob! MatHax Legacy on top!";
            case 3: return "I just EZZz'd %killedperson% using MatHax Legacy!";
            case 4: return "I just fucked %killedperson% using MatHax Legacy!";
            case 5: return "I just nae nae'd %killedperson% using MatHax Legacy!";
            case 6: return "Take the L nerd %killedperson%! You just got ended by MatHax Legacy!";
            case 7: return "%killedperson% just got raped by MatHax Legacy!";
            case 8: return "%killedperson% just got ended by MatHax Legacy!";
        }
        return "";
    }

    public String getGgMessage() {
        int randomNumber = Utils.random(1, 4);
        switch (randomNumber) {
            case 1: return "Close fight %killedperson%, but MatHax Legacy helped me to win!";
            case 2: return "Good fight, %killedperson%! MatHax Legacy helped me alot!";
            case 3: return "GG %killedperson%! MatHax Legacy on top!";
            case 4: return "Nice fight %killedperson%! MatHax Legacy is so good!";
        }
        return "";
    }

    public String getCrystalMessage() {
        int randomNumber = Utils.random(1, 8);
        switch (randomNumber) {
            case 1: return "My crystal aura is too fast for %killedperson%! MatHax Legacy on top!";
            case 2: return "I just EZZz'd %killedperson% using MatHax Legacy crystal aura!";
            case 3: return "I just fucked %killedperson% using MatHax Legacy crystal aura!";
            case 4: return "haha %killedperson% is a noob! MatHax Legacy crystal aura on top!";
            case 5: return "I just nae nae'd %killedperson% using MatHax Legacy crystal aura!";
            case 6: return "Take the L nerd %killedperson%! You just got ended by MatHax Legacy crystal aura!";
            case 7: return "%killedperson% just got raped by MatHax Legacy crystal aura!";
            case 8: return "%killedperson% just got ended by MatHax Legacy crystal aura!";
        }
        return "";
    }

    public String getGgCrystalMessage() {
        int randomNumber = Utils.random(1, 4);
        switch (randomNumber) {
            case 1: return "Close fight %killedperson%, but MatHax Legacy crystal aura won!";
            case 2: return "Good fight, %killedperson%! MatHax Legacy crystal aura helped me!";
            case 3: return "GG %killedperson%! MatHax Legacy crystal aura on top!";
            case 4: return "Nice fight %killedperson%! MatHax Legacy crystal aura is so good!";
        }
        return "";
    }

    public String getBedMessage() {
        int randomNumber = Utils.random(1, 8);
        switch (randomNumber) {
            case 1: return "My bed aura is too fast for %killedperson%! MatHax Legacy on top!";
            case 2: return "I just EZZz'd %killedperson% using MatHax Legacy bed aura!";
            case 3: return "I just fucked %killedperson% using MatHax Legacy bed aura!";
            case 4: return "haha %killedperson% is a noob! MatHax Legacy bed aura on top!";
            case 5: return "I just nae nae'd %killedperson% using MatHax Legacy bed aura!";
            case 6: return "Take the L nerd %killedperson%! You just got ended by MatHax Legacy bed aura!";
            case 7: return "%killedperson% just got raped by MatHax Legacy bed aura!";
            case 8: return "%killedperson% just got ended by MatHax Legacy bed aura!";
        }
        return "";
    }

    public String getGgBedMessage() {
        int randomNumber = Utils.random(1, 4);
        switch (randomNumber) {
            case 1: return "Close fight %killedperson%, but MatHax Legacy bed aura won!";
            case 2: return "Good fight, %killedperson%! MatHax Legacy bed aura helped me!";
            case 3: return "GG %killedperson%! MatHax Legacy bed aura on top!";
            case 4: return "Nice fight %killedperson%! MatHax Legacy bed aura is so good!";
        }
        return "";
    }

    public String getAnchorMessage() {
        int randomNumber = Utils.random(1, 8);
        switch (randomNumber) {
            case 1: return "My anchor aura is too fast for %killedperson%! MatHax Legacy on top!";
            case 2: return "I just EZZz'd %killedperson% using MatHax Legacy anchor aura!";
            case 3: return "I just fucked %killedperson% using MatHax Legacy anchor aura!";
            case 4: return "haha %killedperson% is a noob! MatHax Legacy anchor aura on top!";
            case 5: return "I just nae nae'd %killedperson% using MatHax Legacy anchor aura!";
            case 6: return "Take the L nerd %killedperson%! You just got ended by MatHax Legacy anchor aura!";
            case 7: return "%killedperson% just got raped by MatHax Legacy anchor aura!";
            case 8: return "%killedperson% just got ended by MatHax Legacy anchor aura!";
        }
        return "";
    }

    public String getGgAnchorMessage() {
        int randomNumber = Utils.random(1, 4);
        switch (randomNumber) {
            case 1: return "Close fight %killedperson%, but MatHax Legacy anchor aura won!";
            case 2: return "Good fight, %killedperson%! MatHax Legacy anchor aura helped me!";
            case 3: return "GG %killedperson%! MatHax Legacy anchor aura on top!";
            case 4: return "Nice fight %killedperson%! MatHax Legacy anchor aura is so good!";
        }
        return "";
    }

    public enum Mode {
        EZ,
        GG/*,
        TROLL*/
    }

    // TOTEM POPS

    @EventHandler
    private void onTotemPop(PacketEvent.Receive event) {
        /*if (!totemsEnabled.get()) return;
        if (!(event.packet instanceof EntityStatusS2CPacket)) return;

        EntityStatusS2CPacket packet = (EntityStatusS2CPacket) event.packet;
        if (packet.getStatus() != 35) return;

        Entity entity = packet.getEntity(mc.world);

        if (!(entity instanceof PlayerEntity)) return;

        if (!(Friends.get().isFriend(((PlayerEntity) entity)) && totemsIgnoreFriends.get())) return;

        if (totemsPopped > Math.round(totemsSendDelay.get())) totemsPopped = 0;
        if (totemsPopped == Math.round(totemsSendDelay.get()) || totemsPopped == 0) {
            if (mc.player.distanceTo(entity) < 8) {
                String message = getTotemMessage();
                String toSendMessage = Placeholders.apply(message).replace("%poppedperson%", entity.getName().getString());
                mc.player.sendChatMessage(toSendMessage.replace(Utils.getCoper(), Utils.getCoperReplacement()));
            }
            totemsPopped = 1;
        } else {
            ++totemsPopped;
        }*/
    }

    public String getTotemMessage() {
        int randomNumber = Utils.random(1, 8);
        switch (randomNumber) {
            case 1: return "I am too good for %poppedperson%, pop more! MatHax Legacy on top!";
            case 2: return "haha %poppedperson% is a noob, easy pop! MatHax Legacy on top!";
            case 3: return "I just EZZz'd %poppedperson%'s totem using MatHax Legacy!";
            case 4: return "I just fucked %poppedperson%'S totem using MatHax Legacy!";
            case 5: return "I just nae nae'd %poppedperson%'s totem using MatHax Legacy!";
            case 6: return "Take the L nerd %poppedperson%! Your totem just got ended by MatHax Legacy!";
            case 7: return "%poppedperson%'s totem just got raped by MatHax Legacy!";
            case 8: return "%poppedperson% just got popped by MatHax Legacy!";
        }
        return "";
    }

    @EventHandler
    public void onActivate() {
        // TOTEM POPS
        totemsPopped = 0;
    }

    @EventHandler
    private void onGameJoin(GameJoinedEvent event) {
        // TOTEM POPS
        totemsPopped = 0;
    }
}
