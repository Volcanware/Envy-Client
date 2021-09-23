package mathax.legacy.client.systems.modules.player;

import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import net.minecraft.item.Items;

public class LiquidInteract extends Module {
    public LiquidInteract() {
        super(Categories.Player, Items.WATER_BUCKET, "liquid-interact", "Allows you to interact with liquids");
    }
}
