package mathax.client.utils.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import mathax.client.music.Music;
import mathax.client.utils.misc.ChatUtils;

import java.util.function.Consumer;

public class SearchUtils {
    public static void search(String url, Consumer<AudioPlaylist> success) {
        if (url == null) ChatUtils.error("Music", "Could not load results.");
        else {
            if (!url.startsWith("http:") && !url.startsWith("https:")) url = "ytmsearch:" + url;
            Music.playerManager.loadItem(url, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                    Music.trackScheduler.queue(track);
                }

                @Override
                public void playlistLoaded(AudioPlaylist playlist) {
                    if (playlist.getTracks().isEmpty()) noMatches();
                    else success.accept(playlist);
                }

                @Override
                public void noMatches() {
                    ChatUtils.error("Music", "No tracks could be found.");
                }

                @Override
                public void loadFailed(FriendlyException exception) {}
            });
        }
    }
}
