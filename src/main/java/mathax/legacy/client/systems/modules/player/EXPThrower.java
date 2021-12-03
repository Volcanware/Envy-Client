package mathax.legacy.client.systems.modules.player;

import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.utils.player.FindItemResult;
import mathax.legacy.client.utils.player.InvUtils;
import mathax.legacy.client.utils.player.Rotations;
import mathax.legacy.client.eventbus.EventHandler;
import net.minecraft.item.Items;

public class EXPThrower extends Module {
    public EXPThrower() {
        super(Categories.Player, Items.EXPERIENCE_BOTTLE, "exp-thrower", "Automatically throws XP bottles from your hotbar.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        FindItemResult exp = InvUtils.find(Items.EXPERIENCE_BOTTLE);

        if (exp.found()) {
            Rotations.rotate(mc.player.getYaw(), 90, () -> {
                if (exp.getHand() != null) mc.interactionManager.interactItem(mc.player, mc.world, exp.getHand());
                else {
                    InvUtils.swap(exp.getSlot(), true);
                    mc.interactionManager.interactItem(mc.player, mc.world, exp.getHand());
                    InvUtils.swapBack();
                }
            });
        }
    }

    public enum Mode {
        Automatic,
        Manual
    }
}
