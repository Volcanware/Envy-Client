package mathax.client.gui.widgets.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import mathax.client.gui.widgets.containers.WTable;
import mathax.client.gui.GuiTheme;
import mathax.client.gui.tabs.builtin.MusicTab;
import mathax.client.music.Music;

public class WCurrentTracksView extends WMusicWidget {
    private AudioTrack currentTrack;
    private final WPaginationProvider pagination;

    @Override
    public void add(WTable parent, MusicTab.MusicScreen screen, GuiTheme theme) {
        currentTrack = Music.player.getPlayingTrack();
        parent.row();
        WTable currentTracks = parent.add(theme.section("Current tracks")).expandX().widget().add(theme.table()).expandX().widget();
        super.add(currentTracks, screen, theme);
    }

    public WCurrentTracksView(MusicTab.MusicScreen screen) {
        childWidgets.add(new WPlaybackControls());
        childWidgets.add(new WCurrentTrackView());
        pagination = new WPaginationProvider(i -> screen.construct());
        childWidgets.add(new WPlaylistPage(pagination, () -> Music.trackScheduler.tracks, i -> {
            Music.trackScheduler.tracks.subList(0, i).clear();
            Music.trackScheduler.playNext(Music.player);
        }, i -> {
            Music.trackScheduler.tracks.remove(i.intValue());
            Music.trackScheduler.refreshUI();
        }));
        childWidgets.add(pagination);
    }

    public class WCurrentTrackView extends WMusicWidget {
        @Override
        public void add(WTable parent, MusicTab.MusicScreen screen, GuiTheme theme) {
            if (currentTrack != null && pagination.getPageOffset() == 0) {
                parent.add(theme.label("Current: " + screen.getName(currentTrack).replace("&amp;", "&"))).expandX();
                parent.add(theme.minus()).widget().action = () -> {
                    Music.trackScheduler.playNext(Music.player);
                };
                if (Music.trackScheduler.hasNext()) parent.row();
            }
            super.add(parent, screen, theme);
        }
    }
}
