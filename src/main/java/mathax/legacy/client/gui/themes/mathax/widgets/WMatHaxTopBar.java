package mathax.legacy.client.gui.themes.mathax.widgets;

import mathax.legacy.client.gui.themes.mathax.MatHaxWidget;
import mathax.legacy.client.gui.widgets.WTopBar;
import mathax.legacy.client.utils.render.color.Color;

public class WMatHaxTopBar extends WTopBar implements MatHaxWidget {
    @Override
    protected Color getButtonColor(boolean pressed, boolean hovered) {
        return theme().backgroundColor.get(pressed, hovered);
    }

    @Override
    protected Color getNameColor() {
        return theme().textColor.get();
    }
}
