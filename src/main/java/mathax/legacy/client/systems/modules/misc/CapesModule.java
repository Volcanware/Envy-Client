package mathax.legacy.client.systems.modules.misc;

import mathax.legacy.client.bus.EventHandler;
import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.gui.GuiTheme;
import mathax.legacy.client.gui.widgets.WWidget;
import mathax.legacy.client.gui.widgets.containers.WHorizontalList;
import mathax.legacy.client.gui.widgets.pressable.WButton;
import mathax.legacy.client.settings.BoolSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.utils.network.Capes;
import net.minecraft.item.Items;

public class CapesModule extends Module {
    private int timer = 0;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Boolean> autoReload = sgGeneral.add(new BoolSetting.Builder()
        .name("auto-reload")
        .description("Automatically reloads capes every 10 minutes.")
        .defaultValue(true)
        .build()
    );

    public CapesModule() {
        super(Categories.Misc, Items.CAKE, "capes", "When enabled you will see very cool MatHax cape on users which have them.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (timer > 12000) {
            timer = 0;
            Capes.reload();
        }

        timer++;
    }

    @Override
    public void onActivate() {
        Capes.init();
    }

    @Override
    public void onDeactivate() {
        Capes.disable();
    }

    @Override
    public WWidget getWidget(GuiTheme theme) {
        WHorizontalList w = theme.horizontalList();

        WButton reload = w.add(theme.button("Reload")).widget();
        reload.action = () -> {
            if (isActive()) Capes.reload();
        };
        w.add(theme.label("Reloads all MatHax capes from MatHax API."));

        return w;
    }
}
