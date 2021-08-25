package mathax.client.legacy.systems.commands.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import mathax.client.legacy.MatHaxClientLegacy;
import mathax.client.legacy.systems.Systems;
import mathax.client.legacy.systems.commands.Command;
import mathax.client.legacy.systems.profiles.Profile;
import mathax.client.legacy.utils.entity.fakeplayer.FakePlayerManager;
import mathax.client.legacy.utils.network.Capes;
import mathax.client.legacy.utils.player.ChatUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class MatHaxLegacyCommand extends Command {
    public MatHaxLegacyCommand() {
        super("mathaxlegacy", "Some MatHax Legacy commands.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            ChatUtils.info("You are using MatHax Legacy " + MatHaxClientLegacy.clientVersionWithV + "!");
            return SINGLE_SUCCESS;
        });

        builder.then(literal("version").executes(ctx -> {
            ChatUtils.info("You are using MatHax Legacy " + MatHaxClientLegacy.clientVersionWithV + "!");
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("cape").executes(ctx -> {
            if (Capes.OWNERS.containsKey(mc.player.getUuid())) {
                ChatUtils.info("You own a MatHax Cape! :)");
            } else {
                ChatUtils.info("You dont own a MatHax Cape! :(");
            }
            return SINGLE_SUCCESS;
        })
            .then(argument("reload", StringArgumentType.word())
                .executes(context -> {
                    ChatUtils.info("Reloading capes...");
                    Capes.init();

                    ChatUtils.info("Capes reload complete!");
                    return SINGLE_SUCCESS;
                })
            )
        );

        builder.then(literal("reload").executes(context -> {
                ChatUtils.info("Reloading...");
                Systems.load();
                Capes.init();
                ChatUtils.info("Reload complete!");
                return SINGLE_SUCCESS;
                })

                .then(argument("capes", StringArgumentType.word())
                    .executes(context -> {
                        ChatUtils.info("Reloading capes...");
                        Capes.init();

                        ChatUtils.info("Capes reload complete!");
                        return SINGLE_SUCCESS;
                    })
                )
        );
    }
}
