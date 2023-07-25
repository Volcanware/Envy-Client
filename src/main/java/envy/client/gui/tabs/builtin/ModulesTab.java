package envy.client.gui.tabs.builtin;

import envy.client.gui.GuiTheme;
import envy.client.gui.GuiThemes;
import envy.client.gui.tabs.Tab;
import envy.client.gui.tabs.TabScreen;
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
