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

public class FastBow extends Module {

    public FastBow() {
        super(Categories.Player, Items.BOW, "fast-bow", "Allows you to shoot faster.");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> timer = sgGeneral.add(new DoubleSetting.Builder()
        .name("timer")
        .description("The timer speed to use while using a Bow.")
        .defaultValue(2)
        .min(1)
        .sliderMax(10)
        .build()
    );

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player.isUsingItem() && mc.player.getActiveItem().getItem() == Items.BOW) {
            Modules.get().get(Timer.class).setOverride(timer.get());
        }
        if (mc.player.isUsingItem() && mc.player.getActiveItem().getItem() == Items.CROSSBOW) {
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
