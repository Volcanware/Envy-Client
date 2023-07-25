package envy.client.systems.modules.experimental;

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

public class ResetVL extends Module {
    public ResetVL() {
        super(Categories.Experimental, Items.AIR, "ResetVL", "Tries to reset your violation level");
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Double> timer = sgGeneral.add(new DoubleSetting.Builder()
        .name("Timer")
        .description("What timer to use")
        .defaultValue(1.1)
        .min(0)
        .sliderMax(5)
        .build()
    );

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player != null && mc.world != null){
            Modules.get().get(Timer.class).setOverride(timer.get());
            if (mc.player.isOnGround())
                mc.player.jump();}
    }

    @Override
    public void onDeactivate() {
        Modules.get().get(Timer.class).setOverride(Timer.OFF);
    }
}
