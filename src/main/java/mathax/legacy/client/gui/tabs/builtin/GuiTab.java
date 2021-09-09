package mathax.legacy.client.gui.tabs.builtin;

import mathax.legacy.client.gui.GuiTheme;
import mathax.legacy.client.gui.GuiThemes;
import mathax.legacy.client.gui.tabs.Tab;
import mathax.legacy.client.gui.tabs.TabScreen;
import mathax.legacy.client.gui.tabs.WindowTabScreen;
import mathax.legacy.client.gui.widgets.containers.WTable;
import mathax.legacy.client.gui.widgets.input.WDropdown;
import mathax.legacy.client.utils.Utils;
import net.minecraft.client.gui.screen.Screen;

public class GuiTab extends Tab {
    public GuiTab() {
        super("GUI");
    }

    @Override
    public TabScreen createScreen(GuiTheme theme) {
        return new GuiScreen(theme, this);
    }

    @Override
    public boolean isScreen(Screen screen) {
        return screen instanceof GuiScreen;
    }

    public static class GuiScreen extends WindowTabScreen {
        public GuiScreen(GuiTheme theme, Tab tab) {
            super(theme, tab);

            WTable table = add(theme.table()).expandX().widget();

            table.add(theme.label("Theme:"));
            WDropdown<String> themeW = table.add(theme.dropdown(GuiThemes.getNames(), GuiThemes.get().name)).widget();
            themeW.action = () -> {
                GuiThemes.select(themeW.get());

                Utils.mc.setScreen(null);
                tab.openScreen(GuiThemes.get());
            };

            theme.settings.onActivated();
            add(theme.settings(theme.settings)).expandX();
        }
    }
}
