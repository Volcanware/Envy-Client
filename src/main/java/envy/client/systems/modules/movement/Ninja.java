package envy.client.systems.modules.movement;

import envy.client.eventbus.EventHandler;
import envy.client.events.world.TickEvent;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import net.minecraft.item.Items;
//This could have a better implimentation like not using the key and instead just doing the action
public class Ninja extends Module {
    public Ninja() {
        super(Categories.Ghost, Items.DIAMOND_SWORD, "Ninja", "SpeedBridges For You");
    }

    boolean turn = true;

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.world.getBlockState(mc.player.getSteppingPos()).isAir()) {
            if (!mc.player.isOnGround()) return;
            turn = true;
            mc.options.sneakKey.setPressed(true);
        } else if (turn) {
            turn = false;
            mc.options.sneakKey.setPressed(false);
        }
    }
}
