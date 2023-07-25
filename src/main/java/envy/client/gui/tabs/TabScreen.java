package envy.client.gui.tabs;

import envy.client.gui.GuiTheme;
import envy.client.gui.WidgetScreen;
import envy.client.gui.utils.Cell;
import envy.client.gui.widgets.WWidget;

public abstract class TabScreen extends WidgetScreen {
    public final Tab tab;

    public TabScreen(GuiTheme theme, Tab tab) {
        super(theme, tab.name);
        this.tab = tab;
    }

    public <T extends WWidget> Cell<T> addDirect(T widget) {
        return super.add(widget);
    }
}
