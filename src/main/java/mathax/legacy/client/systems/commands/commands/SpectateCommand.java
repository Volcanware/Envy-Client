package mathax.legacy.client.systems.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import mathax.legacy.client.MatHaxLegacy;
import mathax.legacy.client.events.mathax.KeyEvent;
import mathax.legacy.client.systems.commands.arguments.PlayerArgumentType;
import mathax.legacy.client.systems.commands.Command;
import mathax.legacy.client.bus.EventHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.text.LiteralText;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class SpectateCommand extends Command {

    private final StaticListener shiftListener = new StaticListener();

    public SpectateCommand() {
        super("spectate", "Allows you to spectate nearby players");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("reset").executes(context -> {
            mc.setCameraEntity(mc.player);
            return SINGLE_SUCCESS;
        }));

        builder.then(argument("player", PlayerArgumentType.player()).executes(context -> {
            mc.setCameraEntity(PlayerArgumentType.getPlayer(context));
            mc.player.sendMessage(new LiteralText("Sneak to un-spectate."), true);
            MatHaxLegacy.EVENT_BUS.subscribe(shiftListener);
            return SINGLE_SUCCESS;
        }));
    }

    private class StaticListener {
        @EventHandler
        private void onKey(KeyEvent event) {
            if (mc.options.keySneak.matchesKey(event.key, 0) || mc.options.keySneak.matchesMouse(event.key)) {
                mc.setCameraEntity(mc.player);
                event.cancel();
                MatHaxLegacy.EVENT_BUS.unsubscribe(this);
            }
        }
    }
}
