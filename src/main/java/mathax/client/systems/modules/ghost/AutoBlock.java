package mathax.client.systems.modules.ghost;

import mathax.client.eventbus.EventHandler;
import mathax.client.events.world.TickEvent;
import mathax.client.systems.modules.Categories;
import net.minecraft.item.Items;
import mathax.client.systems.modules.Module;
import mathax.client.systems.modules.Modules;

import java.util.Optional;

import static mathax.client.MatHax.mc;

public class AutoBlock extends Module {

    public AutoBlock() {
        super(Categories.Ghost, Items.COMMAND_BLOCK, "auto-block", "Automatically blocks | Works best on 1.8 servers");
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player.handSwingProgress > 0 && (mc.player.getOffHandStack().getItem().equals(Items.SHIELD))) {
            mc.options.useKey.setPressed(true);
        }
        else {
            mc.options.useKey.setPressed(false);
        }
    }
}
