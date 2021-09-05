package mathax.client.legacy.systems.modules.render;

import mathax.client.legacy.systems.modules.Categories;
import mathax.client.legacy.systems.modules.Module;
import net.minecraft.item.Items;

public class CustomCrosshair extends Module {
    public CustomCrosshair() {
        super(Categories.Render, Items.PINK_STAINED_GLASS_PANE, "custom-crosshair", "Allows you to customize your crosshair.");
    }
}
