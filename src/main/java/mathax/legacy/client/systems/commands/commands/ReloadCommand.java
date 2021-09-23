package mathax.legacy.client.systems.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import mathax.legacy.client.systems.Systems;
import mathax.legacy.client.systems.commands.Command;
import mathax.legacy.client.utils.network.Capes;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class ReloadCommand extends Command {

    public ReloadCommand() {
        super("reload", "Reloads the config, modules, friends, enemies, macros, accounts and capes");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            info("Reloading...");
            Systems.load();
            Capes.init();

            info("Reload complete!");
            return SINGLE_SUCCESS;
        });

        builder.then(literal("capes").executes(ctx -> {
            info("Reloading capes...");
            Capes.init();

            info("Capes reload complete!");
            return SINGLE_SUCCESS;
        }));
    }
}
