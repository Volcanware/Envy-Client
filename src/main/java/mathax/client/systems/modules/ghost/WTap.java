
package mathax.client.systems.modules.ghost;

import baritone.api.utils.input.Input;
import mathax.client.eventbus.EventHandler;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import mathax.client.utils.network.PacketUtils;
import net.minecraft.item.Items;

public final class WTap<PreMotionEvent> extends Module {

    public static int ticks;

    public WTap() {
        super(Categories.Ghost, Items.DIAMOND_SWORD, "WTap", "Makes people take more knockback");
    }

    @EventHandler
    public void onTick() {
        if (mc.player.handSwinging) {
            mc.player.setSprinting(true);
        }
    }
}

