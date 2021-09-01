package mathax.client.legacy.gui.screens;

import mathax.client.legacy.gui.GuiTheme;
import mathax.client.legacy.gui.WindowScreen;
import mathax.client.legacy.gui.widgets.containers.WTable;
import mathax.client.legacy.gui.widgets.input.WTextBox;
import mathax.client.legacy.gui.widgets.pressable.WButton;
import mathax.client.legacy.systems.accounts.types.TheAlteningAccount;

public class AddAlteningAccountScreen extends WindowScreen {
    public AddAlteningAccountScreen(GuiTheme theme) {
        super(theme, "Add The Altening Account");
    }

    @Override
    public void initWidgets() {
        WTable t = add(theme.table()).widget();

        // Token
        t.add(theme.label("Token: "));
        WTextBox token = t.add(theme.textBox("")).minWidth(400).expandX().widget();
        token.setFocused(true);
        t.row();

        // Add
        WButton add = t.add(theme.button("Add")).expandX().widget();
        add.action = () -> {
            if (!token.get().isEmpty()) {
                AccountsScreen.addAccount(add, this, new TheAlteningAccount(token.get()));
            }
        };

        enterAction = add.action;
    }
}
