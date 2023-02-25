package mathax.client.systems.modules.movement;

import mathax.client.eventbus.EventHandler;
import mathax.client.events.world.TickEvent;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import mathax.client.systems.modules.Modules;
import net.minecraft.item.Items;


public class FastFall extends Module {

    public FastFall() {
        super(Categories.Movement, Items.ARMOR_STAND, "fast-fall", "Allows you to fall faster.");
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player.fallDistance > 2) {
            mc.player.setVelocity(mc.player.getVelocity().x, -1, mc.player.getVelocity().z);
        }
    }
}
