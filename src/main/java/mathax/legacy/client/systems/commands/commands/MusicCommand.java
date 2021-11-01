package mathax.legacy.client.systems.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import mathax.legacy.client.music.Music;
import mathax.legacy.client.systems.commands.Command;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

// TODO: Add volume set & info, play command which searches for a song and plays it and stop command which removes all songs.

public class MusicCommand extends Command {

    public MusicCommand() {
        super("music", "Music commands.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            if (Music.player.getPlayingTrack() == null) {
                info("Not playing any song.");
                return SINGLE_SUCCESS;
            }

            // TODO: Add hover text "View on YouTube Music" and add click link to the song.
            info("Currently playing (highlight)%s(default).", Music.player.getPlayingTrack().getInfo().title + " - " + Music.player.getPlayingTrack().getInfo().author);
            return SINGLE_SUCCESS;
        });

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
                if (!Music.player.isPaused()) {
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
