package envy.client.systems.modules.render;

import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import net.minecraft.item.Items;

public class InstantSneak extends Module {
    public InstantSneak() {
        super(Categories.Render, Items.COMMAND_BLOCK, "instant-sneak", "Removes sneak animation making it look like in older versions."); //who sneaks nowadays
    }
}
