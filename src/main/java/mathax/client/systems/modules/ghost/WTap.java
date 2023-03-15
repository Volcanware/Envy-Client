
package mathax.client.systems.modules.ghost;

import baritone.api.utils.input.Input;
import mathax.client.eventbus.EventHandler;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import mathax.client.utils.network.PacketUtils;
import net.minecraft.item.Items;

public final class WTap<PreMotionEvent, AttackEvent> extends Module {

    public static int ticks;

    public WTap() {
        super(Categories.Ghost, Items.DIAMOND_SWORD, "WTap", "Makes people take more knockback");
    }

    public void onAttackEvent(AttackEvent event) {
        ticks = 0;
    }

    public void onPreMotion(PreMotionEvent event) {
        ++ticks;
        if (mc.player.isSprinting()) {
                if (ticks == 2) {
                    mc.player.setSprinting(false);
                }
                if (ticks == 3) {
                    mc.player.setSprinting(true);
                }
            }
        }
    }
