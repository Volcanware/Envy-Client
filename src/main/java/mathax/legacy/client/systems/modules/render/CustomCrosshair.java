package mathax.legacy.client.systems.modules.render;

import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import net.minecraft.item.Items;

public class CustomCrosshair extends Module {

    public CustomCrosshair() {
        super(Categories.Render, Items.COMPASS, "custom-crosshair", "Renders a customizable crosshair instead of the Minecraft one.");
    }
}
