package mathax.client.legacy.systems.modules.render;

import mathax.client.legacy.systems.modules.Categories;
import mathax.client.legacy.systems.modules.Module;

public class UnfocusedCPU extends Module {
    public UnfocusedCPU() {
        super(Categories.Render, "unfocused-CPU", "Will not render anything when your Minecraft window is not focused.");
    }
}
