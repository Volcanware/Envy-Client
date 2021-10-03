package mathax.legacy.client.systems.modules.movement;

import mathax.legacy.client.eventbus.EventHandler;
import mathax.legacy.client.events.world.CollisionShapeEvent;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import net.minecraft.item.Items;
import net.minecraft.util.shape.VoxelShapes;

/*/                    /*/
/*/ Made by C10udburst /*/
/*/                    /*/

public class Prone extends Module {

    public Prone() {
        super(Categories.Movement, Items.WATER_BUCKET, "prone", "Become prone on demand.");
    }

    @EventHandler
    private void onCollisionShape(CollisionShapeEvent event) {
        if (mc.world == null || mc.player == null) return;
        if (event.state == null) return;

        if (event.pos.getY() != mc.player.getY() + 1) return;

        event.shape = VoxelShapes.fullCube();
    }
}
