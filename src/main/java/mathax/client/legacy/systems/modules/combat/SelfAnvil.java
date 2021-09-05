package mathax.client.legacy.systems.modules.combat;

import mathax.client.legacy.events.game.OpenScreenEvent;
import mathax.client.legacy.events.world.TickEvent;
import mathax.client.legacy.systems.modules.Categories;
import mathax.client.legacy.systems.modules.Module;
import mathax.client.legacy.utils.player.InvUtils;
import mathax.client.legacy.utils.world.BlockUtils;
import mathax.client.legacy.bus.EventHandler;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.item.Items;

public class SelfAnvil extends Module {
    public SelfAnvil() {
        super(Categories.Combat, Items.ANVIL, "self-anvil", "Automatically places an anvil on you to prevent other players from going into your hole.");
    }

    @EventHandler
    private void onOpenScreen(OpenScreenEvent event) {
        if (event.screen instanceof AnvilScreen) event.cancel();
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (BlockUtils.place(mc.player.getBlockPos().add(0, 2, 0), InvUtils.findInHotbar(itemStack -> Block.getBlockFromItem(itemStack.getItem()) instanceof AnvilBlock), 0)) {
            toggle();
        }
    }
}
