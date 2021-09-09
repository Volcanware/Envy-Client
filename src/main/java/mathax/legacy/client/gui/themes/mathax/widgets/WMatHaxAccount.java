package mathax.legacy.client.gui.themes.mathax.widgets;

import mathax.legacy.client.gui.WidgetScreen;
import mathax.legacy.client.gui.themes.mathax.MatHaxWidget;
import mathax.legacy.client.gui.widgets.WAccount;
import mathax.legacy.client.systems.accounts.Account;
import mathax.legacy.client.utils.render.color.Color;

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
