package envy.client.systems.modules.player;

import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import net.minecraft.item.Items;

public class NoBreakDelay extends Module {
    public NoBreakDelay() {
        super(Categories.Ghost, Items.STONE, "no-break-delay", "Completely removes the delay between breaking blocks.");
    }
}
