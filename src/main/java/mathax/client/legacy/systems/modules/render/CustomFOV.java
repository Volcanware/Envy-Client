package mathax.client.legacy.systems.modules.render;

import mathax.client.legacy.events.render.Render3DEvent;
import mathax.client.legacy.settings.IntSetting;
import mathax.client.legacy.settings.Setting;
import mathax.client.legacy.settings.SettingGroup;
import mathax.client.legacy.systems.modules.Categories;
import mathax.client.legacy.systems.modules.Module;
import mathax.client.legacy.bus.EventHandler;
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
        super(Categories.Render, Items.GLOW_INK_SAC, "custom-fov", "Allows your FOV to be more customizable.");
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
