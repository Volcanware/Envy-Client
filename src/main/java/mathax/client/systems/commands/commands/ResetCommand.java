package mathax.client.systems.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import mathax.client.gui.GuiThemes;
import mathax.client.settings.Setting;
import mathax.client.systems.commands.Command;
import mathax.client.systems.commands.arguments.ModuleArgumentType;
import mathax.client.systems.modules.Module;
import mathax.client.systems.modules.Modules;
import mathax.client.systems.modules.render.hud.HUD;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class ResetCommand extends Command {
    public ResetCommand() {
        super("reset", "Resets specified settings.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("settings")
                .then(argument("module", ModuleArgumentType.module()).executes(context -> {
                    Module module = context.getArgument("module", Module.class);
                    module.settings.forEach(group -> group.forEach(Setting::reset));
                    module.info("Reset all settings.");
                    return SINGLE_SUCCESS;
                }))
                .then(literal("all").executes(context -> {
                    Modules.get().getAll().forEach(module -> module.settings.forEach(group -> group.forEach(Setting::reset)));
                    info("Reset all module's settings");
                    return SINGLE_SUCCESS;
                }))
        ).then(literal("gui").executes(context -> {
            GuiThemes.get().clearWindowConfigs();
            info("The Click GUI positioning has been reset.");
            return SINGLE_SUCCESS;
        }).then(literal("scale").executes(context -> {
            GuiThemes.get().resetScale();
            info("The GUI scale has been reset.");
            return SINGLE_SUCCESS;
        }))).then(literal("bind")
                .then(argument("module", ModuleArgumentType.module()).executes(context -> {
                    Module module = context.getArgument("module", Module.class);

                    module.keybind.set(true, -1);
                    module.info("Reset bind.");

                    return SINGLE_SUCCESS;
                }))
                .then(literal("all").executes(context -> {
                    Modules.get().getAll().forEach(module -> module.keybind.set(true, -1));
                    info("Reset all module's binds");
                    return SINGLE_SUCCESS;
                }))
        ).then(literal("hud").executes(context -> {
            Modules.get().get(HUD.class).reset.run();
            Modules.get().get(HUD.class).info("Reset HUD elements.");
            return SINGLE_SUCCESS;
        }));
    }
}
