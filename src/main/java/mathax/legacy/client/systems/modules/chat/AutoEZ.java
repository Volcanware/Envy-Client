
package mathax.legacy.client.systems.modules.chat;

import mathax.legacy.client.eventbus.EventHandler;
import mathax.legacy.client.events.packets.PacketEvent;
import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.gui.GuiTheme;
import mathax.legacy.client.gui.widgets.WWidget;
import mathax.legacy.client.gui.widgets.containers.WTable;
import mathax.legacy.client.gui.widgets.input.WTextBox;
import mathax.legacy.client.gui.widgets.pressable.WMinus;
import mathax.legacy.client.gui.widgets.pressable.WPlus;
import mathax.legacy.client.settings.*;
import mathax.legacy.client.systems.friends.Friends;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.systems.modules.Modules;
import mathax.legacy.client.systems.modules.combat.*;
import mathax.legacy.client.utils.entity.EntityUtils;
import mathax.legacy.client.utils.misc.Placeholders;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.s2c.play.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class AutoEZ extends Module {
    private final List<String> killMessages = new ArrayList<>();
    private final List<String> popMessages = new ArrayList<>();

    private String newKillMsgText = "%killed_player%";
    private String newPopMsgText = "%killed_player%";

    private boolean canSendPop = true;
    private int ticks;

    private final Random random = new Random();

    private final SettingGroup sgKills = settings.createGroup("Kills");
    private final SettingGroup sgTotemPops = settings.createGroup("Totem Pops");

    // Kills

    private final Setting<Boolean> kills = sgKills.add(new BoolSetting.Builder()
        .name("enabled")
        .description("Enables the kill messages.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Mode> mode = sgKills.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("Determines what messages to use.")
        .defaultValue(Mode.MatHax)
        .build()
    );

    private final Setting<MessageStyle> messageStyle = sgKills.add(new EnumSetting.Builder<MessageStyle>()
        .name("style")
        .description("Determines what message style to use.")
        .defaultValue(MessageStyle.EZ)
        .visible(() -> mode.get() == Mode.MatHax)
        .build()
    );

    private final Setting<Boolean> ignoreFriends = sgKills.add(new BoolSetting.Builder()
        .name("ignore-friends")
        .description("Ignores friends.")
        .defaultValue(true)
        .build()
    );

    // Totem Pops

    private final Setting<Boolean> totems = sgTotemPops.add(new BoolSetting.Builder()
        .name("enabled")
        .description("Enables the totem pop messages.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Mode> totemMode = sgTotemPops.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("Determines what messages to use.")
        .defaultValue(Mode.MatHax)
        .build()
    );

    private final Setting<Boolean> totemIgnoreFriends = sgTotemPops.add(new BoolSetting.Builder()
        .name("ignore-friends")
        .description("Ignores friends.")
        .defaultValue(true)
        .build()
    );

    public AutoEZ() {
        super(Categories.Chat, Items.LIGHTNING_ROD, "auto-ez", "Announces EASY when you kill or pop someone.");
    }

    @Override
    public WWidget getWidget(GuiTheme theme) {
        WTable table = theme.table();
        killMessagesFillTable(theme, table);
        table.row();
        popMessagesFillTable(theme, table);

        return table;
    }

    private void killMessagesFillTable(GuiTheme theme, WTable table) {
        table.add(theme.horizontalSeparator("Custom Kill Messages")).expandX();
        table.row();

        // Messages
        killMessages.removeIf(String::isEmpty);

        for (int i = 0; i < killMessages.size(); i++) {
            int msgI = i;
            String killMessage = killMessages.get(i);

            WTextBox textBox = table.add(theme.textBox(killMessage)).expandX().widget();
            textBox.action = () -> killMessages.set(msgI, textBox.get());

            WMinus delete = table.add(theme.minus()).widget();
            delete.action = () -> {
                killMessages.remove(msgI);

                table.clear();
                killMessagesFillTable(theme, table);
            };

            table.row();
        }

        if (!killMessages.isEmpty()) {
            table.add(theme.horizontalSeparator()).expandX();
            table.row();
        }

        // New Message
        WTextBox textBox = table.add(theme.textBox(newKillMsgText)).minWidth(100).expandX().widget();
        textBox.action = () -> newKillMsgText = textBox.get();

        WPlus add = table.add(theme.plus()).widget();
        add.action = () -> {
            killMessages.add(newKillMsgText);
            newKillMsgText = "%killed_player%";

            table.clear();
            killMessagesFillTable(theme, table);
        };
    }

    private void popMessagesFillTable(GuiTheme theme, WTable table) {
        table.add(theme.horizontalSeparator("Custom Pop Messages")).expandX();
        table.row();

        // Messages
        popMessages.removeIf(String::isEmpty);

        for (int i = 0; i < popMessages.size(); i++) {
            int msgI = i;
            String popMessage = popMessages.get(i);

            WTextBox textBox = table.add(theme.textBox(popMessage)).expandX().widget();
            textBox.action = () -> popMessages.set(msgI, textBox.get());

            WMinus delete = table.add(theme.minus()).widget();
            delete.action = () -> {
                popMessages.remove(msgI);

                table.clear();
                popMessagesFillTable(theme, table);
            };

            table.row();
        }

        if (!popMessages.isEmpty()) {
            table.add(theme.horizontalSeparator()).expandX();
            table.row();
        }

        // New Message
        WTextBox textBox = table.add(theme.textBox(newPopMsgText)).minWidth(100).expandX().widget();
        textBox.action = () -> newPopMsgText = textBox.get();

        WPlus add = table.add(theme.plus()).widget();
        add.action = () -> {
            popMessages.add(newPopMsgText);
            newPopMsgText = "%popped_player%";

            table.clear();
            popMessagesFillTable(theme, table);
        };
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = super.toTag();

        killMessages.removeIf(String::isEmpty);
        NbtList killMessagesTag = new NbtList();

        popMessages.removeIf(String::isEmpty);
        NbtList popMessagesTag = new NbtList();

        for (String killMessage : killMessages) killMessagesTag.add(NbtString.of(killMessage));
        tag.put("killMessages", killMessagesTag);

        for (String popMessage : popMessages) popMessagesTag.add(NbtString.of(popMessage));
        tag.put("popMessages", popMessagesTag);

        return tag;
    }

    @Override
    public Module fromTag(NbtCompound tag) {
        killMessages.clear();
        popMessages.clear();

        if (tag.contains("killMessages")) {
            NbtList killMessagesTag = tag.getList("killMessages", 8);
            for (NbtElement killMessageTag : killMessagesTag) killMessages.add(killMessageTag.asString());
        } else {
            killMessages.add("haha %killed_player% is a noob! EZZz");
            killMessages.add("I just raped %killed_player%!");
            killMessages.add("I just ended %killed_player%!");
            killMessages.add("I just EZZz'd %killed_player%!");
            killMessages.add("I just fucked %killed_player%!");
            killMessages.add("Take the L nerd %killed_player%! You just got ended!");
            killMessages.add("I just nae nae'd %killed_player%!");
            killMessages.add("I am too good for %killed_player%!");
        }

        if (tag.contains("popMessages")) {
            NbtList popMessagesTag = tag.getList("popMessages", 8);
            for (NbtElement popMessageTag : popMessagesTag) popMessages.add(popMessageTag.asString());
        } else {
            popMessages.add("%popped_player% just lost 1 totem thanks to my skill!");
            popMessages.add("I just ended %popped_player%'s totem!");
            popMessages.add("I just popped %popped_player%!");
            popMessages.add("I just easily popped %popped_player%!");
        }

        return super.fromTag(tag);
    }

    // Kills

    @EventHandler
    public void onPacketReadMessage(PacketEvent.Receive event) {
        if (!kills.get()) return;
        if (killMessages.isEmpty() && mode.get() == Mode.Custom) return;
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
                                        if (ignoreFriends.get() && Friends.get().isFriend(player)) return;
                                        if (EntityUtils.getGameMode(player).isCreative()) return;
                                        String message = getMessageStyle();
                                        mc.player.sendChatMessage(Placeholders.apply(message).replace("%killed_player%", player.getName().getString()));
                                    }
                                } else {
                                    if (mc.player.distanceTo(player) < 5) {
                                        if (ignoreFriends.get() && Friends.get().isFriend(player)) return;
                                        if (EntityUtils.getGameMode(player).isCreative()) return;
                                        String message = getMessageStyle();
                                        mc.player.sendChatMessage(Placeholders.apply(message).replace("%killed_player%", player.getName().getString()));
                                    }
                                }
                            } else {
                                if (mc.player.distanceTo(player) < 7) {
                                    if (ignoreFriends.get() && Friends.get().isFriend(player)) return;
                                    if (EntityUtils.getGameMode(player).isCreative()) return;
                                    String message = getMessageStyle();
                                    mc.player.sendChatMessage(Placeholders.apply(message).replace("%killed_player%", player.getName().getString()));
                                }
                            }
                        } else {
                            if (mc.player.distanceTo(player) < 8) {
                                if (ignoreFriends.get() && Friends.get().isFriend(player)) return;
                                if (EntityUtils.getGameMode(player).isCreative()) return;
                                String message = getMessageStyle();
                                mc.player.sendChatMessage(Placeholders.apply(message).replace("%killed_player%", player.getName().getString()));
                            }
                        }
                    } else {
                        if ((msg.contains("bed") || msg.contains("[Intentional Game Design]")) && (Modules.get().isActive(BedAura.class))) {
                            if ((mc.player.distanceTo(player) < Modules.get().get(BedAura.class).targetRange.get())) {
                                if (ignoreFriends.get() && Friends.get().isFriend(player)) return;
                                if (EntityUtils.getGameMode(player).isCreative()) return;
                                String message = getMessageStyle();
                                mc.player.sendChatMessage(Placeholders.apply(message).replace("%killed_player%", player.getName().getString()));
                            }
                        } else if ((msg.contains("anchor") || msg.contains("[Intentional Game Design]")) && Modules.get().isActive(AnchorAura.class)) {
                            if (mc.player.distanceTo(player) < Modules.get().get(AnchorAura.class).targetRange.get()) {
                                if (ignoreFriends.get() && Friends.get().isFriend(player)) return;
                                if (EntityUtils.getGameMode(player).isCreative()) return;
                                String message = getMessageStyle();
                                mc.player.sendChatMessage(Placeholders.apply(message).replace("%killed_player%", player.getName().getString()));
                            }
                        }
                    }
                }
            }
        }
    }

    public String getMessageStyle() {
        return switch (mode.get()) {
            case MatHax -> switch (messageStyle.get()) {
                case EZ -> getMessage().get(random.nextInt(getMessage().size()));
                case GG -> getGGMessage().get(random.nextInt(getGGMessage().size()));
            };
            case Custom -> killMessages.get(random.nextInt(killMessages.size()));
        };
    }

    private static List<String> getMessage() {
        return Arrays.asList(
            "%killed_player% just got raped by MatHax Legacy!",
            "%killed_player% just got ended by MatHax Legacy!",
            "haha %killed_player% is a noob! MatHax Legacy on top!",
            "I just EZZz'd %killed_player% using MatHax Legacy!",
            "I just fucked %killed_player% using MatHax Legacy!",
            "Take the L nerd %killed_player%! You just got ended by MatHax Legacy!",
            "I just nae nae'd %killed_player% using MatHax Legacy!",
            "I am too good for %killed_player%! MatHax Legacy on top!"
        );
    }

    private static List<String> getGGMessage() {
        return Arrays.asList(
            "GG %killed_player%! MatHax Legacy is so op!",
            "Nice fight %killed_player%! I really enjoyed it!",
            "Close fight %killed_player%, but i won!",
            "Good fight, %killed_player%! MatHax Legacy on top!"
        );
    }

    // Totem Pops

    @EventHandler
    private void onReceivePacket(PacketEvent.Receive event) {
        if (!totems.get()) return;
        if (popMessages.isEmpty() && totemMode.get() == Mode.Custom) return;
        if (!(event.packet instanceof EntityStatusS2CPacket)) return;

        EntityStatusS2CPacket p = (EntityStatusS2CPacket) event.packet;
        if (p.getStatus() != 35) return;

        Entity entity = p.getEntity(mc.world);

        if (!(entity instanceof PlayerEntity)) return;

        if (entity == mc.player) return;
        if (mc.player.distanceTo(entity) > 8) return;
        if (Friends.get().isFriend(((PlayerEntity) entity)) && totemIgnoreFriends.get()) return;

        if (EntityUtils.getGameMode(mc.player).isCreative()) return;
        if (canSendPop) {
            String message = getPopMessageStyle();
            mc.player.sendChatMessage(Placeholders.apply(message).replace("%popped_player%", entity.getName().getString()));
            canSendPop = false;
        }
    }

    @Override
    public void onActivate() {
        canSendPop = true;
        ticks = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (ticks > 600) {
            canSendPop = true;
            ticks = 0;
        }

        if (!canSendPop) {
            ticks++;
        }
    }

    public String getPopMessageStyle() {
        return switch (totemMode.get()) {
            case MatHax -> getPopMessage().get(random.nextInt(getPopMessage().size()));
            case Custom -> popMessages.get(random.nextInt(popMessages.size()));
        };
    }

    private static List<String> getPopMessage() {
        return Arrays.asList(
            "%popped_player% just got popped by MatHax Legacy!",
            "Keep popping %popped_player%! MatHax Legacy owns you!",
            "%popped_player%'s totem just got ended by MatHax Legacy!",
            "%popped_player% just lost 1 totem thanks to MatHax Legacy!",
            "I just easily popped %popped_player% using MatHax Legacy!"
        );
    }

    public enum Mode {
        MatHax,
        Custom
    }

    public enum MessageStyle {
        EZ,
        GG
    }
}
