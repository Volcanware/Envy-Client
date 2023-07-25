package envy.client.gui.themes.meteor.widgets;

import envy.client.gui.themes.meteor.MeteorWidget;
import envy.client.gui.widgets.WTopBar;
import envy.client.utils.render.color.Color;

public class WMeteorTopBar extends WTopBar implements MeteorWidget {
    @Override
    protected Color getButtonColor(boolean pressed, boolean hovered) {
        return theme().backgroundColor.get(pressed, hovered);
    }

    @Override
    protected Color getNameColor() {
        return theme().textColor.get();
    }
}
