package mathax.legacy.client.gui.widgets.music;

import mathax.legacy.client.gui.GuiTheme;
import mathax.legacy.client.gui.screens.music.MusicScreen;
import mathax.legacy.client.gui.widgets.containers.WTable;

import java.util.ArrayList;
import java.util.List;

public abstract class WMusicWidget {
    protected List<WMusicWidget> childWidgets = new ArrayList<>();
    public void add(WTable parent, MusicScreen screen, GuiTheme theme) {
        for (WMusicWidget child : childWidgets) {
            child.add(parent, screen, theme);
        }
    }
}
