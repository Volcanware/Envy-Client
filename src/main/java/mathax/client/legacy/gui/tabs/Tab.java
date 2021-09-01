package mathax.client.legacy.gui.tabs;

import mathax.client.legacy.gui.GuiTheme;
import net.minecraft.client.gui.screen.Screen;

import static mathax.client.legacy.utils.Utils.mc;

public abstract class Tab {
    public final String name;

    public Tab(String name) {
        this.name = name;
    }

    public void OpenScreen(GuiTheme theme) {
        TabScreen screen = this.createScreen(theme);
        screen.addDirect(theme.topBar()).top().centerX();
        mc.setScreen(screen);
    }

    protected abstract TabScreen createScreen(GuiTheme theme);

    public abstract boolean isScreen(Screen screen);
}
