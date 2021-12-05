package mathax.legacy.client.systems.modules.movement;

import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.settings.EnumSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.systems.modules.Modules;
import mathax.legacy.client.utils.Utils;
import mathax.legacy.client.eventbus.EventHandler;
import net.minecraft.item.Items;

public class AntiVoid extends Module {
    private final Flight flight = Modules.get().get(Flight.class);

    private boolean wasFlightEnabled, hasRun;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("The method to prevent you from falling into the void.")
        .defaultValue(Mode.Jump)
        .onChanged(a -> onActivate())
        .build()
    );

    public AntiVoid() {
        super(Categories.Movement, Items.BARRIER, "anti-void", "Attempts to prevent you from falling into the void.");
    }

    @Override
    public void onActivate() {
        if (mode.get() == Mode.Flight) wasFlightEnabled = flight.isActive();
    }

    @Override
    public void onDeactivate() {
        if (!wasFlightEnabled && mode.get() == Mode.Flight && Utils.canUpdate() && flight.isActive()) flight.toggle();
    }

    @EventHandler
    private void onPreTick(TickEvent.Pre event) {
        if (mc.player.getY() > Utils.getMinHeight() || mc.player.getY() < -15) {
            if (hasRun && mode.get() == Mode.Flight && flight.isActive()) {
                flight.toggle();
                hasRun = false;
            }

            return;
        }

        switch (mode.get()) {
            case Flight -> {
                if (!flight.isActive()) flight.toggle();
                hasRun = true;
            }
            case Jump -> mc.player.jump();
        }
    }

    public enum Mode {
        Flight,
        Jump
    }
}
