package mathax.legacy.client.systems.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import mathax.legacy.client.systems.commands.Command;
import net.minecraft.command.CommandSource;

public class BaritoneCommand extends Command {
    public BaritoneCommand() {
        super("baritone", "Executes baritone commands.", "b");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        // TODO: Baritone
        /*builder.then(argument("command", StringArgumentType.greedyString())
            .executes(context -> {
                String command = context.getArgument("command", String.class);
                BaritoneAPI.getProvider().getPrimaryBaritone().getCommandManager().execute(command);
                return SINGLE_SUCCESS;
            })
        );*/
    }
}
