package mathax.legacy.client.systems.modules.render;

import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import net.minecraft.item.Items;

public class NoBob extends Module {
    public NoBob() {
        super(Categories.Render, Items.COMMAND_BLOCK, "no-bob", "Disables hand animation");
    }
}
