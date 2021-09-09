package mathax.legacy.client.mixin;

import mathax.legacy.client.MatHaxLegacy;
import mathax.legacy.client.events.entity.player.FinishUsingItemEvent;
import mathax.legacy.client.events.entity.player.StoppedUsingItemEvent;
import mathax.legacy.client.events.game.ItemStackTooltipEvent;
import mathax.legacy.client.utils.Utils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Inject(method = "getTooltip", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onGetTooltip(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> info, List<Text> list) {
        if (Utils.canUpdate()) {
            MatHaxLegacy.EVENT_BUS.post(ItemStackTooltipEvent.get((ItemStack) (Object) this, list));
        }
    }

    @Inject(method = "finishUsing", at = @At("HEAD"))
    private void onFinishUsing(World world, LivingEntity user, CallbackInfoReturnable<ItemStack> info) {
        if (user == Utils.mc.player) {
            MatHaxLegacy.EVENT_BUS.post(FinishUsingItemEvent.get((ItemStack) (Object) this));
        }
    }

    @Inject(method = "onStoppedUsing", at = @At("HEAD"))
    private void onStoppedUsing(World world, LivingEntity user, int remainingUseTicks, CallbackInfo info) {
        if (user == Utils.mc.player) {
            MatHaxLegacy.EVENT_BUS.post(StoppedUsingItemEvent.get((ItemStack) (Object) this));
        }
    }
}
