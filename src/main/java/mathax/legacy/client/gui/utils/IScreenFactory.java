package mathax.legacy.client.gui.utils;

import mathax.legacy.client.gui.GuiTheme;
import mathax.legacy.client.gui.WidgetScreen;

public interface IScreenFactory {
    WidgetScreen createScreen(GuiTheme theme);
}
