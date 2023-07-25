package envy.client.systems.modules.movement;

import envy.client.eventbus.EventHandler;
import envy.client.events.world.TickEvent;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import net.minecraft.item.Items;

public class JetPack extends Module {


    public JetPack() {
        super(Categories.Movement, Items.FIREWORK_ROCKET, "JetPack", "Hold Space to fly");
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.options.jumpKey.isPressed())
            mc.player.jump();
    }
}
