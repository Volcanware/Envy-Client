package mathax.legacy.client.systems.commands.commands;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import mathax.legacy.client.music.Music;
import mathax.legacy.client.music.TrackScheduler;
import mathax.legacy.client.systems.commands.Command;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class MusicCommand extends Command {

    public MusicCommand() {
        super("music", "Music commands.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            info("Currently playing (highlight)%s(default).", Music.player.getPlayingTrack());
            return SINGLE_SUCCESS;
        });

        builder.then(literal("set-volume")
            .then(argument("new-volume", FloatArgumentType.floatArg())
                .executes(context -> {
                    Music.trackScheduler.setVolume(context.getArgument("new-volume", Float.class));
                    info("Set music volume to (highlight)%s(default).", context.getArgument("new-volume", Float.class));
                    return SINGLE_SUCCESS;
                })
            )
        );

        builder.then(literal("pause")
            .executes(context -> {
                if (Music.player.isPaused()) {
                    info("Already paused.");
                    return SINGLE_SUCCESS;
                }

                Music.trackScheduler.setPaused(true);
                info("Paused.");
                return SINGLE_SUCCESS;
            })
        );

        builder.then(literal("continue")
            .executes(context -> {
                if (Music.player.isPaused()) {
                    info("Not paused.");
                    return SINGLE_SUCCESS;
                }

                Music.trackScheduler.setPaused(false);
                info("Continued playing.");
                return SINGLE_SUCCESS;
            })
        );
    }
}
