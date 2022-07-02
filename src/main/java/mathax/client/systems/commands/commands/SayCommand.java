package mathax.client.systems.commands.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import mathax.client.mixin.ClientPlayerEntityAccessor;
import mathax.client.systems.commands.Command;
import mathax.client.systems.modules.Modules;
import mathax.client.utils.misc.ChatUtils;
import mathax.client.systems.modules.chat.BetterChat;
import net.minecraft.command.CommandSource;
import net.minecraft.network.message.ChatMessageSigner;
import net.minecraft.network.message.MessageSignature;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class SayCommand extends Command {
    public String messageText = "";

    public SayCommand() {
        super("say", "Sends messages in chat.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("message", StringArgumentType.greedyString()).executes(context -> {
            BetterChat bc = Modules.get().get(BetterChat.class);

            if (bc.isActive()) {
                messageText = context.getArgument("message", String.class);

                String message = messageText;

                if (bc.emojiFix.get()) message = bc.applyEmojiFix(message);

                if (bc.annoy.get()) message = bc.applyAnnoy(message);

                switch (bc.fancy.get()) {
                    case Full_Width -> message = bc.applyFull(message);
                    case Small_CAPS -> message = bc.applySmall(message);
                    case UwU -> message = bc.applyUwU(message);
                    case Leet -> message = bc.applyLeet(message);
                    case ZV -> message = bc.applyZV(message);
                }

                if (bc.prefix.get()) message = bc.getAffix(bc.prefixText.get(), bc.prefixFont.get(), bc.prefixRandom.get()) + message;

                if (bc.suffix.get()) message = bc.getAffix(bc.suffixText.get(), bc.suffixFont.get(), bc.suffixRandom.get()) + message;

                if (bc.coordsProtection.get() && bc.containsCoordinates(message)) {
                    MutableText warningMessage = Text.literal("It looks like there are coordinates in your message! ");

                    MutableText sendButton = bc.getSendButton(message);
                    warningMessage.append(sendButton);

                    ChatUtils.sendMsg(warningMessage);

                    return SINGLE_SUCCESS;
                }

                MessageSignature messageSignature = ((ClientPlayerEntityAccessor) mc.player)._signChatMessage(ChatMessageSigner.create(mc.player.getUuid()), Text.literal(message));
                mc.getNetworkHandler().sendPacket(new ChatMessageC2SPacket(message, messageSignature, false));
            } else {
                MessageSignature messageSignature = ((ClientPlayerEntityAccessor) mc.player)._signChatMessage(ChatMessageSigner.create(mc.player.getUuid()), Text.literal(context.getArgument("message", String.class)));
                mc.getNetworkHandler().sendPacket(new ChatMessageC2SPacket(context.getArgument("message", String.class), messageSignature, false));
            }

            return SINGLE_SUCCESS;
        }));
    }
}
