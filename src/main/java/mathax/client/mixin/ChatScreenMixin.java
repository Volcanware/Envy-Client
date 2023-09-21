package mathax.client.mixin;

import baritone.api.BaritoneAPI;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mathax.client.MatHax;
import mathax.client.events.game.SendMessageEvent;
import mathax.client.systems.commands.Commands;
import mathax.client.systems.config.Config;
import mathax.client.systems.modules.Modules;
import mathax.client.systems.modules.chat.BetterChat;
import mathax.client.utils.misc.ChatUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin {
    @Shadow protected TextFieldWidget chatField;

    @Shadow
    public abstract boolean sendMessage(String chatText, boolean addToHistory);

    @Unique
    private boolean ignoreChatMessage;

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;setMaxLength(I)V", shift = At.Shift.AFTER))
    private void onInit(CallbackInfo info) {
        if (Modules.get().get(BetterChat.class).isInfiniteChatBox()) chatField.setMaxLength(Integer.MAX_VALUE);
    }

    @Inject(method = "sendMessage", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(String message, boolean addToHistory, CallbackInfoReturnable<Boolean> info) {
        if (ignoreChatMessage) return;

        if (!message.startsWith(Config.get().prefix.get()) && !message.startsWith("/") && !message.startsWith(BaritoneAPI.getSettings().prefix.value)) {
            SendMessageEvent event = MatHax.EVENT_BUS.post(SendMessageEvent.get(message));

            if (!event.isCancelled()) {
                ignoreChatMessage = true;
                sendMessage(event.message, addToHistory);
                ignoreChatMessage = false;
            }

            info.setReturnValue(true);
            return;
        }

        if (message.startsWith(Config.get().prefix.get())) {
            try {
                Commands.get().dispatch(message.substring(Config.get().prefix.get().length()));
            } catch (CommandSyntaxException e) {
                ChatUtils.error(e.getMessage());
            }

            MinecraftClient.getInstance().inGameHud.getChatHud().addToMessageHistory(message);
            info.setReturnValue(true);
        }
    }
}
