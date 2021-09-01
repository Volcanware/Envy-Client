package mathax.client.legacy.gui.screens;

import mathax.client.legacy.gui.GuiTheme;
import mathax.client.legacy.gui.WindowScreen;
import mathax.client.legacy.gui.widgets.containers.WTable;
import mathax.client.legacy.gui.widgets.input.WTextBox;
import mathax.client.legacy.gui.widgets.pressable.WButton;
import mathax.client.legacy.systems.accounts.Accounts;
import mathax.client.legacy.systems.accounts.types.PremiumAccount;

public class AddPremiumAccountScreen extends WindowScreen {
    public AddPremiumAccountScreen(GuiTheme theme) {
        super(theme, "Add Premium Account");
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
        WButton add = t.add(theme.button("Add")).expandX().widget();
        add.action = () -> {
            PremiumAccount account = new PremiumAccount(email.get(), password.get());
            if (!email.get().isEmpty() && !password.get().isEmpty() && email.get().contains("@") && !Accounts.get().exists(account)) {
                AccountsScreen.addAccount(add, this, account);
            }
        };

        enterAction = add.action;
    }
}
