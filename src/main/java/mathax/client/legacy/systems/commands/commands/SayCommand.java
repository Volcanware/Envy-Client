package mathax.client.legacy.systems.commands.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import mathax.client.legacy.systems.commands.Command;
import mathax.client.legacy.systems.modules.Modules;
import mathax.client.legacy.systems.modules.chat.BetterChat;
import mathax.client.legacy.utils.player.ChatUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class SayCommand extends Command {

    public String messageTextRaw = "";

    public SayCommand() {
        super("say", "Sends messages in chat.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("message", StringArgumentType.greedyString()).executes(context -> {
            if (Modules.get().isActive(BetterChat.class)) {
                messageTextRaw = context.getArgument("message", String.class);

                String message = messageTextRaw;

                if (Modules.get().get(BetterChat.class).annoy.get())
                    message = Modules.get().get(BetterChat.class).applyAnnoy(message);

                if (Modules.get().get(BetterChat.class).fancy.get()) {
                    if (Modules.get().get(BetterChat.class).fancyType.get() == BetterChat.FancyType.FullWidth) {
                        message = Modules.get().get(BetterChat.class).applyFull(message);
                    }
                    if (Modules.get().get(BetterChat.class).fancyType.get() == BetterChat.FancyType.SmallCAPS) {
                        message = Modules.get().get(BetterChat.class).applySmall(message);
                    }
                    if (Modules.get().get(BetterChat.class).fancyType.get() == BetterChat.FancyType.UwU) {
                        message = Modules.get().get(BetterChat.class).applyUwU(message);
                    }
                    if (Modules.get().get(BetterChat.class).fancyType.get() == BetterChat.FancyType.Leet) {
                        message = Modules.get().get(BetterChat.class).applyLeet(message);
                    }
                }

                message = Modules.get().get(BetterChat.class).getGreenChat() + message + Modules.get().get(BetterChat.class).getSuffix();

                if (Modules.get().get(BetterChat.class).coordsProtection.get() && Modules.get().get(BetterChat.class).containsCoordinates(message)) {
                    BaseText warningMessage = new LiteralText("It looks like there are coordinates in your message! ");

                    BaseText sendButton = Modules.get().get(BetterChat.class).getSendButton(message);
                    warningMessage.append(sendButton);

                    ChatUtils.sendMsg(warningMessage);

                    return SINGLE_SUCCESS;
                }

                mc.getNetworkHandler().sendPacket(new ChatMessageC2SPacket(message));
            } else {
                String messageText = context.getArgument("message", String.class);
                mc.getNetworkHandler().sendPacket(new ChatMessageC2SPacket(messageText));
            }
            return SINGLE_SUCCESS;
        }));
    }
}
