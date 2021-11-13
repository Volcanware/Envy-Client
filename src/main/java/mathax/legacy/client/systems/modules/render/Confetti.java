package mathax.legacy.client.systems.modules.render;

import mathax.legacy.client.MatHaxLegacy;
import mathax.legacy.client.settings.ColorSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.utils.render.color.SettingColor;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;

public class Confetti extends Module {
    private final SettingGroup sgGeneral = settings.createGroup("Colors");

    // Colors

    private final Setting<SettingColor> colorOne = sgGeneral.add(new ColorSetting.Builder()
        .name("first")
        .description("The first confetti color to change.")
        .defaultValue(new SettingColor(MatHaxLegacy.INSTANCE.MATHAX_COLOR.r, MatHaxLegacy.INSTANCE.MATHAX_COLOR.g, MatHaxLegacy.INSTANCE.MATHAX_COLOR.b))
        .build()
    );

    private final Setting<SettingColor> colorTwo = sgGeneral.add(new ColorSetting.Builder()
        .name("second")
        .description("The second confetti color to change.")
        .defaultValue(new SettingColor(MatHaxLegacy.INSTANCE.MATHAX_BACKGROUND_COLOR.r, MatHaxLegacy.INSTANCE.MATHAX_BACKGROUND_COLOR.g, MatHaxLegacy.INSTANCE.MATHAX_BACKGROUND_COLOR.b))
        .build()
    );

    public Confetti() {
        super(Categories.Render, Items.TOTEM_OF_UNDYING, "Confetti", "Changes the color of the totem pop particles.");
    }

    public Vec3d getColorOne() {
        return getDoubleVectorColor(colorOne);
    }

    public Vec3d getColorTwo() {
        return getDoubleVectorColor(colorTwo);
    }

    public Vec3d getDoubleVectorColor(Setting<SettingColor> colorSetting) {
        return new Vec3d(colorSetting.get().r / 255.0, colorSetting.get().g / 255.0, colorSetting.get().b / 255.0);
    }
}
