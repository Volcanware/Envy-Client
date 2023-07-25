package envy.client.systems.modules.misc;

import envy.client.eventbus.EventHandler;
import envy.client.events.world.TickEvent;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import net.minecraft.item.Items;

import java.util.Random;

public class CrazyCape extends Module {

    @EventHandler
    public void onTick(TickEvent.Post event) {
        Random random = new Random();
        int randomOffset = random.nextInt(5) - 1; // generates a random number between -1 and 1
        mc.player.capeX = mc.player.prevCapeX + 0.7 + randomOffset;
        mc.player.capeY = mc.player.prevCapeY + 0.5 + randomOffset;
        mc.player.capeZ = mc.player.prevCapeZ + 0.7 + randomOffset;
    }
    public CrazyCape() {
        super(Categories.Fun, Items.PAPER, "Crazy Cape", "Breaks the Cape Physics");
    }
}
