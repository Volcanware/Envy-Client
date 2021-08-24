package mathax.client.legacy.gui.tabs.builtin;

import mathax.client.legacy.gui.GuiTheme;
import mathax.client.legacy.gui.GuiThemes;
import mathax.client.legacy.gui.tabs.Tab;
import mathax.client.legacy.gui.tabs.TabScreen;
import net.minecraft.client.gui.screen.Screen;

public class ModulesTab extends Tab {
    public ModulesTab() {
        super("Modules");
    }

    @Override
    public TabScreen createScreen(GuiTheme theme) {
        return theme.modulesScreen();
    }

    @Override
    public boolean isScreen(Screen screen) {
        return GuiThemes.get().isModulesScreen(screen);
    }
}
