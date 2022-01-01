package mathax.client.systems.modules.client;

import mathax.client.eventbus.EventHandler;
import mathax.client.events.world.TickEvent;
import mathax.client.gui.GuiTheme;
import mathax.client.gui.widgets.WWidget;
import mathax.client.gui.widgets.containers.WHorizontalList;
import mathax.client.gui.widgets.pressable.WButton;
import mathax.client.settings.BoolSetting;
import mathax.client.settings.Setting;
import mathax.client.settings.SettingGroup;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import mathax.client.utils.network.Capes;
import net.minecraft.item.Items;

public class CapesModule extends Module {
    private int timer = 0;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Boolean> autoReload = sgGeneral.add(new BoolSetting.Builder()
        .name("auto-reload")
        .description("Automatically reloads the capes every 10 minutes.")
        .defaultValue(true)
        .build()
    );

    // Buttons

    @Override
    public WWidget getWidget(GuiTheme theme) {
        WHorizontalList w = theme.horizontalList();

        WButton reload = w.add(theme.button("Reload")).widget();
        reload.action = () -> {
            if (isActive()) Capes.init();
        };
        w.add(theme.label("Reloads the capes."));

        return w;
    }

    public CapesModule() {
        super(Categories.Client, Items.CAKE, "capes", "Shows very cool capes on users which have them.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (!autoReload.get()) {
            timer = 0;
            return;
        }

        if (timer > 12000) {
            timer = 0;
            Capes.init();
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
}
