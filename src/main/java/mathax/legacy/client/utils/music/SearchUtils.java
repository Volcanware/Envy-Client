package mathax.legacy.client.utils.music;

public class SearchUtils {/*
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
    }*/
}
