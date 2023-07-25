package envy.client.gui.themes.mathax.widgets;

import envy.client.gui.themes.mathax.MatHaxWidget;
import envy.client.gui.widgets.WTopBar;
import envy.client.utils.render.color.Color;

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
