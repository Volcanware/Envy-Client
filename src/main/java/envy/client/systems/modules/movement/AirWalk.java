package envy.client.systems.modules.movement;

import envy.client.eventbus.EventHandler;
import envy.client.events.world.TickEvent;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import net.minecraft.item.Items;

public class AirWalk extends Module {

    public AirWalk() {
        super(Categories.Movement, Items.FEATHER, "air-walk", "Allows you to walk on air.");
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player.getVelocity() != null) {
            mc.player.setVelocity(mc.player.getVelocity().x, 0, mc.player.getVelocity().z);
        }
    }
}
