package mathax.client.legacy.systems.modules.fun;

import mathax.client.legacy.bus.EventHandler;
import mathax.client.legacy.bus.EventPriority;
import mathax.client.legacy.events.world.TickEvent;
import mathax.client.legacy.gui.GuiTheme;
import mathax.client.legacy.gui.widgets.WWidget;
import mathax.client.legacy.gui.widgets.containers.WHorizontalList;
import mathax.client.legacy.gui.widgets.pressable.WButton;
import mathax.client.legacy.settings.BoolSetting;
import mathax.client.legacy.settings.Setting;
import mathax.client.legacy.settings.SettingGroup;
import mathax.client.legacy.systems.modules.Categories;
import mathax.client.legacy.systems.modules.Module;
import mathax.client.legacy.systems.modules.movement.AutoWalk;
import mathax.client.legacy.utils.entity.fakeplayer.FakePlayerManager;

public class Capes extends Module {
    private int timer = 0;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Boolean> autoReload = sgGeneral.add(new BoolSetting.Builder()
        .name("auto-reload")
        .description("Automatically reloads capes every 10 minutes.")
        .defaultValue(true)
        .build()
    );

    /*private final Setting<Boolean> ignoreOwn = sgGeneral.add(new BoolSetting.Builder()
        .name("ignore-own")
        .description("Ignores your own cape.")
        .defaultValue(true)
        .onChanged(val -> mathax.client.legacy.utils.network.Capes.reload())
        .build()
    );*/

    public Capes() {
        super(Categories.Fun, "capes", "When enabled you will see MatHax capes on users which have them.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (timer > 12000) {
            timer = 0;
            mathax.client.legacy.utils.network.Capes.reload();
        }

        timer++;
    }

    @Override
    public void onActivate() {
        mathax.client.legacy.utils.network.Capes.init();
    }

    @Override
    public void onDeactivate() {
        mathax.client.legacy.utils.network.Capes.disable();
    }

    @Override
    public WWidget getWidget(GuiTheme theme) {
        WHorizontalList w = theme.horizontalList();

        WButton reload = w.add(theme.button("Reload")).widget();
        reload.action = () -> {
            if (isActive()) mathax.client.legacy.utils.network.Capes.reload();
        };
        w.add(theme.label("Reloads all MatHax capes from MatHax API."));

        return w;
    }
}
