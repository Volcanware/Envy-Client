package mathax.client.gui.widgets.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import mathax.client.gui.widgets.containers.WHorizontalList;
import mathax.client.gui.widgets.containers.WTable;
import mathax.client.gui.widgets.pressable.WButton;
import mathax.client.gui.GuiTheme;
import mathax.client.gui.screens.music.PlaylistsScreen;
import mathax.client.gui.tabs.builtin.MusicTab;
import mathax.client.music.Music;

import java.util.Collections;

import static mathax.client.MatHax.mc;

public class WPlaybackControls extends WMusicWidget {
    @Override
    public void add(WTable parent, MusicTab.MusicScreen screen, GuiTheme theme) {
        WHorizontalList list = parent.add(theme.horizontalList()).widget();
        WButton pauseButton = list.add(theme.button(Music.player.isPaused() ? "Resume" : "Pause")).widget();
        pauseButton.action = () -> {
            if (Music.player.isPaused()) {
                Music.trackScheduler.setPaused(false);
                pauseButton.set("Pause");
            } else {
                Music.trackScheduler.setPaused(true);
                pauseButton.set("Resume");
            }
        };

        if (Music.trackScheduler.hasNext()) {
            list.add(theme.button("Shuffle")).widget().action = () -> {
                Collections.shuffle(Music.trackScheduler.tracks);
                screen.construct();
            };
            list.add(theme.button("Clear")).widget().action = () -> {
                Music.trackScheduler.tracks.clear();
                screen.construct();
            };
        }

        list.add(theme.button("Playlists")).widget().action = () -> {
            mc.setScreen(new PlaylistsScreen(theme, screen));
        };

        String duration;
        AudioTrack current = Music.player.getPlayingTrack();
        if (current == null) duration = "Not playing";
        else duration = Music.getTime();

        list.add(theme.label("Duration: " + duration)).right();

        parent.row();
        super.add(parent, screen, theme);
    }
}
