package mathax.client.legacy.systems.modules.render;

import mathax.client.legacy.systems.modules.Categories;
import mathax.client.legacy.systems.modules.Module;

public class NoBob extends Module {
    public NoBob() {
        super(Categories.Render, "no-bob", "Disables hand animation.");
    }
}
