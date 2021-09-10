package mathax.legacy.client.systems.modules.chat;

import mathax.legacy.client.Version;
import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.gui.GuiTheme;
import mathax.legacy.client.gui.widgets.WWidget;
import mathax.legacy.client.gui.widgets.containers.WTable;
import mathax.legacy.client.gui.widgets.input.WTextBox;
import mathax.legacy.client.gui.widgets.pressable.WMinus;
import mathax.legacy.client.gui.widgets.pressable.WPlus;
import mathax.legacy.client.settings.*;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.utils.Utils;
import mathax.legacy.client.bus.EventHandler;
import mathax.legacy.client.utils.placeholders.Placeholders;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

import java.util.ArrayList;
import java.util.List;

public class Spam extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("The delay between specified messages in ticks.")
        .defaultValue(20)
        .min(0)
        .sliderMax(10000)
        .build()
    );

    private final Setting<Boolean> random = sgGeneral.add(new BoolSetting.Builder()
        .name("randomise")
        .description("Selects a random message from your spam message list.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> randomNumbers = sgGeneral.add(new BoolSetting.Builder()
        .name("random-numbers")
        .description("Appends random numbers to the messages to bypass antispam plugins.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Integer> randomNumbersMin = sgGeneral.add(new IntSetting.Builder()
        .name("min-number")
        .description("Minimal number in random number append.")
        .defaultValue(1)
        .min(0)
        .sliderMax(10000)
        .visible(randomNumbers::get)
        .build()
    );

    private final Setting<Integer> randomNumbersMax = sgGeneral.add(new IntSetting.Builder()
        .name("max-number")
        .description("Maximal number in random number append.")
        .defaultValue(100000)
        .min(0)
        .sliderMax(1000000)
        .visible(randomNumbers::get)
        .build()
    );

    private final Setting<Boolean> placeholder = sgGeneral.add(new BoolSetting.Builder()
        .name("placeholders")
        .description("Replaces placeholders. Example: %version% -> " + Version.getStylized())
        .defaultValue(true)
        .build()
    );

    private final List<String> messages = new ArrayList<>();
    private int messageI, timer;
    private String newText = "";

    public Spam() {
        super(Categories.Chat, Items.BELL, "spam", "Spams specified messages in chat.");
    }

    @Override
    public void onActivate() {
        timer = delay.get();
        messageI = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (messages.isEmpty()) return;

        if (timer <= 0) {
            int i;
            if (random.get()) {
                i = Utils.random(0, messages.size());
            } else {
                if (messageI >= messages.size()) messageI = 0;
                i = messageI++;
            }
            if (placeholder.get()) {
                if (randomNumbers.get()) {
                    mc.player.sendChatMessage(Placeholders.apply(messages.get(i)) + String.format(" %03d", Utils.random(randomNumbersMin.get(), randomNumbersMax.get())));
                } else {
                    mc.player.sendChatMessage(Placeholders.apply(messages.get(i)));
                }
            } else {
                if (randomNumbers.get()) {
                    mc.player.sendChatMessage(messages.get(i) + String.format(" %03d", Utils.random(randomNumbersMin.get(), randomNumbersMax.get())));
                } else {
                    mc.player.sendChatMessage(messages.get(i));
                }
            }
            timer = delay.get();
        } else {
            timer--;
        }
    }

    @Override
    public WWidget getWidget(GuiTheme theme) {
        WTable table = theme.table();
        fillTable(theme, table);

        return table;
    }

    private void fillTable(GuiTheme theme, WTable table) {
        table.add(theme.horizontalSeparator("Messages")).expandX();
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
            newText = "";

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
            messages.add("MatHax on top!");
        }

        return super.fromTag(tag);
    }
}
