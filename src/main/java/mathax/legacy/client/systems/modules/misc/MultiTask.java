package mathax.legacy.client.systems.modules.misc;

import mathax.legacy.client.eventbus.EventHandler;
import mathax.legacy.client.events.entity.player.InteractEvent;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import net.minecraft.item.Items;

public class MultiTask extends Module {

    public MultiTask() {
        super(Categories.Misc, Items.NETHERITE_PICKAXE, "Multi-Task", "Allows you to eat while mining a block.");
    }

    @EventHandler
    public void onInteractEvent(InteractEvent event) {
        event.usingItem = false;
    }
}
