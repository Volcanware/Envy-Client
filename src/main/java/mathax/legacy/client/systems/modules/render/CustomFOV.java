package mathax.legacy.client.systems.modules.render;

import mathax.legacy.client.events.render.Render3DEvent;
import mathax.legacy.client.settings.IntSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.bus.EventHandler;
import net.minecraft.item.Items;

public class CustomFOV extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> fovSetting = sgGeneral.add(new IntSetting.Builder()
        .name("fov")
        .description("Your custom fov.")
        .defaultValue(130)
        .sliderMin(1)
        .sliderMax(175)
        .build()
    );

    private double fov;

    public CustomFOV() {
        super(Categories.Render, Items.GLOW_INK_SAC, "custom-fov");
    }

    @Override
    public void onActivate() {
        fov = mc.options.fov;
        mc.options.fov = fovSetting.get();
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (fovSetting.get() != mc.options.fov) {
            mc.options.fov = fovSetting.get();
        }
    }

    @Override
    public void onDeactivate() {
        mc.options.fov = fov;
    }
}
