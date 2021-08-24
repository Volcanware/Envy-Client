package mathax.client.legacy.gui.tabs;

import mathax.client.legacy.gui.GuiTheme;
import mathax.client.legacy.gui.WidgetScreen;
import mathax.client.legacy.gui.utils.Cell;
import mathax.client.legacy.gui.widgets.WWidget;

public class TabScreen extends WidgetScreen {
    public final Tab tab;

    public TabScreen(GuiTheme theme, Tab tab) {
        super(theme, tab.name);
        this.tab = tab;
    }

    public <T extends WWidget> Cell<T> addDirect(T widget) {
        return super.add(widget);
    }
}
