package mathax.client.legacy.gui;

import mathax.client.legacy.gui.utils.Cell;
import mathax.client.legacy.gui.widgets.WWidget;
import mathax.client.legacy.gui.widgets.containers.WWindow;

public abstract class WindowScreen extends WidgetScreen {
    protected final WWindow window;
    private boolean firstInit = true;

    public WindowScreen(GuiTheme theme, String title) {
        super(theme, title);

        window = super.add(theme.window(title)).center().widget();
        window.view.scrollOnlyWhenMouseOver = false;
    }

    @Override
    protected void init() {
        super.init();

        if (firstInit) {
            firstInit = false;
            initWidgets();
        }
    }

    public abstract void initWidgets();

    public void reload() {
        clear();
        initWidgets();
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
