package mathax.client.systems.modules.render;

import mathax.client.eventbus.EventHandler;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import net.minecraft.item.Items;

public class Australia extends Module {

    public Australia() {
        super(Categories.Render, Items.AIR, "australia", "Makes you look like you're in Australia.");
    }

    @EventHandler
    public boolean onActivate() {
        mc.options.getFov().setValue(260);
        info("You are now in Australia.");
        return false;
    }

    @EventHandler
    public void onDeactivate() {
        mc.options.getFov().setValue(110);
        info("You are no longer in Australia.");
    }
}
