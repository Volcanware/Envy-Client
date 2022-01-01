package mathax.client.systems.modules.misc;

import mathax.client.eventbus.EventHandler;
import mathax.client.events.entity.player.InteractEvent;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import net.minecraft.item.Items;

public class MultiTask extends Module {

    public MultiTask() {
        super(Categories.Misc, Items.NETHERITE_PICKAXE, "multi-task", "Allows you to eat while mining a block.");
    }

    @EventHandler
    public void onInteractEvent(InteractEvent event) {
        event.usingItem = false;
    }
}
