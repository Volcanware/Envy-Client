package mathax.client.legacy.gui.screens;

import mathax.client.legacy.gui.GuiTheme;
import mathax.client.legacy.gui.WidgetScreen;
import mathax.client.legacy.gui.WindowScreen;
import mathax.client.legacy.gui.widgets.WAccount;
import mathax.client.legacy.gui.widgets.containers.WContainer;
import mathax.client.legacy.gui.widgets.containers.WHorizontalList;
import mathax.client.legacy.gui.widgets.pressable.WButton;
import mathax.client.legacy.systems.accounts.Account;
import mathax.client.legacy.systems.accounts.Accounts;
import mathax.client.legacy.systems.accounts.MicrosoftLogin;
import mathax.client.legacy.systems.accounts.types.MicrosoftAccount;
import mathax.client.legacy.utils.network.MatHaxExecutor;

import static mathax.client.legacy.utils.Utils.mc;

public class AccountsScreen extends WindowScreen {
    public AccountsScreen(GuiTheme theme) {
        super(theme, "Accounts");
    }

    @Override
    public void initWidgets() {
        // Accounts
        for (Account<?> account : Accounts.get()) {
            WAccount wAccount = add(theme.account(this, account)).expandX().widget();
            wAccount.refreshScreenAction = this::reload;
        }

        // Add account
        WHorizontalList l = add(theme.horizontalList()).expandX().widget();

        addButton(l, "Cracked", () -> mc.setScreen(new AddCrackedAccountScreen(theme)));
        addButton(l, "Premium", () -> mc.setScreen(new AddPremiumAccountScreen(theme)));
        addButton(l, "Microsoft", () -> {
            locked = true;

            MicrosoftLogin.getRefreshToken(refreshToken -> {
                locked = false;

                if (refreshToken != null) {
                    MicrosoftAccount account = new MicrosoftAccount(refreshToken);
                    addAccount(null, this, account);
                }
            });
        });
        addButton(l, "The Altening", () -> mc.setScreen(new AddAlteningAccountScreen(theme)));
    }

    private void addButton(WContainer c, String text, Runnable action) {
        WButton button = c.add(theme.button(text)).expandX().widget();
        button.action = action;
    }

    public static void addAccount(WButton add, WidgetScreen screen, Account<?> account) {
        if (add != null) add.set("...");
        screen.locked = true;

        MatHaxExecutor.execute(() -> {
            if (account.fetchInfo() && account.fetchHead()) {
                Accounts.get().add(account);
                screen.locked = false;

                if (add != null) screen.onClose();
                else if (screen instanceof AccountsScreen s) {
                    s.reload();
                }
            }

            if (add != null) add.set("Add");
            screen.locked = false;
        });
    }
}