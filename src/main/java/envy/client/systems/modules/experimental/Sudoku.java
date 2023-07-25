package envy.client.systems.modules.experimental;

import envy.client.eventbus.EventHandler;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import net.minecraft.item.Items;

public class Sudoku extends Module {

    public Sudoku() {
        super(Categories.Experimental, Items.COMMAND_BLOCK, "Sudoku", "Self Destruct");
    }

    @EventHandler
    public boolean onActivate() {
        throw new NullPointerException("Shit");
    }
}
