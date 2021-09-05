package mathax.client.legacy.systems.modules.movement;

import mathax.client.legacy.settings.BlockListSetting;
import mathax.client.legacy.settings.DoubleSetting;
import mathax.client.legacy.settings.Setting;
import mathax.client.legacy.settings.SettingGroup;
import mathax.client.legacy.systems.modules.Categories;
import mathax.client.legacy.systems.modules.Module;
import net.minecraft.block.Block;
import net.minecraft.item.Items;

import java.util.Collections;
import java.util.List;

public class Slippy extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Double> slippness = sgGeneral.add(new DoubleSetting.Builder()
        .name("slippness")
        .description("Decide how slippery blocks should be")
        .min(0.0)
        .max(1.10)
        .sliderMax(1.10)
        .defaultValue(1.02)
        .build()
    );

    public final Setting<List<Block>> blocks = sgGeneral.add(new BlockListSetting.Builder()
        .name("ignored blocks")
        .description("Decide which blocks not to slip on")
        .defaultValue(Collections.emptyList())
        .build()
    );

    public Slippy() {
        super(Categories.Movement, Items.BLUE_ICE, "slippy", "Makes blocks slippery like ice.");
    }
}
