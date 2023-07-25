package envy.client.gui.screens.music;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import envy.client.Envy;
import envy.client.gui.GuiTheme;
import envy.client.gui.WindowScreen;
import envy.client.gui.tabs.builtin.MusicTab;
import envy.client.gui.widgets.containers.WTable;
import envy.client.gui.widgets.input.WTextBox;
import envy.client.gui.widgets.music.WMusicWidget;
import envy.client.gui.widgets.music.WPaginationProvider;
import envy.client.utils.music.PlaylistUtils;
import envy.client.utils.music.SearchUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlaylistsScreen extends WindowScreen {
    private final MusicTab.MusicScreen musicScreen;
    private List<WMusicWidget> childWidgets;
    private WTable table;
    private WPaginationProvider pagination;

    public PlaylistsScreen(GuiTheme theme, MusicTab.MusicScreen musicScreen) {
        super(theme, "Music - Playlists");
        this.musicScreen = musicScreen;
        this.parent = musicScreen;
    }

    @Override
    public void initWidgets() {
        childWidgets = new ArrayList<>();

        pagination = new WPaginationProvider(j -> construct());
        childWidgets.add(pagination);

        if (table != null) table.clear();
        clear();
        table = add(theme.table()).expandX().minWidth(300).widget();
        construct();
    }

    public void construct() {
        pagination.setMaxPage(PlaylistUtils.count() / MusicTab.MusicScreen.pageSize);
        table.clear();

        WTextBox box = table.add(theme.textBox("")).expandX().widget();
        table.add(theme.plus()).widget().action = () -> SearchUtils.search(box.get(), playlist -> {
                PlaylistUtils.add(box.get());
                construct();
            });

        table.row();
        table.add(theme.button("Reset")).expandX().widget().action = () -> {
            PlaylistUtils.reset();
            construct();
        };

        List<Map.Entry<String, AudioPlaylist>> keys = PlaylistUtils.getEntriesOrdered();
        for (int i = 0; i < keys.size(); i++) {
            int j = i + pagination.getPageOffset();
            table.row();
            AudioPlaylist playlist = keys.get(j).getValue();

            table.add(theme.button(playlist.getName())).expandX().widget().action = () -> Envy.mc.setScreen(new PlaylistViewScreen(theme, playlist, musicScreen).setParent(this));

            table.add(theme.minus()).right().widget().action = () -> {
                PlaylistUtils.remove(keys.get(j).getKey());
                construct();
            };
        }

        for (WMusicWidget wMusicWidget : childWidgets) {
            wMusicWidget.add(table, musicScreen, theme);
        }
    }
}
