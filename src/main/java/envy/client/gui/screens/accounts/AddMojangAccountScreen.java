package envy.client.gui.screens.accounts;

import envy.client.gui.GuiTheme;
import envy.client.gui.widgets.containers.WTable;
import envy.client.gui.widgets.input.WTextBox;
import envy.client.systems.accounts.Accounts;
import envy.client.systems.accounts.types.MojangAccount;

public class AddMojangAccountScreen extends AddAccountScreen {
    public AddMojangAccountScreen(GuiTheme theme, AccountsScreen parent) {
        super(theme, "Add Mojang Account", parent);
    }

    @Override
    public void initWidgets() {
        WTable t = add(theme.table()).widget();

        // Email
        t.add(theme.label("Email: "));
        WTextBox email = t.add(theme.textBox("")).minWidth(400).expandX().widget();
        email.setFocused(true);
        t.row();

        // Password
        t.add(theme.label("Password: "));
        WTextBox password = t.add(theme.textBox("")).minWidth(400).expandX().widget();
        t.row();

        // Add
        add = t.add(theme.button("Add")).expandX().widget();
        add.action = () -> {
            MojangAccount account = new MojangAccount(email.get(), password.get());
            if (!email.get().isEmpty() && !password.get().isEmpty() && email.get().contains("@") && !Accounts.get().exists(account)) AccountsScreen.addAccount(this, parent, account);
        };

        enterAction = add.action;
    }
}
