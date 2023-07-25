package envy.client.systems.modules.render;

import envy.client.Envy;
import envy.client.settings.ColorSetting;
import envy.client.settings.Setting;
import envy.client.settings.SettingGroup;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import envy.client.utils.render.color.SettingColor;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;

public class Confetti extends Module {
    private final SettingGroup sgGeneral = settings.createGroup("Colors");

    // Colors

    private final Setting<SettingColor> colorOne = sgGeneral.add(new ColorSetting.Builder()
        .name("first")
        .description("The first confetti color to change.")
        .defaultValue(new SettingColor(Envy.INSTANCE.MATHAX_COLOR.r, Envy.INSTANCE.MATHAX_COLOR.g, Envy.INSTANCE.MATHAX_COLOR.b))
        .build()
    );

    private final Setting<SettingColor> colorTwo = sgGeneral.add(new ColorSetting.Builder()
        .name("second")
        .description("The second confetti color to change.")
        .defaultValue(new SettingColor(Envy.INSTANCE.MATHAX_BACKGROUND_COLOR.r, Envy.INSTANCE.MATHAX_BACKGROUND_COLOR.g, Envy.INSTANCE.MATHAX_BACKGROUND_COLOR.b))
        .build()
    );

    public Confetti() {
        super(Categories.Render, Items.TOTEM_OF_UNDYING, "Confetti", "Changes the color of the totem pop particles.");
    }
    //does this work
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
