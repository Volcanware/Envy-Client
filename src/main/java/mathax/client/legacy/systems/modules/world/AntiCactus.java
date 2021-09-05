package mathax.client.legacy.systems.modules.world;

import mathax.client.legacy.systems.modules.Categories;
import mathax.client.legacy.systems.modules.Module;
import net.minecraft.item.Items;

public class AntiCactus extends Module {
    public AntiCactus() {
        super(Categories.World, Items.CACTUS, "anti-cactus", "Prevents you from taking damage from cacti.");
    }
}
