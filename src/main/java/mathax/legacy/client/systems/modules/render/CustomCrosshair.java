package mathax.legacy.client.systems.modules.render;

import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import net.minecraft.item.Items;

public class CustomCrosshair extends Module {
    public CustomCrosshair() {
        super(Categories.Render, Items.PINK_STAINED_GLASS_PANE, "custom-crosshair");
    }
}
