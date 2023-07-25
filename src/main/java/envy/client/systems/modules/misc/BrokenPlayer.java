package envy.client.systems.modules.misc;

import envy.client.eventbus.EventHandler;
import envy.client.events.world.TickEvent;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import net.minecraft.item.Items;

import java.util.Random;

public class BrokenPlayer extends Module {

    @EventHandler
    public void onTick(TickEvent.Post event) {
        Random random = new Random();
        int randomOffset = random.nextInt(5) - 1; // generates a random number between -1 and 1
        mc.player.bodyYaw = mc.player.prevBodyYaw + 0.7f + randomOffset;
        mc.player.headYaw = mc.player.prevHeadYaw + 0.5f + randomOffset;
    }
    public BrokenPlayer() {
        super(Categories.Fun, Items.PAPER, "BrokenPlayer", "Makes the Player Act Oddly");
    }
}
