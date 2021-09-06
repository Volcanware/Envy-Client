package mathax.client.legacy.systems.modules.misc;

import mathax.client.legacy.systems.modules.Categories;
import mathax.client.legacy.systems.modules.Module;
import mathax.client.legacy.systems.modules.Modules;
import net.minecraft.item.Items;

import java.util.ArrayList;

public class Panic extends Module {

    public Panic(){
        super(Categories.Misc, Items.COMMAND_BLOCK, "panic", "Disables all active modules.");
    }

    @Override
    public void onActivate() {
        info("All modules disabled.");
        new ArrayList<>(Modules.get().getActive()).forEach(Module::toggle);
    }
}
