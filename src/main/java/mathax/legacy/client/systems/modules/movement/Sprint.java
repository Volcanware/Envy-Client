package mathax.legacy.client.systems.modules.movement;

import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.settings.BoolSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.eventbus.EventHandler;
import net.minecraft.item.Items;

public class Sprint extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Boolean> whenStationary = sgGeneral.add(new BoolSetting.Builder()
        .name("when-stationary")
        .description("Continues sprinting even if you do not move.")
        .defaultValue(true)
        .build()
    );

    public Sprint() {
        super(Categories.Movement, Items.DIAMOND_BOOTS, "sprint", "Automatically sprints.");
    }

    @Override
    public void onDeactivate() {
        mc.player.setSprinting(false);
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player.forwardSpeed > 0 && !whenStationary.get()) mc.player.setSprinting(true);
        else if (whenStationary.get()) mc.player.setSprinting(true);
    }
}
