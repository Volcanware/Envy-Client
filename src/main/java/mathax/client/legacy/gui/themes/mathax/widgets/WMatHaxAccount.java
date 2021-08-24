package mathax.client.legacy.gui.themes.mathax.widgets;

import mathax.client.legacy.gui.WidgetScreen;
import mathax.client.legacy.gui.themes.mathax.MatHaxWidget;
import mathax.client.legacy.gui.widgets.WAccount;
import mathax.client.legacy.systems.accounts.Account;
import mathax.client.legacy.utils.render.color.Color;

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
