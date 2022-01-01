package mathax.client.gui.widgets.music;

import mathax.client.gui.widgets.containers.WTable;
import mathax.client.gui.GuiTheme;
import mathax.client.gui.tabs.builtin.MusicTab;

import java.util.ArrayList;
import java.util.List;

public abstract class WMusicWidget {
    protected List<WMusicWidget> childWidgets = new ArrayList<>();
    public void add(WTable parent, MusicTab.MusicScreen screen, GuiTheme theme) {
        for (WMusicWidget child : childWidgets) {
            child.add(parent, screen, theme);
        }
    }
}
