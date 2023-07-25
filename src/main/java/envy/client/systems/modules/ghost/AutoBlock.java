package envy.client.systems.modules.ghost;

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

public class AutoBlock extends Module {

    public AutoBlock() {
        super(Categories.Ghost, Items.COMMAND_BLOCK, "auto-block", "Automatically blocks | Works best on 1.8 servers");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> timer = sgGeneral.add(new DoubleSetting.Builder()
        .name("timer")
        .description("The timer speed to use while Blocking.")
        .defaultValue(2)
        .min(1)
        .sliderMax(10)
        .build()
    );

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player.handSwingProgress > 0 && (mc.player.getOffHandStack().getItem().equals(Items.SHIELD))) {
            mc.options.useKey.setPressed(true);
            Modules.get().get(Timer.class).setOverride(timer.get());
        }
        else {
            mc.options.useKey.setPressed(false);
            Modules.get().get(Timer.class).setOverride(1);
        }
    }

    @Override
    public void onDeactivate() {
        Modules.get().get(Timer.class).setOverride(1);
    }
}
