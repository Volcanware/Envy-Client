package mathax.client.legacy.gui.utils;

import mathax.client.legacy.gui.GuiTheme;
import mathax.client.legacy.gui.WidgetScreen;

public interface IScreenFactory {
    WidgetScreen createScreen(GuiTheme theme);
}
