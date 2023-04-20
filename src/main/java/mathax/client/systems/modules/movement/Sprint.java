package mathax.client.systems.modules.movement;

import mathax.client.eventbus.EventHandler;
import mathax.client.events.world.TickEvent;
import mathax.client.gui.GuiTheme;
import mathax.client.gui.widgets.WWidget;
import mathax.client.gui.widgets.containers.WHorizontalList;
import mathax.client.gui.widgets.pressable.WButton;
import mathax.client.settings.BoolSetting;
import mathax.client.settings.DoubleSetting;
import mathax.client.settings.Setting;
import mathax.client.settings.SettingGroup;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import mathax.client.systems.modules.Modules;
import mathax.client.systems.modules.misc.TPSSync;
import mathax.client.utils.player.PlayerUtils;
import net.minecraft.item.Items;

public class Sprint extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public static final double OFF = 1;
    private double override = 1;


    // General

    private final Setting<Boolean> whenStationary = sgGeneral.add(new BoolSetting.Builder()
        .name("when-stationary")
        .description("Continues sprinting even if you do not move.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> MultiDirection = sgGeneral.add(new BoolSetting.Builder()
        .name("Multi-Direction")
        .description("Continues sprinting even if you change direction.")
        .defaultValue(false)
        .build()
    );

    /*private final Setting<Double> multiplier = sgGeneral.add(new DoubleSetting.Builder()
        .name("multiplier")
        .description("The timer multiplier amount.")
        .defaultValue(1)
        .min(0.1)
        .sliderRange(0.1, 3)
        .build()
    );*/

    public Sprint() {
        super(Categories.Ghost, Items.DIAMOND_BOOTS, "sprint", "Automatically sprints.");
    }

    @Override
    public void onDeactivate() {
        mc.player.setSprinting(false);
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player.forwardSpeed > 0 && !whenStationary.get()) mc.player.setSprinting(true);
        else if (whenStationary.get()) mc.player.setSprinting(true);
        if (PlayerUtils.isMoving() && MultiDirection.get()) {
            mc.player.setSprinting(true);
        }
    }

    /*@Override
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

    public void setOverride(double override) {
        this.override = override;
    }*/
}

