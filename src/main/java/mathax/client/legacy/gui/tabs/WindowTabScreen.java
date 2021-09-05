package mathax.client.legacy.gui.tabs;

import mathax.client.legacy.gui.GuiTheme;
import mathax.client.legacy.gui.utils.Cell;
import mathax.client.legacy.gui.widgets.WWidget;
import mathax.client.legacy.gui.widgets.containers.WWindow;

public class WindowTabScreen extends TabScreen {
    protected final WWindow window;

    public WindowTabScreen(GuiTheme theme, Tab tab) {
        super(theme, tab);

        window = super.add(theme.window(tab.name)).center().widget();
    }

    @Override
    public <W extends WWidget> Cell<W> add(W widget) {
        return window.add(widget);
    }

    @Override
    public void clear() {
        window.clear();
    }
}
