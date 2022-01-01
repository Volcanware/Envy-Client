package mathax.client.gui.widgets.music;

import mathax.client.gui.widgets.containers.WTable;
import mathax.client.gui.GuiTheme;
import mathax.client.gui.screens.music.PlaylistViewScreen;
import mathax.client.gui.tabs.builtin.MusicTab;
import mathax.client.gui.widgets.input.WTextBox;
import mathax.client.utils.music.SearchUtils;

import static mathax.client.MatHax.mc;

public class WSearchBar extends WMusicWidget {
    @Override
    public void add(WTable parent, MusicTab.MusicScreen screen, GuiTheme theme) {
        WTextBox box = parent.add(theme.textBox("")).expandX().widget();
        parent.add(theme.button("Search")).widget().action = () -> SearchUtils.search(box.get(), playlist -> mc.setScreen(new PlaylistViewScreen(theme, playlist, screen)));
        super.add(parent, screen, theme);
    }
}
