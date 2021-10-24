package mathax.legacy.client.gui.tabs;

import mathax.legacy.client.gui.GuiTheme;
import mathax.legacy.client.utils.Utils;
import net.minecraft.client.gui.screen.Screen;

public abstract class Tab {
    public final String name;

    public Tab(String name) {
        this.name = name;
    }

    public void openScreen(GuiTheme theme) {
        TabScreen screen = this.createScreen(theme);
        screen.addDirect(theme.topBar()).top().centerX();
        Utils.mc.setScreen(screen);
    }

    public abstract TabScreen createScreen(GuiTheme theme);

    public abstract boolean isScreen(Screen screen);
}
