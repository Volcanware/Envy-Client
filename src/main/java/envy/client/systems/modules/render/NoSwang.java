package envy.client.systems.modules.render;

import envy.client.eventbus.EventHandler;
import envy.client.events.world.TickEvent;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import net.minecraft.item.Items;

public class NoSwang extends Module {

    public NoSwang() {
        super(Categories.Render, Items.AIR, "NoSwang", "Removes the swang animation | Client Side Only");
    }

    @EventHandler //hmmm, maybe volcans ip
    private void onTick(TickEvent.Post event) {
        if (mc.player.handSwingProgress > 0) {
            mc.player.handSwingProgress = 0;
        }
    }
}
