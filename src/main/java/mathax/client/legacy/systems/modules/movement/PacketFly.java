package mathax.client.legacy.systems.modules.movement;

import mathax.client.legacy.systems.modules.Categories;
import mathax.client.legacy.systems.modules.Module;
import net.minecraft.item.Items;

public class PacketFly extends Module {
    public PacketFly() {
        super(Categories.Movement, Items.COMMAND_BLOCK, "packet-fly", "Allows you to fly using packets.");
    }
}
