package mathax.client.legacy.systems.modules.world;

import mathax.client.legacy.gui.GuiTheme;
import mathax.client.legacy.gui.widgets.WWidget;
import mathax.client.legacy.gui.widgets.containers.WHorizontalList;
import mathax.client.legacy.gui.widgets.pressable.WButton;
import mathax.client.legacy.settings.DoubleSetting;
import mathax.client.legacy.settings.Setting;
import mathax.client.legacy.settings.SettingGroup;
import mathax.client.legacy.systems.modules.Categories;
import mathax.client.legacy.systems.modules.Module;
import mathax.client.legacy.systems.modules.Modules;
import mathax.client.legacy.systems.modules.misc.TPSSync;
import net.minecraft.item.Items;

public class Timer extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> multiplier = sgGeneral.add(new DoubleSetting.Builder()
        .name("multiplier")
        .description("The timer multiplier amount.")
        .defaultValue(1)
        .min(0.1)
        .sliderMin(1)
        .build()
    );

    public static final double OFF = 1;
    private double override = 1;

    public Timer() {
        super(Categories.World, Items.CLOCK, "timer", "Changes the speed of everything in your game.");
    }

    public double getMultiplier() {
        return override != OFF ? override : (isActive() ? multiplier.get() : OFF);
    }

    public void setOverride(double override) {
        this.override = override;
    }

    @Override
    public WWidget getWidget(GuiTheme theme) {
        if (Modules.get().get(TPSSync.class).isActive()) {
            WHorizontalList list = theme.horizontalList();
            list.add(theme.label("Multiplier is overwritten by TPSSync."));
            WButton disableBtn = list.add(theme.button("Disable TPSSync")).widget();
            disableBtn.action = () -> {
                TPSSync tpsSync = Modules.get().get(TPSSync.class);
                if (tpsSync.isActive()) tpsSync.toggle();
                list.visible = false;
            };
            return list;
        }
        return null;
    }
}
