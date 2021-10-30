package mathax.legacy.client.gui.widgets.music;

import mathax.legacy.client.gui.GuiTheme;
import mathax.legacy.client.gui.screens.music.MusicScreen;
import mathax.legacy.client.gui.screens.music.PlaylistViewScreen;
import mathax.legacy.client.gui.widgets.containers.WTable;
import mathax.legacy.client.gui.widgets.input.WTextBox;
import mathax.legacy.client.utils.music.SearchUtils;

import static mathax.legacy.client.MatHaxLegacy.mc;

public class WSearchBar extends WMusicWidget {
    @Override
    public void add(WTable parent, MusicScreen screen, GuiTheme theme) {
        WTextBox box = parent.add(theme.textBox("")).expandX().widget();
        parent.add(theme.button("Search")).widget().action = () -> SearchUtils.search(box.get(), playlist -> mc.setScreen(new PlaylistViewScreen(theme, playlist, screen)));
        super.add(parent, screen, theme);
    }
}
