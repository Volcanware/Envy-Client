package mathax.legacy.client.gui.utils;

import mathax.legacy.client.gui.GuiTheme;
import mathax.legacy.client.gui.widgets.WWidget;
import mathax.legacy.client.settings.Settings;

public interface SettingsWidgetFactory {
    WWidget create(GuiTheme theme, Settings settings, String filter);
}
