package mathax.client.legacy.systems.modules.combat;

import mathax.client.legacy.systems.modules.Categories;
import mathax.client.legacy.systems.modules.Module;
import net.minecraft.item.Items;

public class PistonAura extends Module {

    public PistonAura() {
        super(Categories.Combat, Items.PISTON, "piston-aura", "Moves crystals into people using pistons and explodes them.");
    }


}
