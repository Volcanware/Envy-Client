package envy.client.systems.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import envy.client.systems.Systems;
import envy.client.systems.commands.Command;
import envy.client.utils.network.Capes;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class ReloadCommand extends Command {
    public ReloadCommand() {
        super("reload", "Reloads the config, modules, friends, enemies, macros, accounts and capes.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            Systems.load();
            Capes.init();
            info("Reloaded.");
            return SINGLE_SUCCESS;
        });

        builder.then(literal("capes").executes(ctx -> {
            Capes.init();
            info("Reloaded capes.");
            return SINGLE_SUCCESS;
        }));
    }
}
