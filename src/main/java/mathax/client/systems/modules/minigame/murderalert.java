/*package mathax.client.systems.modules.minigame;

import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.item.Items.IRON_SWORD;

public class murderalert extends Module {

    public murderalert() {
        super(Categories.Minigame, Items.DIAMOND_BOOTS, "Murder-Alert", "Alert's When A Player Shows an Iron Sword");
    }

    public void onTick() {
        if (mc.player.getStackInHand(Hand.MAIN_HAND).getItem() == Items.IRON_SWORD) {
            warning("A Player is holding a sword!");
        }
    }
}*/
