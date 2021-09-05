package mathax.client.legacy.systems.modules.render;

import mathax.client.legacy.systems.modules.Categories;
import mathax.client.legacy.systems.modules.Module;
import net.minecraft.item.Items;

public class NoBob extends Module {
    public NoBob() {
        super(Categories.Render, Items.COMMAND_BLOCK, "no-bob", "Disables hand animation.");
    }
}
