package mathax.client.gui.screens.accounts;

import mathax.client.MatHax;
import mathax.client.gui.GuiTheme;
import mathax.client.gui.widgets.containers.WTable;
import mathax.client.gui.widgets.input.WTextBox;
import mathax.client.systems.accounts.MicrosoftCookieLogin;
import mathax.client.systems.accounts.types.MicrosoftAccount;
import mathax.client.utils.network.MatHaxExecutor;

import java.io.File;

public class AddCookieAccountScreen extends AddAccountScreen{
    Exception exception = null;
    protected AddCookieAccountScreen(GuiTheme theme, AccountsScreen parent) {
        super(theme, "Import Cookie alt", parent);
    }

    public void initWidgets() {
        WTable t = add(theme.table()).widget();

        // last error
        if (exception != null) {
            t.add(theme.label("ERROR: " + exception.getLocalizedMessage()));
            t.row();
        }

        // Token
        t.add(theme.label("Path: "));
        WTextBox path = t.add(theme.textBox("")).minWidth(400).expandX().widget();
        path.setFocused(true);
        t.row();

        // Add
        add = t.add(theme.button("Add")).expandX().widget();
        add.action = () -> {
            MatHaxExecutor.EXECUTOR.execute(
                () -> {
                    if (!path.get().isEmpty()) {
                        try {
                            locked = true;
                            File file = new File(path.get());
                            String refreshToken = MicrosoftCookieLogin.getRefreshTokenFromCookie(file);
                            locked = false;

                            if (refreshToken != null) {
                                MicrosoftAccount account = new MicrosoftAccount(refreshToken);
                                AccountsScreen.addAccount(this, parent, account);
                            }
                        } catch (Exception e) {
                            exception = e;
                            locked = false;
                        }
                    }
                }
            );
        };

        enterAction = add.action;
    }
}
