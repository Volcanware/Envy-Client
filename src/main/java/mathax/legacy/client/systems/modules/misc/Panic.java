package mathax.legacy.client.systems.modules.misc;

import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.systems.modules.Modules;
import net.minecraft.item.Items;

import java.util.ArrayList;

public class Panic extends Module {

    public Panic(){
        super(Categories.Misc, Items.COMMAND_BLOCK, "panic");
    }

    @Override
    public void onActivate() {
        info("All modules disabled.");
        new ArrayList<>(Modules.get().getActive()).forEach(Module::toggle);
    }
}
