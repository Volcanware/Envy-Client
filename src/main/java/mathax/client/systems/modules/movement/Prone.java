package mathax.client.systems.modules.movement;

import mathax.client.eventbus.EventHandler;
import mathax.client.events.world.CollisionShapeEvent;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import net.minecraft.item.Items;
import net.minecraft.util.shape.VoxelShapes;

public class Prone extends Module {
    public Prone() {
        super(Categories.Movement, Items.WATER_BUCKET, "prone", "Become prone on demand.");
    }

    @EventHandler
    private void onCollisionShape(CollisionShapeEvent event) {
        if (mc.world == null || mc.player == null || event.pos == null) return;
        if (event.state == null) return;

        if (event.pos.getY() != mc.player.getY() + 1) return;

        event.shape = VoxelShapes.fullCube();
    }
}
