package mathax.legacy.client.gui.screens.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import mathax.legacy.client.gui.GuiTheme;
import mathax.legacy.client.gui.tabs.Tab;
import mathax.legacy.client.gui.tabs.WindowTabScreen;
import mathax.legacy.client.gui.widgets.containers.WTable;
import mathax.legacy.client.gui.widgets.music.WCurrentTracksView;
import mathax.legacy.client.gui.widgets.music.WMusicWidget;
import mathax.legacy.client.gui.widgets.music.WSearchBar;

import java.util.ArrayList;
import java.util.List;

public class MusicScreen extends WindowTabScreen {
    public MusicScreen(GuiTheme theme, Tab tab) {
        super(theme, tab);
    }

    private WTable table;
    public static final int pageSize = 10;
    private List<WMusicWidget> childWidgets;

    @Override
    public void initWidgets() {
        super.init();
        childWidgets = new ArrayList<>();
        childWidgets.add(new WSearchBar());
        childWidgets.add(new WCurrentTracksView(this));
        if (table != null) table.clear();
        clear();
        table = add(theme.table()).expandX().minWidth(300).widget();
        construct();
    }

    public void construct() {
        table.clear();
        for (WMusicWidget wMusicWidget : childWidgets) {
            wMusicWidget.add(table, this, theme);
        }
    }

    public String getName(AudioTrack track) {
        if (track == null) return "Not playing";
        return track.getInfo().title + " (" + track.getInfo().author + ")";
    }
}
