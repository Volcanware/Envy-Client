package envy.client.gui.widgets.music;

import envy.client.Envy;
import envy.client.gui.GuiTheme;
import envy.client.gui.screens.music.PlaylistViewScreen;
import envy.client.gui.tabs.builtin.MusicTab;
import envy.client.gui.widgets.containers.WTable;
import envy.client.gui.widgets.input.WTextBox;
import envy.client.utils.music.SearchUtils;

public class WSearchBar extends WMusicWidget {
    @Override
    public void add(WTable parent, MusicTab.MusicScreen screen, GuiTheme theme) {
        WTextBox box = parent.add(theme.textBox("")).expandX().widget();
        parent.add(theme.button("Search")).widget().action = () -> SearchUtils.search(box.get(), playlist -> Envy.mc.setScreen(new PlaylistViewScreen(theme, playlist, screen)));
        super.add(parent, screen, theme);
    }
}
