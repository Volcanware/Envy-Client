package mathax.legacy.client.systems.modules.movement;

import mathax.legacy.client.eventbus.EventHandler;
import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.mixininterface.IVec3d;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;

public class Gravity extends Module {
    public Gravity() {
        super(Categories.Movement, Items.FEATHER, "gravity", "Changes gravity to moon gravity.");
    }

    @EventHandler
    private void onTick(final TickEvent.Post event) {
        if (mc.options.keySneak.isPressed()) return;
        Vec3d velocity = mc.player.getVelocity();
        ((IVec3d) velocity).set(velocity.x, velocity.y + 0.0568000030517578, velocity.z);
    }
}
