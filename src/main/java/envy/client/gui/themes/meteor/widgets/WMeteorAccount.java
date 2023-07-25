package envy.client.gui.themes.meteor.widgets;

import envy.client.gui.WidgetScreen;
import envy.client.gui.themes.meteor.MeteorWidget;
import envy.client.gui.widgets.WAccount;
import envy.client.systems.accounts.Account;
import envy.client.utils.render.color.Color;

public class WMeteorAccount extends WAccount implements MeteorWidget {
    public WMeteorAccount(WidgetScreen screen, Account<?> account) {
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
