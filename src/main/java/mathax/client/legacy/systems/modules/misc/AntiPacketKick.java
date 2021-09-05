package mathax.client.legacy.systems.modules.misc;

import mathax.client.legacy.systems.modules.Categories;
import mathax.client.legacy.systems.modules.Module;
import net.minecraft.item.Items;

public class AntiPacketKick extends Module {
    public AntiPacketKick() {
        super(Categories.Misc, Items.COMPARATOR, "anti-packet-kick", "Attempts to prevent you from being disconnected by large packets.");
    }
}
