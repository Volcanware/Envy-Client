package envy.client.utils.player;

import envy.client.Envy;
import net.minecraft.util.Hand;

public record FindItemResult(int slot, int count) {
    public boolean found() {
        return slot != -1;
    }

    public Hand getHand() {
        if (slot == SlotUtils.OFFHAND) return Hand.OFF_HAND;
        else if (slot == Envy.mc.player.getInventory().selectedSlot) return Hand.MAIN_HAND;
        return null;
    }

    public boolean isMainHand() {
        return getHand() == Hand.MAIN_HAND;
    }

    public boolean isOffhand() {
        return getHand() == Hand.OFF_HAND;
    }

    public boolean isHotbar() {
        return slot >= SlotUtils.HOTBAR_START && slot <= SlotUtils.HOTBAR_END;
    }

    public boolean isMain() {
        return slot >= SlotUtils.MAIN_START && slot <= SlotUtils.MAIN_END;
    }

    public boolean isArmor() {
        return slot >= SlotUtils.ARMOR_START && slot <= SlotUtils.ARMOR_END;
    }
}
