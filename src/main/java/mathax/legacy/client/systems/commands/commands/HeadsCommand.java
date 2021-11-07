package mathax.legacy.client.systems.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import mathax.legacy.client.gui.GuiThemes;
import mathax.legacy.client.gui.screens.HeadScreen;
import mathax.legacy.client.systems.commands.Command;
import mathax.legacy.client.utils.Utils;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class HeadsCommand extends Command {
    public HeadsCommand() {
        super("heads", "Displays heads GUI.", "head");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(ctx -> {
            Utils.screenToOpen = new HeadScreen(GuiThemes.get());
            return SINGLE_SUCCESS;
        });
    }
}
