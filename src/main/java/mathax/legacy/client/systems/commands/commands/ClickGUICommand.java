package mathax.legacy.client.systems.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import mathax.legacy.client.gui.GuiThemes;
import mathax.legacy.client.gui.tabs.Tabs;
import mathax.legacy.client.systems.commands.Command;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class ClickGUICommand extends Command {

    public ClickGUICommand() {
        super("click-gui", "Opens the Click GUI");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            Tabs.get().get(0).openScreen(GuiThemes.get());
            return SINGLE_SUCCESS;
        });
    }
}
