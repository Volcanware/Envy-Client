package envy.client.gui.widgets.music;

import envy.client.gui.GuiTheme;
import envy.client.gui.tabs.builtin.MusicTab;
import envy.client.gui.widgets.containers.WTable;

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
