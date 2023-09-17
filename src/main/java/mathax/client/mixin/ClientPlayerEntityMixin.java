package mathax.client.mixin;

import baritone.api.BaritoneAPI;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mathax.client.MatHax;
import mathax.client.events.entity.DamageEvent;
import mathax.client.events.entity.DropItemsEvent;
import mathax.client.events.entity.player.SendMovementPacketsEvent;
import mathax.client.events.game.SendMessageEvent;
import mathax.client.systems.commands.Commands;
import mathax.client.systems.config.Config;
import mathax.client.systems.modules.Modules;
import mathax.client.systems.modules.misc.NoSignatures;
import mathax.client.systems.modules.misc.Twerk;
import mathax.client.systems.modules.movement.NoSlow;
import mathax.client.systems.modules.movement.Scaffold;
import mathax.client.systems.modules.movement.Sneak;
import mathax.client.systems.modules.movement.Velocity;
import mathax.client.systems.modules.player.Portals;
import mathax.client.utils.Utils;
import mathax.client.utils.misc.ChatUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.message.ArgumentSignatureDataMap;
import net.minecraft.network.message.ChatMessageSigner;
import net.minecraft.network.message.MessageSignature;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(value = ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    @Shadow
    @Final
    public ClientPlayNetworkHandler networkHandler;

    private boolean ignoreChatMessage;

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile, @Nullable PlayerPublicKey publicKey) {
        super(world, profile, publicKey);
    }

    @Shadow public abstract void sendChatMessage(String string);

    @Shadow
    public abstract void tickMovement();

    @Shadow
    public abstract boolean isUsingItem();

    @Shadow
    private boolean usingItem;

    @Shadow
    public abstract void useBook(ItemStack book, Hand hand);

    @Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
    private void onDropSelectedItem(boolean dropEntireStack, CallbackInfoReturnable<Boolean> info) {
        if (MatHax.EVENT_BUS.post(DropItemsEvent.get(getMainHandStack())).isCancelled()) info.setReturnValue(false);
    }

    @Inject(method = "sendChatMessage(Ljava/lang/String;Lnet/minecraft/text/Text;)V", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(String message, Text preview, CallbackInfo info) {
        if (ignoreChatMessage) return;

        if (!message.startsWith(Config.get().prefix.get()) && !message.startsWith("/") && !message.startsWith(BaritoneAPI.getSettings().prefix.value)) {
            SendMessageEvent event = MatHax.EVENT_BUS.post(SendMessageEvent.get(message));

            if (!event.isCancelled()) {
                ignoreChatMessage = true;
                sendChatMessage(event.message);
                ignoreChatMessage = false;
            }

            info.cancel();
            return;
        }

        if (message.startsWith(Config.get().prefix.get())) {
            try {
                Commands.get().dispatch(message.substring(Config.get().prefix.get().length()));
            } catch (CommandSyntaxException e) {
                ChatUtils.error(e.getMessage());
            }

            info.cancel();
        }
    }

    @Redirect(method = "updateNausea", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;"))
    private Screen updateNauseaGetCurrentScreenProxy(MinecraftClient client) {
        if (Modules.get().isActive(Portals.class)) return null;
        return client.currentScreen;
    }

    @Unique
    private boolean realUsingItem;

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void tickMovementHead(CallbackInfo ci) {
        realUsingItem = usingItem;
         if (Modules.get().get(NoSlow.class).items()) {
             usingItem = false;
         }
    }

    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void tickMovementTail(CallbackInfo ci) {
        usingItem = realUsingItem;
    }

    @Inject(method = "isSneaking", at = @At("HEAD"), cancellable = true)
    private void onIsSneaking(CallbackInfoReturnable<Boolean> info) {
        if (Modules.get().isActive(Scaffold.class)) info.setReturnValue(false);
    }

    @Inject(method = "shouldSlowDown", at = @At("HEAD"), cancellable = true)
    private void onShouldSlowDown(CallbackInfoReturnable<Boolean> info) {
        if (Modules.get().get(NoSlow.class).sneaking()) info.setReturnValue(shouldLeaveSwimmingPose());
    }

    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
    private void onPushOutOfBlocks(double x, double d, CallbackInfo info) {
        Velocity velocity = Modules.get().get(Velocity.class);
        if (velocity.isActive() && velocity.blocks.get()) info.cancel();
    }

    @Inject(method = "damage", at = @At("HEAD"))
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        if (Utils.canUpdate() && world.isClient && canTakeDamage()) MatHax.EVENT_BUS.post(DamageEvent.get(this, source));
    }

    // No Signatures

    @Inject(method = "signChatMessage", at = @At("HEAD"), cancellable = true)
    private void onSignChatMessage(ChatMessageSigner signer, Text message, CallbackInfoReturnable<MessageSignature> info) {
        if (Modules.get().isActive(NoSignatures.class)) info.setReturnValue(MessageSignature.none());
    }

    @Inject(method = "signArguments", at = @At("HEAD"), cancellable = true)
    private void onSignArguments(ChatMessageSigner signer, ParseResults<CommandSource> parseResults, @Nullable Text preview, CallbackInfoReturnable<ArgumentSignatureDataMap> info) {
        if (Modules.get().isActive(NoSignatures.class)) info.setReturnValue(ArgumentSignatureDataMap.empty());
    }

    // Rotations

    @Inject(method = "sendMovementPackets", at = @At("HEAD"))
    private void onSendMovementPacketsHead(CallbackInfo info) {
        MatHax.EVENT_BUS.post(SendMovementPacketsEvent.Pre.get());
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V", ordinal = 0))
    private void onTickHasVehicleBeforeSendPackets(CallbackInfo info) {
        MatHax.EVENT_BUS.post(SendMovementPacketsEvent.Pre.get());
    }

    @Inject(method = "sendMovementPackets", at = @At("TAIL"))
    private void onSendMovementPacketsTail(CallbackInfo info) {
        MatHax.EVENT_BUS.post(SendMovementPacketsEvent.Post.get());
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V", ordinal = 1, shift = At.Shift.AFTER))
    private void onTickHasVehicleAfterSendPackets(CallbackInfo info) {
        MatHax.EVENT_BUS.post(SendMovementPacketsEvent.Post.get());
    }

    // Sneak
    @Redirect(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSneaking()Z"))
    private boolean isSneaking(ClientPlayerEntity clientPlayerEntity) {
        return (Modules.get().get(Sneak.class).doPacket() || Modules.get().get(NoSlow.class).airStrict() || clientPlayerEntity.isSneaking()) || (Modules.get().get(Twerk.class).doPacket() || Modules.get().get(NoSlow.class).airStrict() || clientPlayerEntity.isSneaking());
    }
}
