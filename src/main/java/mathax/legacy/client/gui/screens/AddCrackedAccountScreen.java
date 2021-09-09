package mathax.legacy.client.gui.screens;

import mathax.legacy.client.gui.GuiTheme;
import mathax.legacy.client.gui.widgets.containers.WTable;
import mathax.legacy.client.gui.widgets.input.WTextBox;
import mathax.legacy.client.systems.accounts.Accounts;
import mathax.legacy.client.systems.accounts.types.CrackedAccount;

public class AddCrackedAccountScreen extends AddAccountScreen {
    public AddCrackedAccountScreen(GuiTheme theme, AccountsScreen parent) {
        super(theme, "Add Cracked Account", parent);
    }

    @Override
    public void initWidgets() {
        WTable t = add(theme.table()).widget();

        // Name
        t.add(theme.label("Name: "));
        WTextBox name = t.add(theme.textBox("", (text, c) ->
            // Username can't contain spaces
            c != ' '
        )).minWidth(400).expandX().widget();
        name.setFocused(true);
        t.row();

        // Add
        add = t.add(theme.button("Add")).expandX().widget();
        add.action = () -> {
            if (!name.get().isEmpty()) {
                CrackedAccount account = new CrackedAccount(name.get());
                if (!(Accounts.get().exists(account))) {
                    AccountsScreen.addAccount(this, account);
                }
            }
        };

        enterAction = add.action;
    }
}
