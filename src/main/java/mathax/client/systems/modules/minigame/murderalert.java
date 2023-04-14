package mathax.client.systems.modules.minigame;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static mathax.client.systems.modules.Categories.Player;
import static net.minecraft.item.Items.IRON_SWORD;

public class murderalert extends Module {

    public murderalert() {
        super(Categories.Experimental, Items.DIAMOND_BOOTS, "Murder-Alert", "Alert's When A Player Shows an Iron Sword");
    }

    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        List<AbstractClientPlayerEntity> players = mc.world.getPlayers();

        for (PlayerEntity player : players) {
            ItemStack heldItem = player.getInventory().getMainHandStack();
            if (heldItem.getItem() == Items.DIAMOND) {
                //player.sendMessage(Text.of("You are holding a diamond!", toString(player.getName()));
            }
        }
    }

    //private Object toString(Text name) {
    //}
}

