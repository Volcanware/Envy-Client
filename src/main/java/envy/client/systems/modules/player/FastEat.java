package envy.client.systems.modules.player;

import envy.client.eventbus.EventHandler;
import envy.client.events.world.TickEvent;
import envy.client.settings.DoubleSetting;
import envy.client.settings.Setting;
import envy.client.settings.SettingGroup;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import envy.client.systems.modules.Modules;
import envy.client.systems.modules.world.Timer;
import net.minecraft.item.Items;

public class FastEat extends Module {

    public FastEat() {
        super(Categories.Player, Items.GOLDEN_APPLE, "fast-eat", "Allows you to eat faster.");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> timer = sgGeneral.add(new DoubleSetting.Builder()
        .name("timer")
        .description("The timer speed to use while eating.")
        .defaultValue(2)
        .min(1)
        .sliderMax(10)
        .build()
    );

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player.isHolding(item -> item.isFood() && mc.player.isUsingItem())) {
            Modules.get().get(Timer.class).setOverride(timer.get());
        }
        else {
            Modules.get().get(Timer.class).setOverride(1);
        }
    }

    @Override
    public void onDeactivate() {
        Modules.get().get(Timer.class).setOverride(1);
    }
}
