
package mathax.legacy.client.systems.modules.chat;

import mathax.legacy.client.bus.EventHandler;
import mathax.legacy.client.events.packets.PacketEvent;
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
    private final List<String> messages = new ArrayList<>();
    private String newText = "%killed_player%";
    private final Random random = new Random();

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    //General

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("Determines what messages to use.")
        .defaultValue(Mode.MatHax)
        .build()
    );

    private final Setting<MessageStyle> messageStyle = sgGeneral.add(new EnumSetting.Builder<MessageStyle>()
        .name("style")
        .description("Determines what message style to use.")
        .defaultValue(MessageStyle.EZ)
        .visible(() -> mode.get() == Mode.MatHax)
        .build()
    );

    private final Setting<Boolean> ignoreFriends = sgGeneral.add(new BoolSetting.Builder()
        .name("ignore-friends")
        .description("Ignores friends.")
        .defaultValue(true)
        .build()
    );

    public AutoEZ() {
        super(Categories.Chat, Items.LIGHTNING_ROD, "auto-EZ", "Announces EASY when you kill someone");
    }

    @Override
    public WWidget getWidget(GuiTheme theme) {
        WTable table = theme.table();
        fillTable(theme, table);

        return table;
    }

    private void fillTable(GuiTheme theme, WTable table) {
        table.add(theme.horizontalSeparator("Custom Messages")).expandX();
        table.row();

        // Messages
        messages.removeIf(String::isEmpty);

        for (int i = 0; i < messages.size(); i++) {
            int msgI = i;
            String message = messages.get(i);

            WTextBox textBox = table.add(theme.textBox(message)).expandX().widget();
            textBox.action = () -> messages.set(msgI, textBox.get());

            WMinus delete = table.add(theme.minus()).widget();
            delete.action = () -> {
                messages.remove(msgI);

                table.clear();
                fillTable(theme, table);
            };

            table.row();
        }

        if (!messages.isEmpty()) {
            table.add(theme.horizontalSeparator()).expandX();
            table.row();
        }

        // New Message
        WTextBox textBox = table.add(theme.textBox(newText)).minWidth(100).expandX().widget();
        textBox.action = () -> newText = textBox.get();

        WPlus add = table.add(theme.plus()).widget();
        add.action = () -> {
            messages.add(newText);
            newText = "%killed_player%";

            table.clear();
            fillTable(theme, table);
        };
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = super.toTag();

        messages.removeIf(String::isEmpty);
        NbtList messagesTag = new NbtList();

        for (String message : messages) messagesTag.add(NbtString.of(message));
        tag.put("messages", messagesTag);

        return tag;
    }

    @Override
    public Module fromTag(NbtCompound tag) {
        messages.clear();

        if (tag.contains("messages")) {
            NbtList messagesTag = tag.getList("messages", 8);
            for (NbtElement messageTag : messagesTag) messages.add(messageTag.asString());
        } else {
            messages.add("I just raped %killed_person%!");
            messages.add("I just ended %killed_person%!");
            messages.add("haha %killed_person% is a noob! EZZz");
            messages.add("I just EZZz'd %killed_person%!");
            messages.add("I just fucked %killed_person%!");
            messages.add("I just nae nae'd %killed_person%!");
            messages.add("Take the L nerd %killed_person%! You just got ended!");
            messages.add("I am too good for %killed_person%!");
        }

        return super.fromTag(tag);
    }

    @EventHandler
    public void onPacketReadMessage(PacketEvent.Receive event) {
        if (messages.isEmpty()) return;
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
                                        mc.player.sendChatMessage(Placeholders.apply(message).replace("%killed_person%", player.getName().getString()));
                                    }
                                } else {
                                    if (mc.player.distanceTo(player) < 5) {
                                        if (ignoreFriends.get() && Friends.get().isFriend(player)) return;
                                        if (EntityUtils.getGameMode(player).isCreative()) return;
                                        String message = getMessageStyle();
                                        mc.player.sendChatMessage(Placeholders.apply(message).replace("%killed_person%", player.getName().getString()));
                                    }
                                }
                            } else {
                                if (mc.player.distanceTo(player) < 7) {
                                    if (ignoreFriends.get() && Friends.get().isFriend(player)) return;
                                    if (EntityUtils.getGameMode(player).isCreative()) return;
                                    String message = getMessageStyle();
                                    mc.player.sendChatMessage(Placeholders.apply(message).replace("%killed_person%", player.getName().getString()));
                                }
                            }
                        } else {
                            if (mc.player.distanceTo(player) < 8) {
                                if (ignoreFriends.get() && Friends.get().isFriend(player)) return;
                                if (EntityUtils.getGameMode(player).isCreative()) return;
                                String message = getMessageStyle();
                                mc.player.sendChatMessage(Placeholders.apply(message).replace("%killed_person%", player.getName().getString()));
                            }
                        }
                    } else {
                        if ((msg.contains("bed") || msg.contains("[Intentional Game Design]")) && (Modules.get().isActive(BedAura.class))) {
                            if ((mc.player.distanceTo(player) < Modules.get().get(BedAura.class).targetRange.get())) {
                                if (ignoreFriends.get() && Friends.get().isFriend(player)) return;
                                if (EntityUtils.getGameMode(player).isCreative()) return;
                                String message = getMessageStyle();
                                mc.player.sendChatMessage(Placeholders.apply(message).replace("%killed_person%", player.getName().getString()));
                            }
                        } else if ((msg.contains("anchor") || msg.contains("[Intentional Game Design]")) && Modules.get().isActive(AnchorAura.class)) {
                            if (mc.player.distanceTo(player) < Modules.get().get(AnchorAura.class).targetRange.get()) {
                                if (ignoreFriends.get() && Friends.get().isFriend(player)) return;
                                if (EntityUtils.getGameMode(player).isCreative()) return;
                                String message = getMessageStyle();
                                mc.player.sendChatMessage(Placeholders.apply(message).replace("%killed_person%", player.getName().getString()));
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
            case Custom -> messages.get(random.nextInt(messages.size()));
        };
    }

    private static List<String> getMessage() {
        return Arrays.asList(
            "%killed_person% just got raped by MatHax Legacy!",
            "%killed_person% just got ended by MatHax Legacy!",
            "haha %killed_person% is a noob! MatHax Legacy on top!",
            "I just EZZz'd %killed_person% using MatHax Legacy!",
            "I just fucked %killed_person% using MatHax Legacy!",
            "I just nae nae'd %killed_person% using MatHax Legacy!",
            "Take the L nerd %killed_person%! You just got ended by MatHax Legacy!",
            "I am too good for %killed_person%! MatHax Legacy on top!"
        );
    }

    private static List<String> getGGMessage() {
        return Arrays.asList(
            "GG %killed_person%! MatHax Legacy is so op!",
            "Close fight %killed_person%, but i won!",
            "Good fight, %killed_person%! MatHax Legacy on top!",
            "Nice fight %killed_person%! I really enjoyed it!"
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
