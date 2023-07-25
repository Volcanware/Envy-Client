package envy.client.systems.modules.world;

import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import net.minecraft.item.Items;

public class AntiCactus extends Module {
    //how this works, who knows
    public AntiCactus() {
        super(Categories.World, Items.CACTUS, "anti-cactus", "Prevents you from taking damage from cactus.");
    }
}
