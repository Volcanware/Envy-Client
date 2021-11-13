package mathax.legacy.client.systems.modules.movement;

import mathax.legacy.client.settings.BoolSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import net.minecraft.item.Items;

public class Moses extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    public final Setting<Boolean> lava = sgGeneral.add(new BoolSetting.Builder().name("lava").description("Applies to lava too.").defaultValue(false).build());

    public Moses() {
        super(Categories.Movement, Items.ACACIA_LEAVES, "moses", "Lets you walk through water as if it was air.");
    }
}
