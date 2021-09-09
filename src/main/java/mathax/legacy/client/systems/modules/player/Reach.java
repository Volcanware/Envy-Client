package mathax.legacy.client.systems.modules.player;

import mathax.legacy.client.settings.DoubleSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import net.minecraft.item.Items;

public class Reach extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> reach = sgGeneral.add(new DoubleSetting.Builder()
        .name("reach")
        .description("Your reach modifier.")
        .defaultValue(5)
        .min(0)
        .sliderMax(6)
        .build()
    );

    public Reach() {
        super(Categories.Player, Items.COMMAND_BLOCK, "reach", "Gives you super long arms.");
    }

    public float getReach() {
        if (!isActive()) return mc.interactionManager.getCurrentGameMode().isCreative() ? 5.0F : 4.5F;
        return reach.get().floatValue();
    }
}
