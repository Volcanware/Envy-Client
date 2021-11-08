package mathax.legacy.client.gui.themes.meteor.widgets;

import mathax.legacy.client.gui.themes.meteor.MeteorWidget;
import mathax.legacy.client.gui.widgets.WTopBar;
import mathax.legacy.client.utils.render.color.Color;

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
