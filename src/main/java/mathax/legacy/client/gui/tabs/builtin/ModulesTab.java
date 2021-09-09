package mathax.legacy.client.gui.tabs.builtin;

import mathax.legacy.client.gui.GuiTheme;
import mathax.legacy.client.gui.GuiThemes;
import mathax.legacy.client.gui.tabs.Tab;
import mathax.legacy.client.gui.tabs.TabScreen;
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
