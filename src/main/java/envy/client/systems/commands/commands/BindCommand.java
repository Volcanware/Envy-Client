package envy.client.systems.commands.commands;


import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import envy.client.systems.commands.Command;
import envy.client.systems.commands.arguments.ModuleArgumentType;
import envy.client.systems.modules.Module;
import envy.client.systems.modules.Modules;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class BindCommand extends Command {
    public BindCommand() {
        super("bind", "Binds a specified module to the next pressed key.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("module", ModuleArgumentType.module()).executes(context -> {
            Module module = context.getArgument("module", Module.class);
            Modules.get().setModuleToBind(module);

            module.info("Press a key to bind the module to.");
            return SINGLE_SUCCESS;
        }));
    }
}
