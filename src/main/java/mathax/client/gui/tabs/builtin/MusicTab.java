package mathax.client.gui.tabs.builtin;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import mathax.client.gui.widgets.containers.WTable;
import mathax.client.gui.widgets.music.WCurrentTracksView;
import mathax.client.gui.widgets.music.WMusicWidget;
import mathax.client.gui.widgets.music.WSearchBar;
import mathax.client.gui.GuiTheme;
import mathax.client.gui.tabs.Tab;
import mathax.client.gui.tabs.TabScreen;
import mathax.client.gui.tabs.WindowTabScreen;
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
