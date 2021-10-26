package mathax.legacy.client.systems.modules.render;

import mathax.legacy.client.settings.*;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import net.minecraft.item.Items;

// TODO: Implement when 1.17.1 & Sodium support added. https://www.curseforge.com/minecraft/mc-mods/smooth-chunks

public class SmoothChunks extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    public final Setting<Boolean> disableNearby = sgGeneral.add(new BoolSetting.Builder()
        .name("disable-nearby")
        .description("Disables animating chunks close to you.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Integer> duration = sgGeneral.add(new IntSetting.Builder()
        .name("duration")
        .description("Duration of the animation in ticks.")
        .defaultValue(20)
        .min(1)
        .sliderRange(1, 100)
        .build()
    );

    private final Setting<Double> translationAmount = sgGeneral.add(new DoubleSetting.Builder()
        .name("translation-amount")
        .description("The amount the chunk moves to get to its final position.")
        .defaultValue(5)
        .range(1, 10)
        .build()
    );

    public final Setting<Animation> animation = sgGeneral.add(new EnumSetting.Builder<Animation>()
        .name("animation")
        .description("Determines the chunk animation.")
        .defaultValue(Animation.Up)
        .build()
    );

    public SmoothChunks() {
        super(Categories.Render, Items.GRASS_BLOCK, "smooth-chunks", "Animates chunk loading.");
    }

    public enum Animation {
        Down,
        Up,
        In,
        Scale
    }
}
