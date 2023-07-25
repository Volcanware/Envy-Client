package envy.client.gui.themes.mathax.widgets;

import envy.client.gui.WidgetScreen;
import envy.client.gui.themes.mathax.MatHaxWidget;
import envy.client.gui.widgets.WAccount;
import envy.client.systems.accounts.Account;
import envy.client.utils.render.color.Color;

public class WMatHaxAccount extends WAccount implements MatHaxWidget {
    public WMatHaxAccount(WidgetScreen screen, Account<?> account) {
        super(screen, account);
    }

    @Override
    protected Color loggedInColor() {
        return theme().loggedInColor.get();
    }

    @Override
    protected Color accountTypeColor() {
        return theme().textSecondaryColor.get();
    }
}
