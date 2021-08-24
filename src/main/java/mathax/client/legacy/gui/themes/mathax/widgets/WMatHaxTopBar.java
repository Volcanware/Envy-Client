package mathax.client.legacy.gui.themes.mathax.widgets;

import mathax.client.legacy.gui.themes.mathax.MatHaxWidget;
import mathax.client.legacy.gui.widgets.WTopBar;
import mathax.client.legacy.utils.render.color.Color;

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
