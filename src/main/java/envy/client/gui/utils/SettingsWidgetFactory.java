package envy.client.gui.utils;

import envy.client.gui.GuiTheme;
import envy.client.gui.widgets.WWidget;
import envy.client.settings.Settings;

public interface SettingsWidgetFactory {
    WWidget create(GuiTheme theme, Settings settings, String filter);
}
