/*
package mathax.client.systems.modules.ghost;

import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import mathax.client.utils.network.PacketUtils;
import net.minecraft.item.Items;

public final class WTap<PreMotionEvent> extends Module {

    public static int ticks;

    public WTap() {
        super(Categories.Combat, Items.DIAMOND_SWORD, "WTap", "Makes people take more knockback");
    }

    public void onTick() {

        assert mc.player != null;
        if (mc.player.handSwinging) {
            ticks = 0;
        }
        ++ticks;
        if (mc.player.isSprinting()) {
            if (ticks == 2) {
                mc.player.setSprinting(false);
            }

            if (ticks == 3) {
                mc.player.setSprinting(true);
            }
        } else if (ticks < 10) {
            mc.player.setSprinting(true);
            mc.player.setSprinting(false);
        }
    }
}
*/
