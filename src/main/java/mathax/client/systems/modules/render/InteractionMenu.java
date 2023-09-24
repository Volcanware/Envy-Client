package mathax.client.systems.modules.render;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import mathax.client.MatHax;
import mathax.client.gui.GuiTheme;
import mathax.client.gui.screens.interactionmenu.InteractionScreen;
import mathax.client.gui.utils.StarscriptTextBoxRenderer;
import mathax.client.gui.widgets.WWidget;
import mathax.client.gui.widgets.containers.WTable;
import mathax.client.gui.widgets.input.WTextBox;
import mathax.client.gui.widgets.pressable.WMinus;
import mathax.client.gui.widgets.pressable.WPlus;
import mathax.client.settings.*;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import mathax.client.utils.Utils;
import mathax.client.utils.misc.KeyBind;
import mathax.client.utils.misc.MathaxStarscript;
import mathax.client.utils.render.color.SettingColor;
import meteordevelopment.starscript.value.Value;
import meteordevelopment.starscript.value.ValueMap;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/*/----------------------------------------------------------------------------------------------------------------------/*/
/*/ Used from Meteor Rejects                                                                                             /*/
/*/ https://github.com/AntiCope/meteor-rejects/blob/master/src/main/java/anticope/rejects/modules/InteractionMenu.java /*/
/*/----------------------------------------------------------------------------------------------------------------------/*/

public class InteractionMenu extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgStyle = settings.createGroup("Style");

    private final Setting<Object2BooleanMap<EntityType<?>>> entities = sgGeneral.add(new EntityTypeListSetting.Builder()
        .name("entities")
        .description("Entities")
        .defaultValue(EntityType.PLAYER)
        .build()
    );
    public final Setting<KeyBind> keybind = sgGeneral.add(new KeyBindSetting.Builder()
        .name("keybind")
        .description("The keybind to open.")
        .action(this::onKey)
        .build()
    );
    public final Setting<Boolean> useCrosshairTarget = sgGeneral.add(new BoolSetting.Builder()
        .name("use-crosshair-target")
        .description("Use crosshair target.")
        .defaultValue(false)
        .build()
    );

    // Style
    public final Setting<SettingColor> selectedDotColor = sgStyle.add(new ColorSetting.Builder()
        .name("selected-dot-color")
        .description("Color of the dot when selected.")
        .defaultValue(new SettingColor(76, 255, 0))
        .build()
    );
    public final Setting<SettingColor> dotColor = sgStyle.add(new ColorSetting.Builder()
        .name("dot-color")
        .description("Color of the dot when.")
        .defaultValue(new SettingColor(0, 148, 255))
        .build()
    );
    public final Setting<SettingColor> backgroundColor = sgStyle.add(new ColorSetting.Builder()
        .name("background-color")
        .description("Color of the background.")
        .defaultValue(new SettingColor(128, 128, 128, 128))
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

    public final Setting<Map<String, String>> messages = sgGeneral.add(new StringMapSetting.Builder()
        .name("messages")
        .description("Messages.")
        .renderer(StarscriptTextBoxRenderer.class)
        .build()
    );

    public InteractionMenu() {
        super(Categories.Render, Items.ARMOR_STAND, "interaction-menu", "An interaction screen when looking at an entity.");
        MathaxStarscript.ss.set("entity", () -> wrap(InteractionScreen.interactionMenuEntity));
    }

    public void onKey() {
        if (mc.player == null || mc.currentScreen != null) return;
        Entity e = null;
        if (useCrosshairTarget.get()) {
            e = mc.targetedEntity;
        } else {
            Optional<Entity> lookingAt = DebugRenderer.getTargetedEntity(mc.player, 20);
            if (lookingAt.isPresent()) {
                e = lookingAt.get();
            }
        }

        if (e == null) return;
        if (entities.get().getOrDefault(e.getType(), false)) {
            mc.setScreen(new InteractionScreen(e, this));
        }
    }

    private static Value wrap(Entity entity) {
        if (entity == null) {
            return Value.map(new ValueMap()
                .set("_toString", Value.null_())
                .set("health", Value.null_())
                .set("pos", Value.map(new ValueMap()
                    .set("_toString", Value.null_())
                    .set("x", Value.null_())
                    .set("y", Value.null_())
                    .set("z", Value.null_())
                ))
                .set("uuid", Value.null_())
            );
        }
        return Value.map(new ValueMap()
            .set("_toString", Value.string(entity.getName().getString()))
            .set("health", Value.number(entity instanceof LivingEntity e ? e.getHealth() : 0))
            .set("pos", Value.map(new ValueMap()
                .set("_toString", posString(entity.getX(), entity.getY(), entity.getZ()))
                .set("x", Value.number(entity.getX()))
                .set("y", Value.number(entity.getY()))
                .set("z", Value.number(entity.getZ()))
            ))
            .set("uuid", Value.string(entity.getUuidAsString()))
        );
    }

    private static Value posString(double x, double y, double z) {
        return Value.string(String.format("X: %.0f Y: %.0f Z: %.0f", x, y, z));
    }
}
