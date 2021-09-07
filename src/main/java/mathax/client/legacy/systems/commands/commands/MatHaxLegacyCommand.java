package mathax.client.legacy.systems.commands.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import mathax.client.legacy.Version;
import mathax.client.legacy.systems.Systems;
import mathax.client.legacy.systems.commands.Command;
import mathax.client.legacy.utils.network.Capes;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Formatting;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class MatHaxLegacyCommand extends Command {

    public MatHaxLegacyCommand() {
        super("mathaxlegacy", "Some MatHax Legacy commands.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            info("You are using " + Formatting.RED + "MatHax Legacy" + Formatting.GRAY + " " + Formatting.WHITE + Version.getStylized() + Formatting.GRAY+ "!");
            return SINGLE_SUCCESS;
        });

        builder.then(literal("version").executes(ctx -> {
            info("You are using " + Formatting.RED + "MatHax Legacy" + Formatting.GRAY + " " + Formatting.WHITE + Version.getStylized() + Formatting.GRAY+ "!");
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("cape").executes(ctx -> {
            if (Capes.OWNERS.containsKey(mc.player.getUuid())) {
                info("You own a " + Formatting.RED + "MatHax" + Formatting.GRAY + " Cape! :)");
            } else {
                info("You dont own a " + Formatting.RED + "MatHax" + Formatting.GRAY + " Cape! :(");
            }
            return SINGLE_SUCCESS;
        })
            .then(argument("reload", StringArgumentType.word())
                .executes(context -> {
                    info("Reloading capes...");
                    Capes.init();

                    info("Capes reload complete!");
                    return SINGLE_SUCCESS;
                })
            )
        );

        builder.then(literal("reload").executes(context -> {
                info("Reloading...");
                Systems.load();
                Capes.init();
                info("Reload complete!");
                return SINGLE_SUCCESS;
                })

                .then(argument("capes", StringArgumentType.word())
                    .executes(context -> {
                        info("Reloading capes...");
                        Capes.init();

                        info("Capes reload complete!");
                        return SINGLE_SUCCESS;
                    })
                )
        );
    }
}
