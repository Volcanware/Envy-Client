package mathax.legacy.client.gui.screens.settings;

import mathax.legacy.client.gui.GuiTheme;
import mathax.legacy.client.gui.widgets.WWidget;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.systems.modules.Modules;

import java.util.List;

public class ModuleListSettingScreen extends LeftRightListSettingScreen<Module> {
    public ModuleListSettingScreen(GuiTheme theme, Setting<List<Module>> setting) {
        super(theme, "Select Modules", setting, setting.get(), Modules.REGISTRY);
    }

    @Override
    protected WWidget getValueWidget(Module value) {
        return theme.label(getValueName(value));
    }

    @Override
    protected String getValueName(Module value) {
        return value.title;
    }
}
