package envy.client.systems.modules.render;

import envy.client.Envy;
import envy.client.gui.GuiTheme;
import envy.client.gui.screens.interactionmenu.InteractionScreen;
import envy.client.gui.widgets.WWidget;
import envy.client.gui.widgets.containers.WTable;
import envy.client.gui.widgets.input.WTextBox;
import envy.client.gui.widgets.pressable.WMinus;
import envy.client.gui.widgets.pressable.WPlus;
import envy.client.settings.*;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import envy.client.utils.Utils;
import envy.client.utils.misc.KeyBind;
import envy.client.utils.render.color.SettingColor;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;

import java.util.HashMap;
import java.util.Optional;

/*/----------------------------------------------------------------------------------------------------------------------/*/
/*/ Used from Meteor Rejects                                                                                             /*/
/*/ https://github.com/AntiCope/meteor-rejects/blob/master/src/main/java/anticope/rejects/modules/InteractionMenu.java /*/
/*/----------------------------------------------------------------------------------------------------------------------/*/

public class InteractionMenu extends Module {
    public final HashMap<String,String> messages = new HashMap<>();
    private String currMsgK = "", currMsgV = "";

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgStyle = settings.createGroup("Style");

    // General

    private final Setting<Object2BooleanMap<EntityType<?>>> entities = sgGeneral.add(new EntityTypeListSetting.Builder()
        .name("entities")
        .description("Entities")
        .defaultValue(Utils.asO2BMap(EntityType.PLAYER))
        .build()
    );

    public final Setting<KeyBind> keybind = sgGeneral.add(new KeyBindSetting.Builder()
        .name("keybind")
        .description("The keybind to open.")
        .action(this::onKey)
        .build()
    );

    // Style

    public final Setting<SettingColor> selectedDotColor = sgStyle.add(new ColorSetting.Builder()
        .name("selected-dot-color")
        .description("Color of the dot when selected.")
        .defaultValue(new SettingColor(255, 25, 25))
        .build()
    );

    public final Setting<SettingColor> dotColor = sgStyle.add(new ColorSetting.Builder()
        .name("dot-color")
        .description("Color of the dot when.")
        .defaultValue(new SettingColor(Envy.INSTANCE.MATHAX_COLOR.r, Envy.INSTANCE.MATHAX_COLOR.g, Envy.INSTANCE.MATHAX_COLOR.b))
        .build()
    );

    public final Setting<SettingColor> backgroundColor = sgStyle.add(new ColorSetting.Builder()
        .name("background-color")
        .description("Color of the background.")
        .defaultValue(new SettingColor(0, 0, 0, 75))
        .build()
    );

    public final Setting<SettingColor> borderColor = sgStyle.add(new ColorSetting.Builder()
        .name("border-color")
        .description("Color of the border.")
        .defaultValue(new SettingColor(0, 0, 0))
        .build()
    );

    public final Setting<SettingColor> textColor = sgStyle.add(new ColorSetting.Builder()
        .name("text-color")
        .description("Color of the text.")
        .defaultValue(new SettingColor(255, 255, 255))
        .build()
    );

    // Buttons

    @Override
    public WWidget getWidget(GuiTheme theme) {
        WTable table = theme.table();
        fillTable(theme, table);
        return table; //imagine interacting
    }

    private void fillTable(GuiTheme theme, WTable table) {
        table.clear();

        messages.keySet().forEach((key) -> {
            table.add(theme.label(key)).expandCellX();
            table.add(theme.label(messages.get(key))).expandCellX();
            WMinus delete = table.add(theme.minus()).widget();
            delete.action = () -> {
                messages.remove(key);
                fillTable(theme,table);
            };
            table.row();
        });

        WTextBox textBoxK = table.add(theme.textBox(currMsgK)).minWidth(100).expandX().widget();
        textBoxK.action = () -> currMsgK = textBoxK.get();

        WTextBox textBoxV = table.add(theme.textBox(currMsgV)).minWidth(100).expandX().widget();
        textBoxV.action = () -> currMsgV = textBoxV.get();

        WPlus add = table.add(theme.plus()).widget();
        add.action = () -> {
            if (currMsgK.equals("")  && currMsgV.equals("")) {
                messages.put(currMsgK, currMsgV);
                currMsgK = ""; currMsgV = "";
                fillTable(theme,table);
            }
        };

        table.row();
    }

    public InteractionMenu() {
        super(Categories.Render, Items.ARMOR_STAND,"interaction-menu","An interaction screen when looking at an entity.");
    }

    public void onKey() {
        if (mc.currentScreen != null) return;
        Optional<Entity> lookingAt = DebugRenderer.getTargetedEntity(mc.player, 20);
        if (lookingAt.isPresent()) {
            Entity e = lookingAt.get();
            if (entities.get().getBoolean(e.getType())) {
                mc.setScreen(new InteractionScreen(e, this));
            }
        }
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = super.toTag();

        NbtCompound messTag = new NbtCompound();
        messages.keySet().forEach((key) -> messTag.put(key, NbtString.of(messages.get(key))));

        tag.put("messages", messTag);
        return tag;
    }

    @Override
    public Module fromTag(NbtCompound tag) {

        messages.clear();
        if (tag.contains("messages")) {
            NbtCompound msgs = tag.getCompound("messages");
            msgs.getKeys().forEach((key) -> messages.put(key, msgs.getString(key)));
        }

        return super.fromTag(tag);
    }
}
