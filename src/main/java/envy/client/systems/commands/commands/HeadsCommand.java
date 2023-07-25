package envy.client.systems.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import envy.client.gui.GuiThemes;
import envy.client.gui.screens.HeadScreen;
import envy.client.systems.commands.Command;
import envy.client.utils.Utils;
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
