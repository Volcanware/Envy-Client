package envy.client.gui.screens.accounts;

import envy.client.Envy;
import envy.client.gui.GuiTheme;
import envy.client.gui.WindowScreen;
import envy.client.gui.widgets.WAccount;
import envy.client.gui.widgets.containers.WContainer;
import envy.client.gui.widgets.containers.WHorizontalList;
import envy.client.gui.widgets.pressable.WButton;
import envy.client.systems.accounts.Account;
import envy.client.systems.accounts.Accounts;
import envy.client.systems.accounts.MicrosoftLogin;
import envy.client.systems.accounts.types.MicrosoftAccount;
import envy.client.utils.misc.NbtUtils;
import envy.client.utils.network.MatHaxExecutor;
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

        addButton(l, "Cracked", () -> Envy.mc.setScreen(new AddCrackedAccountScreen(theme, this)));
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
        addButton(l, "Mojang", () -> Envy.mc.setScreen(new AddMojangAccountScreen(theme, this)));
        addButton(l, "The Altening", () -> Envy.mc.setScreen(new AddAlteningAccountScreen(theme, this)));
    }

    private void addButton(WContainer c, String text, Runnable action) {
        WButton button = c.add(theme.button(text)).expandX().widget();
        button.action = action;
    }

    public static void addAccount(@Nullable AddAccountScreen screen, AccountsScreen parent, Account<?> account) {
        if (screen != null) screen.locked = true;

        MatHaxExecutor.execute(() -> {
            if (account.fetchInfo() && account.fetchHead()) {
                Accounts.get().add(account);
                if (account.login()) Accounts.get().save();

                if (screen != null) {
                    screen.locked = false;
                    screen.close();
                }

                parent.reload();

                return;
            }

            if (screen != null) screen.locked = false;
        });
    }

    @Override
    public boolean toClipboard() {
        return NbtUtils.toClipboard(Accounts.get());
    }

    @Override
    public boolean fromClipboard() {
        return NbtUtils.fromClipboard(Accounts.get());
    }
}
