package mathax.legacy.client.systems.modules.misc;

import mathax.legacy.client.eventbus.EventHandler;
import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.utils.player.FindItemResult;
import mathax.legacy.client.utils.player.InvUtils;
import mathax.legacy.client.utils.player.PlayerUtils;
import mathax.legacy.client.utils.world.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.RenameItemC2SPacket;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
/*/-----------------------------------------------------------------------------------------------------------------/*/
/*/ Used from Auto Anvil Dupe addon by Timoreo22                                                                    /*/
/*/ https://github.com/timoreo22/auto-anvil-dupe/blob/main/src/main/java/fr/timoreo/autodupe/modules/AnvilDupe.java /*/
/*/-----------------------------------------------------------------------------------------------------------------/*/

public class AutoAnvilDupe extends Module {
    private BlockPos target;

    private int prevSlot;
    private int first = -1;

    private boolean didDupe = false;
    private boolean pickingUp = false;

    private ItemStack toDupe = null;

    public AutoAnvilDupe() {
        super(Categories.Misc, Items.ANVIL, "auto-anvil-dupe", "Automatically dupes using the anvil dupe.");
    }

    @Override
    public void onActivate() {
        target = null;
        prevSlot = mc.player.getInventory().selectedSlot;
        didDupe = false;
        first = -1;
        toDupe = mc.player.getInventory().getStack(0).copy();
    }

    @Override
    public void onDeactivate() {
        if (mc.crosshairTarget == null) return;
        InvUtils.swap(prevSlot, false);
        toDupe = null;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (target == null) {
            if (mc.crosshairTarget == null) {
                toggle();
                return;
            }

            if (mc.crosshairTarget.getType() != HitResult.Type.BLOCK) return;

            BlockPos pos = ((BlockHitResult) mc.crosshairTarget).getBlockPos().up();
            BlockState state = mc.world.getBlockState(pos);

            if (isAnvil(mc.world.getBlockState(pos.down()).getBlock())) {
                target = ((BlockHitResult) mc.crosshairTarget).getBlockPos();
                return;
            }

            if (state.getMaterial().isReplaceable() || isAnvil(state.getBlock())) target = ((BlockHitResult) mc.crosshairTarget).getBlockPos().up();
            else return;
        }

        if (PlayerUtils.distanceTo(target) > mc.interactionManager.getReachDistance()) {
            error("Target block pos out of reach!");
            target = null;
            return;
        }

        if (mc.world.getBlockState(target).getMaterial().isReplaceable()) {
            FindItemResult echest = InvUtils.findInHotbar(AutoAnvilDupe::isAnvil);
            didDupe = false;
            if (!echest.found()) {
                error("No Anvils in hotbar, disabling...");
                toggle();
                return;
            }

            BlockUtils.place(target, echest, true, 0, true);
        }

        if (isAnvil(mc.world.getBlockState(target).getBlock())) {
            if (mc.player.currentScreenHandler == mc.player.playerScreenHandler) {
                ActionResult result = mc.interactionManager.interactBlock(mc.player, mc.world, Hand.OFF_HAND, (BlockHitResult) mc.crosshairTarget);
                if (result == ActionResult.SUCCESS && mc.player.currentScreenHandler instanceof AnvilScreenHandler) doDupeTick();
            } else if (!(mc.player.currentScreenHandler instanceof AnvilScreenHandler)) toggle();
            else doDupeTick();
        }
    }

    private void doDupeTick() {
        if (mc.player.currentScreenHandler instanceof AnvilScreenHandler) {
            if (pickingUp) {
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, ScreenHandler.EMPTY_SPACE_SLOT_INDEX, 0, SlotActionType.PICKUP, mc.player);
                pickingUp = false;
                return;
            }

            if (didDupe) {
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 30, 0, SlotActionType.PICKUP, mc.player);
                pickingUp = true;
                didDupe = false;
                return;
            }

            if (mc.player.experienceLevel == 0) {
                error("Out of XP, disabling...");
                toggle();
                return;
            }

            if (!mc.player.currentScreenHandler.getSlot(0).hasStack()) {
                if (!mc.player.currentScreenHandler.getSlot(30).hasStack()) return;
                if (!mc.player.currentScreenHandler.getSlot(30).getStack().isItemEqual(toDupe)) {
                    didDupe = true;
                    return;
                }

                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 30, 0, SlotActionType.QUICK_MOVE, mc.player);
                first = 4;
                return;
            }

            if (first == 2) {
                first = -1;
                String newName;
                String name = mc.player.currentScreenHandler.getSlot(0).getStack().getName().asString();
                if (!name.endsWith(" ")) newName = name + " ";
                else newName = name.substring(0, name.length() - 1);
                ((AnvilScreenHandler) mc.player.currentScreenHandler).setNewItemName(newName);
                mc.player.networkHandler.sendPacket(new RenameItemC2SPacket(newName));
                return;
            }

            if (first > 0) {
                first--;
                return;
            }

            if (((AnvilScreenHandler) mc.player.currentScreenHandler).getLevelCost() != 1) return;
            if (mc.player.getInventory().getEmptySlot() == -1) {
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 2, 0, SlotActionType.PICKUP, mc.player);
                didDupe = true;
            }
        }
    }

    private static boolean isAnvil(ItemStack itemStack) {
        return itemStack.getItem() == Items.DAMAGED_ANVIL || itemStack.getItem() == Items.CHIPPED_ANVIL || itemStack.getItem() == Items.ANVIL;
    }

    private static boolean isAnvil(Block b) {
        return b == Blocks.DAMAGED_ANVIL || b == Blocks.CHIPPED_ANVIL || b == Blocks.ANVIL;
    }
}
