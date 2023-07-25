package envy.client.gui.tabs.builtin;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import envy.client.gui.GuiTheme;
import envy.client.gui.tabs.Tab;
import envy.client.gui.tabs.TabScreen;
import envy.client.gui.tabs.WindowTabScreen;
import envy.client.gui.widgets.containers.WTable;
import envy.client.gui.widgets.music.WCurrentTracksView;
import envy.client.gui.widgets.music.WMusicWidget;
import envy.client.gui.widgets.music.WSearchBar;
import net.minecraft.client.gui.screen.Screen;

import java.util.ArrayList;
import java.util.List;

public class MusicTab extends Tab {
    public MusicTab() {
        super("Music");
    }

    @Override
    public TabScreen createScreen(GuiTheme theme) {
        return new MusicScreen(theme, this);
    }

    @Override
    public boolean isScreen(Screen screen) {
        return screen instanceof MusicScreen;
    }

    public static class MusicScreen extends WindowTabScreen {
        private List<WMusicWidget> childWidgets;

        public static final int pageSize = 10;

        private WTable table;

        public MusicScreen(GuiTheme theme, Tab tab) {
            super(theme, tab);
        }

        @Override
        public void initWidgets() {
            childWidgets = new ArrayList<>();
            childWidgets.add(new WSearchBar());
            childWidgets.add(new WCurrentTracksView(this));
            if (this.table != null) this.table.clear();
            clear();
            this.table = add(theme.table()).expandX().minWidth(300).widget();
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
}
