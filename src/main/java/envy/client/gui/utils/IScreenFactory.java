package envy.client.gui.utils;

import envy.client.gui.GuiTheme;
import envy.client.gui.WidgetScreen;

public interface IScreenFactory {
    WidgetScreen createScreen(GuiTheme theme);
}
