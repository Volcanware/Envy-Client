package mathax.legacy.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import mathax.legacy.client.gui.GuiTheme;
import mathax.legacy.client.gui.GuiThemes;
import mathax.legacy.client.gui.tabs.TabScreen;
import mathax.legacy.client.gui.tabs.Tabs;

public class MatHaxLegacyModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ignored -> {
            GuiTheme theme = GuiThemes.get();
            TabScreen screen = Tabs.get().get(0).createScreen(theme);
            screen.addDirect(theme.topBar()).top().centerX();
            return screen;
        };
    }
}
