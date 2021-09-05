package mathax.client.legacy.gui.screens;

import mathax.client.legacy.gui.GuiTheme;
import mathax.client.legacy.gui.WindowScreen;
import mathax.client.legacy.gui.widgets.pressable.WButton;

public abstract class AddAccountScreen extends WindowScreen {
    public final AccountsScreen parent;
    public WButton add;

    protected AddAccountScreen(GuiTheme theme, String title, AccountsScreen parent) {
        super(theme, title);
        this.parent = parent;
    }
}
