package mathax.client.legacy.systems.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import mathax.client.legacy.systems.commands.Command;
import mathax.client.legacy.utils.network.Capes;
import mathax.client.legacy.utils.player.ChatUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Formatting;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class CapesCommand extends Command {
    public CapesCommand() {
        super("capes", "MatHax Capes.", "cape");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            if (Capes.OWNERS.containsKey(mc.player.getUuid())) {
                ChatUtils.info("Capes", "You own a \" + Formatting.RED + \"MatHax\" + Formatting.GRAY + \" Cape! :)");
            } else {
                ChatUtils.info("Capes", "You dont own a " + Formatting.RED + "MatHax" + Formatting.GRAY + " Cape! :(");
            }
            return SINGLE_SUCCESS;
        });

        builder.then(literal("reload").executes(context -> {
            info("Reloading capes...");
            Capes.init();

            info("Capes reload complete!");
            return SINGLE_SUCCESS;
        }));
    }
}
