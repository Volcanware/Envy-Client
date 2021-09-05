package mathax.client.legacy.systems.modules.player;

import mathax.client.legacy.systems.modules.Categories;
import mathax.client.legacy.systems.modules.Module;
import net.minecraft.item.Items;

public class LiquidInteract extends Module {
    public LiquidInteract() {
        super(Categories.Player, Items.WATER_BUCKET, "liquid-interact", "Allows you to interact with liquids.");
    }
}
