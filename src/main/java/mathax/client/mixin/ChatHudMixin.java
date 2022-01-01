package mathax.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import mathax.client.events.game.ReceiveMessageEvent;
import mathax.client.mixininterface.IChatHud;
import mathax.client.systems.modules.Modules;
import mathax.client.utils.misc.text.StringCharacterVisitor;
import mathax.client.MatHax;
import mathax.client.systems.modules.client.ClientSpoof;
import mathax.client.systems.modules.chat.BetterChat;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.option.ChatVisibility;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.regex.Pattern;

import static mathax.client.MatHax.mc;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin implements IChatHud {
    private static final Pattern BARITONE_PREFIX_REGEX = Pattern.compile("^\\s{0,2}(<[0-9]{1,2}:[0-9]{1,2}>\\s)?\\[Baritone\\]");
    private static final Pattern BARITONE_PREFIX_REGEX_2 = Pattern.compile("^\\s{0,2}(<[0-9]{1,2}:[0-9]{1,2}:[0-9]{1,2}>\\s)?\\[Baritone\\]");

    private static final Identifier MATHAX_CHAT_ICON = new Identifier("mathax", "textures/icons/icon64.png");
    private static final Identifier METEOR_CHAT_ICON = new Identifier("mathax", "textures/icons/meteor64.png");
    private static final Identifier BARITONE_CHAT_ICON = new Identifier("mathax", "textures/icons/baritone.png");

    @Shadow @Final private List<ChatHudLine<OrderedText>> visibleMessages;
    @Shadow private int scrolledLines;

    @Shadow protected abstract void addMessage(Text message, int messageId, int timestamp, boolean refresh);

    @Unique private boolean skipOnAddMessage;

    @Inject(at = @At("HEAD"), method = "addMessage(Lnet/minecraft/text/Text;I)V", cancellable = true)
    private void onAddMessage(Text text, int id, CallbackInfo info) {
        if (skipOnAddMessage) return;

        ReceiveMessageEvent event = MatHax.EVENT_BUS.post(ReceiveMessageEvent.get(text, id));

        if (event.isCancelled()) info.cancel();
        else if (event.isModified()) {
            info.cancel();

            skipOnAddMessage = true;
            addMessage(event.getMessage(), id);
            skipOnAddMessage = false;
        }
    }

    @Redirect(method = "addMessage(Lnet/minecraft/text/Text;IIZ)V", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I"))
    private int addMessageListSizeProxy(List<ChatHudLine> list) {
        BetterChat betterChat = Modules.get().get(BetterChat.class);
        return betterChat.isLongerChat() && betterChat.getChatLength() > 100 ? 1 : list.size();
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(MatrixStack matrices, int tickDelta, CallbackInfo info) {
        if (!Modules.get().get(BetterChat.class).displayPlayerHeads()) return;
        if (mc.options.chatVisibility == ChatVisibility.HIDDEN) return;
        int maxLineCount = mc.inGameHud.getChatHud().getVisibleLineCount();

        double d = mc.options.chatOpacity * 0.8999999761581421D + 0.10000000149011612D;
        double g = 9.0D * (mc.options.chatLineSpacing + 1.0D);
        double h = -8.0D * (mc.options.chatLineSpacing + 1.0D) + 4.0D * mc.options.chatLineSpacing + 8.0D;

        matrices.push();
        matrices.translate(2, -0.1f, 10);
        RenderSystem.enableBlend();
        for (int m = 0; m + this.scrolledLines < this.visibleMessages.size() && m < maxLineCount; ++m) {
            ChatHudLine<OrderedText> chatHudLine = this.visibleMessages.get(m + this.scrolledLines);
            if (chatHudLine != null) {
                int x = tickDelta - chatHudLine.getCreationTick();
                if (x < 200 || isChatFocused()) {
                    double o = isChatFocused() ? 1.0D : getMessageOpacityMultiplier(x);
                    if (o * d > 0.01D) {
                        double s = ((double)(-m) * g);
                        var visitor = new StringCharacterVisitor();
                        chatHudLine.getText().accept(visitor);
                        drawIcon(matrices, visitor.result.toString(), (int)(s + h), (float)(o * d));
                    }
                }
            }
        }

        RenderSystem.disableBlend();
        matrices.pop();
    }

    @Override
    public void add(Text message, int messageId, int timestamp, boolean refresh) {
        addMessage(message, messageId, timestamp, refresh);
    }

    private boolean isChatFocused() {
        return mc.currentScreen instanceof ChatScreen;
    }

    @Shadow
    private static double getMessageOpacityMultiplier(int age) {
        throw new AssertionError();
    }

    @Shadow
    protected abstract void addMessage(Text message, int messageId);

    private void drawIcon(MatrixStack matrices, String line, int y, float opacity) {
        ClientSpoof cs = Modules.get().get(ClientSpoof.class);

        if (getMatHax().matcher(line).find()) {
            if (cs.changeChatFeedbackIcon()) RenderSystem.setShaderTexture(0, METEOR_CHAT_ICON);
            else RenderSystem.setShaderTexture(0, MATHAX_CHAT_ICON);
            matrices.push();
            RenderSystem.setShaderColor(1, 1, 1, opacity);
            matrices.translate(0, y, 0);
            matrices.scale(0.125f, 0.125f, 1);
            DrawableHelper.drawTexture(matrices, 0, 0, 0f, 0f, 64, 64, 64, 64);
            RenderSystem.setShaderColor(1, 1, 1, 1);
            matrices.pop();
            return;
        } else if (getMatHax2().matcher(line).find()) {
            if (cs.changeChatFeedbackIcon()) RenderSystem.setShaderTexture(0, METEOR_CHAT_ICON);
            else RenderSystem.setShaderTexture(0, MATHAX_CHAT_ICON);
            matrices.push();
            RenderSystem.setShaderColor(1, 1, 1, opacity);
            matrices.translate(0, y, 0);
            matrices.scale(0.125f, 0.125f, 1);
            DrawableHelper.drawTexture(matrices, 0, 0, 0f, 0f, 64, 64, 64, 64);
            RenderSystem.setShaderColor(1, 1, 1, 1);
            matrices.pop();
            return;
        } else if (BARITONE_PREFIX_REGEX.matcher(line).find()) {
            RenderSystem.setShaderTexture(0, BARITONE_CHAT_ICON);
            matrices.push();
            RenderSystem.setShaderColor(1, 1, 1, opacity);
            matrices.translate(0, y, 10);
            matrices.scale(0.125f, 0.125f, 1);
            DrawableHelper.drawTexture(matrices, 0, 0, 0f, 0f, 64, 64, 64, 64);
            RenderSystem.setShaderColor(1, 1, 1, 1);
            matrices.pop();
            return;
        } else if (BARITONE_PREFIX_REGEX_2.matcher(line).find()) {
            RenderSystem.setShaderTexture(0, BARITONE_CHAT_ICON);
            matrices.push();
            RenderSystem.setShaderColor(1, 1, 1, opacity);
            matrices.translate(0, y, 10);
            matrices.scale(0.125f, 0.125f, 1);
            DrawableHelper.drawTexture(matrices, 0, 0, 0f, 0f, 64, 64, 64, 64);
            RenderSystem.setShaderColor(1, 1, 1, 1);
            matrices.pop();
            return;
        }

        Identifier skin = getMessageTexture(line);
        if (skin != null) {
            RenderSystem.setShaderColor(1, 1, 1, opacity);
            RenderSystem.setShaderTexture(0, skin);
            DrawableHelper.drawTexture(matrices, 0, y, 8, 8, 8.0F, 8.0F,8, 8, 64, 64);
            DrawableHelper.drawTexture(matrices, 0, y, 8, 8, 40.0F, 8.0F,8, 8, 64, 64);
            RenderSystem.setShaderColor(1, 1, 1, 1);
        }
    }

    private Pattern getMatHax() {
        ClientSpoof cs = Modules.get().get(ClientSpoof.class);
        if (cs.changeChatFeedback()) return Pattern.compile("^\\s{0,2}(<[0-9]{1,2}:[0-9]{1,2}>\\s)?\\[" + cs.chatFeedbackText + "\\]");
        return Pattern.compile("^\\s{0,2}(<[0-9]{1,2}:[0-9]{1,2}>\\s)?\\[MatHax\\]");
    }

    private Pattern getMatHax2() {
        ClientSpoof cs = Modules.get().get(ClientSpoof.class);
        if (cs.changeChatFeedback()) return Pattern.compile("^\\s{0,2}(<[0-9]{1,2}:[0-9]{1,2}:[0-9]{1,2}>\\s)?\\[" + cs.chatFeedbackText.get() + "\\]");
        return Pattern.compile("^\\s{0,2}(<[0-9]{1,2}:[0-9]{1,2}:[0-9]{1,2}>\\s)?\\[MatHax\\]");
    }

    private static Identifier getMessageTexture(String message) {
        if (mc.getNetworkHandler() == null) return null;
        for (String part : message.split("(§.)|[^\\w]")) {
            if (part.isBlank()) continue;
            PlayerListEntry p = mc.getNetworkHandler().getPlayerListEntry(part);
            if (p != null) return p.getSkinTexture();
        }

        return null;
    }
}
