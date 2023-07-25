package envy.client.systems.modules.movement;

import envy.client.eventbus.EventHandler;
import envy.client.events.entity.player.JumpVelocityMultiplierEvent;
import envy.client.settings.DoubleSetting;
import envy.client.settings.Setting;
import envy.client.settings.SettingGroup;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import net.minecraft.item.Items;

public class HighJump extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Double> multiplier = sgGeneral.add(new DoubleSetting.Builder()
        .name("jump-multiplier")
        .description("Jump height multiplier.")
        .defaultValue(1.1)
        .min(0)
        .sliderRange(0, 8)
        .build()
    );

    public HighJump() {
        super(Categories.Movement, Items.RABBIT, "high-jump", "Makes you jump higher than normal.");
    }

    @EventHandler
    private void onJumpVelocityMultiplier(JumpVelocityMultiplierEvent event) {
        event.multiplier *= multiplier.get();
    }
}
