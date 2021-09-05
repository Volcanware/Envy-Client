package mathax.client.legacy.systems.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import mathax.client.legacy.systems.Systems;
import mathax.client.legacy.systems.commands.Command;
import mathax.client.legacy.utils.network.Capes;
import mathax.client.legacy.utils.player.ChatUtils;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class ReloadCommand extends Command {
    public ReloadCommand() {
        super("reload", "Reloads the config, modules, friends, enemies, macros, accounts and capes.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            ChatUtils.info("Reloading...");
            Systems.load();
            Capes.init();

            ChatUtils.info("Reload complete!");
            return SINGLE_SUCCESS;
        });

        builder.then(literal("capes").executes(ctx -> {
            ChatUtils.info("Reloading capes...");
            Capes.init();

            ChatUtils.info("CapesModule reload complete!");
            return SINGLE_SUCCESS;
        }));
    }
}
