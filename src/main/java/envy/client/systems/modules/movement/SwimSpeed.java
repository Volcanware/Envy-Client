package envy.client.systems.modules.movement;

import envy.client.eventbus.EventHandler;
import envy.client.events.world.TickEvent;
import envy.client.settings.DoubleSetting;
import envy.client.settings.Setting;
import envy.client.settings.SettingGroup;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import envy.client.utils.player.SwimSpeedUtils;
import net.minecraft.item.Items;

import java.util.Objects;

//This is utterly fucking retarded
public class SwimSpeed extends Module {

    public SwimSpeed() {
        super(Categories.Movement, Items.SCULK_SENSOR, "swim-speed", "Speedy");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> velocityMultiplier = sgGeneral.add(new DoubleSetting.Builder()
        .name("velocity-multiplier")
        .description("The velocity multiplier.")
        .defaultValue(1.0)
        .min(0)
        .sliderMax(10)
        .build()
    );

    @EventHandler
    public boolean onTick(TickEvent.Post event) {
        if (mc.options.forwardKey.isPressed() && Objects.requireNonNull(mc.player).isSwimming() && mc.player.isSubmergedInWater()) {
            SwimSpeedUtils.throwPlayer(velocityMultiplier.get());
        }
        return false;
    }
}
