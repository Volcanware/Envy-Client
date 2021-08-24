package mathax.client.legacy.systems.modules.player;

import mathax.client.legacy.systems.modules.Categories;
import mathax.client.legacy.systems.modules.Module;

public class NoBreakDelay extends Module {
    public NoBreakDelay() {
        super(Categories.Player, "no-break-delay", "Completely removes the delay between breaking blocks.");
    }
}
