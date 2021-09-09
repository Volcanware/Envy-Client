package mathax.legacy.client.gui.screens;

import mathax.legacy.client.gui.GuiTheme;
import mathax.legacy.client.gui.widgets.containers.WTable;
import mathax.legacy.client.gui.widgets.input.WTextBox;
import mathax.legacy.client.systems.accounts.Accounts;
import mathax.legacy.client.systems.accounts.types.PremiumAccount;

public class AddPremiumAccountScreen extends AddAccountScreen {
    public AddPremiumAccountScreen(GuiTheme theme, AccountsScreen parent) {
        super(theme, "Add Premium Account", parent);
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
            PremiumAccount account = new PremiumAccount(email.get(), password.get());
            if (!email.get().isEmpty() && !password.get().isEmpty() && email.get().contains("@") && !Accounts.get().exists(account)) {
                AccountsScreen.addAccount(this, account);
            }
        };

        enterAction = add.action;
    }
}
