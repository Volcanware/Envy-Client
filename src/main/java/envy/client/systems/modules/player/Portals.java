package envy.client.systems.modules.player;

import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import net.minecraft.item.Items;

public class Portals extends Module {
    public Portals() {
        super(Categories.Player, Items.OBSIDIAN, "Anti-Portal Trap", "Allows you to use GUIs normally while in a Nether Portal.");
    }
}
