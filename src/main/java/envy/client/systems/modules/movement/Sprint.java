package envy.client.systems.modules.movement;

import envy.client.eventbus.EventHandler;
import envy.client.events.world.TickEvent;
import envy.client.settings.BoolSetting;
import envy.client.settings.DoubleSetting;
import envy.client.settings.Setting;
import envy.client.settings.SettingGroup;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import envy.client.systems.modules.Modules;
import envy.client.systems.modules.world.Timer;
import envy.client.utils.player.PlayerUtils;
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

    private final Setting<Boolean> Timer = sgGeneral.add(new BoolSetting.Builder()
        .name("Timer")
        .description("Enable timer")
        .defaultValue(false)
        .build()
    );

    private final Setting<Double> TimerMultiplier = sgGeneral.add(new DoubleSetting.Builder()
        .name("multiplier")
        .description("The timer multiplier amount.")
        .defaultValue(1)
        .min(0.1)
        .sliderRange(0.1, 3)
        .visible(() -> Timer.get())
        .build()
    );

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
        if (PlayerUtils.isMoving()) {
            if (Timer.get()) {
                Modules.get().get(Timer.class).setOverride(TimerMultiplier.get());
            }
        }
    }
}

