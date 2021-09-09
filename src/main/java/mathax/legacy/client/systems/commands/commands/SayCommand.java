package mathax.legacy.client.systems.commands.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import mathax.legacy.client.systems.commands.Command;
import mathax.legacy.client.systems.modules.Modules;
import mathax.legacy.client.systems.modules.chat.BetterChat;
import mathax.legacy.client.utils.player.ChatUtils;
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
                    switch (Modules.get().get(BetterChat.class).fancyType.get()) {
                        case FullWidth -> message = Modules.get().get(BetterChat.class).applyFull(message);
                        case SmallCAPS -> message = Modules.get().get(BetterChat.class).applySmall(message);
                        case UwU -> message = Modules.get().get(BetterChat.class).applyUwU(message);
                        case Leet -> message = Modules.get().get(BetterChat.class).applyLeet(message);
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
