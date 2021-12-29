package mathax.legacy.client.systems.modules.movement;

import mathax.legacy.client.settings.BoolSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import net.minecraft.item.Items;

public class AntiLevitation extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Boolean> applyGravity = sgGeneral.add(new BoolSetting.Builder()
        .name("gravity")
        .description("Applies gravity.")
        .defaultValue(false)
        .build()
    );

    public AntiLevitation() {
        super(Categories.Movement, Items.ANVIL, "anti-levitation", "Prevents the levitation effect from working.");
    }

    public boolean isApplyGravity() {
        return applyGravity.get();
    }
}
