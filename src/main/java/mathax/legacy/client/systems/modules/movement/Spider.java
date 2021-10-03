package mathax.legacy.client.systems.modules.movement;

import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.settings.DoubleSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.bus.EventHandler;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;

public class Spider extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> speed = sgGeneral.add(new DoubleSetting.Builder()
        .name("climb-speed")
        .description("The speed you go up blocks.")
        .defaultValue(0.2)
        .min(0.0)
        .build()
    );

    public Spider() {
        super(Categories.Movement, Items.SPIDER_EYE, "spider", "Allows you to climb walls like a spider.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!mc.player.horizontalCollision) return;

        Vec3d velocity = mc.player.getVelocity();
        if (velocity.y >= 0.2) return;

        mc.player.setVelocity(velocity.x, speed.get(), velocity.z);
    }
}
