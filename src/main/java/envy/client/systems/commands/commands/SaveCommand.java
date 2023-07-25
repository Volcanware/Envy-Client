package envy.client.systems.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import envy.client.systems.Systems;
import envy.client.systems.commands.Command;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class SaveCommand extends Command {
    public SaveCommand() {
        super("save", "Saves all current settings.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            info("Saving settings...");
            Systems.save();
            info("Settings save complete!");
            return SINGLE_SUCCESS;
        });
    }
}
