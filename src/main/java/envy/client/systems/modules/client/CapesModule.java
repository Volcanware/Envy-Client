package envy.client.systems.modules.client;

import envy.client.eventbus.EventHandler;
import envy.client.events.world.TickEvent;
import envy.client.gui.GuiTheme;
import envy.client.gui.widgets.WWidget;
import envy.client.gui.widgets.containers.WHorizontalList;
import envy.client.gui.widgets.pressable.WButton;
import envy.client.settings.BoolSetting;
import envy.client.settings.Setting;
import envy.client.settings.SettingGroup;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import envy.client.utils.network.Capes;
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

    @Override
    public boolean onActivate() {
        timer = 0;
        Capes.init();
        return false;
    }

    @Override
    public void onDeactivate() {
        Capes.disable();
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (!autoReload.get()) return;

        if (timer >= 12000) {
            timer = 0;
            Capes.init();
        }

        timer++;
    }
}
