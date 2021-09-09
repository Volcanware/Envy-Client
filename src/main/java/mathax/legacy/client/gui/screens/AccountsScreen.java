package mathax.legacy.client.gui.screens;

import mathax.legacy.client.gui.GuiTheme;
import mathax.legacy.client.gui.WindowScreen;
import mathax.legacy.client.gui.widgets.WAccount;
import mathax.legacy.client.gui.widgets.containers.WContainer;
import mathax.legacy.client.gui.widgets.containers.WHorizontalList;
import mathax.legacy.client.gui.widgets.pressable.WButton;
import mathax.legacy.client.systems.accounts.Account;
import mathax.legacy.client.systems.accounts.Accounts;
import mathax.legacy.client.systems.accounts.MicrosoftLogin;
import mathax.legacy.client.systems.accounts.types.MicrosoftAccount;
import mathax.legacy.client.utils.network.MatHaxExecutor;
import mathax.legacy.client.utils.Utils;
import org.jetbrains.annotations.Nullable;

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

        addButton(l, "Cracked", () -> Utils.mc.setScreen(new AddCrackedAccountScreen(theme, this)));
        addButton(l, "Premium", () -> Utils.mc.setScreen(new AddPremiumAccountScreen(theme, this)));
        addButton(l, "Microsoft", () -> {
            locked = true;

            MicrosoftLogin.getRefreshToken(refreshToken -> {
                locked = false;

                if (refreshToken != null) {
                    MicrosoftAccount account = new MicrosoftAccount(refreshToken);
                    addAccount(null, account);
                }
            });
        });
        addButton(l, "The Altening", () -> Utils.mc.setScreen(new AddAlteningAccountScreen(theme, this)));
    }

    private void addButton(WContainer c, String text, Runnable action) {
        WButton button = c.add(theme.button(text)).expandX().widget();
        button.action = action;
    }

    public static void addAccount(@Nullable AddAccountScreen screen, Account<?> account) {
        if (screen != null) {
            screen.add.set("...");
            screen.locked = true;
        }

        MatHaxExecutor.execute(() -> {
            if (account.fetchInfo() && account.fetchHead()) {
                Accounts.get().add(account);
                if (account.login()) Accounts.get().save();

                if (screen != null) {
                    screen.locked = false;
                    screen.onClose();
                    screen.parent.reload();
                }

                return;
            }

            if (screen != null) {
                screen.add.set("Add");
                screen.locked = false;
            }
        });
    }
}
