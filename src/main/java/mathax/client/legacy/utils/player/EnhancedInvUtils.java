package mathax.client.legacy.utils.player;

import mathax.client.legacy.bus.EventHandler;
import mathax.client.legacy.events.world.TickEvent;
import mathax.client.legacy.utils.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Predicate;

import static mathax.client.legacy.utils.Utils.mc;

public class EnhancedInvUtils {
    private static Long currentMove;
    public static final int OFFHAND_SLOT;
    private static final Deque<Long> moveQueue;
    private static final FindItemResult findItemResult;

    static {
        OFFHAND_SLOT = 45;
        findItemResult = new FindItemResult();
        moveQueue = new ArrayDeque<Long>();
    }

    private static boolean findItemInAll1(ItemStack itemStack) {
        return true;
    }

    private static boolean findItemInMain3(ItemStack itemStack) {
        return true;
    }

    private static boolean actionContains(long l, int n) {
        return n == unpackLongTo(l) || n == unpackLongFrom(l);
    }

    public static Hand getHand(Item item) {
        Hand hand = Hand.MAIN_HAND;
        if (mc.player.getInventory().getMainHandStack().getItem() == item) {
            hand = Hand.MAIN_HAND;
        }
        return hand;
    }

    private static int unpackLongTo(long l) {
        return Utils.unpackLong2(l);
    }

    public static boolean canMove(Long l, Long l2) {
        return unpackLongPrio(l) < unpackLongPrio(l2);
    }

    public static int invIndexToSlotId(int n) {
        if (n < 9 && n != -1) {
            return 44 - (8 - n);
        }
        return n;
    }

    public static Hand getHand(Predicate<ItemStack> predicate) {
        Hand hand = null;
        if (predicate.test(mc.player.getMainHandStack())) {
            hand = Hand.MAIN_HAND;
        } else if (predicate.test(mc.player.getOffHandStack())) {
            hand = Hand.OFF_HAND;
        }
        return hand;
    }

    public static FindItemResult findItemWithCount(Item item) {
        findItemResult.slot = -1;
        findItemResult.count = 0;
        for (int i = 0; i < mc.player.getInventory().selectedSlot; ++i) {
            ItemStack itemStack = mc.player.getInventory().getStack(i);
            if (itemStack.getItem() != item) continue;
            if (!findItemResult.found()) {
                findItemResult.slot = i;
            }
            findItemResult.count += item.getMaxCount();
            if (-2 <= 0) continue;
            return null;
        }
        return findItemResult;
    }

    private static int findItem(Item item, Predicate<ItemStack> predicate, int n) {
        for (int i = 0; i < n; ++i) {
            ItemStack itemStack = mc.player.getInventory().getStack(i);
            if (itemStack != null && itemStack.getItem() != item || !predicate.test(itemStack))
                continue;
            return i;
        }
        return -1;
    }

    public static void swap(int n) {
        if (n != mc.player.getInventory().selectedSlot && n >= 0 && n < 9) {
            mc.player.getInventory().selectedSlot = n;
        }
    }

    public static int findItemInHotbar(Item item) {
        return findItemInHotbar(item, EnhancedInvUtils::findItemInHotbar2);
    }

    public static int findItemInHotbar(Item item, Predicate<ItemStack> predicate) {
        return findItem(item, predicate, 9);
    }

    private static boolean findItemInHotbar2(ItemStack itemStack) {
        return true;
    }

    @EventHandler(priority = -200)
    private static void onTick(TickEvent.Pre pre) {
        if (mc.world == null || mc.player == null || mc.player.isCreative()) {
            moveQueue.clear();
            return;
        }
        if (!mc.player.getInventory().isEmpty() && mc.currentScreen == null && mc.player.getInventory().size() == 46) {
            int n = findItemWithCount((Item) mc.player.getMainHandStack().getItem()).slot;
            if (n == -1) {
                n = mc.player.getInventory().selectedSlot;
            }
            if (n != -1) {
                clickSlot(invIndexToSlotId(n), 0, SlotActionType.PICKUP);
            }
        }
        if (!moveQueue.isEmpty() && mc.player.getInventory().size() == 46) {
            currentMove = moveQueue.remove();
            clickSlot(unpackLongFrom(currentMove), 0, SlotActionType.PICKUP);
            clickSlot(unpackLongTo(currentMove), 0, SlotActionType.PICKUP);
            clickSlot(unpackLongFrom(currentMove), 0, SlotActionType.PICKUP);
        }
    }

    public static void clickSlot(int n, int n2, SlotActionType slotActionType) {
        mc.interactionManager.clickSlot(mc.player.getInventory().selectedSlot, n, n2, slotActionType, (PlayerEntity) mc.player);
    }

    private static int unpackLongPrio(long l) {
        return Utils.unpackLong4(l);
    }

    private static int unpackLongFrom(long l) {
        return Utils.unpackLong3(l);
    }

    public static class FindItemResult {
        public int slot;
        public int count;

        public boolean found() {
            return this.slot != -1;
        }
    }
}
