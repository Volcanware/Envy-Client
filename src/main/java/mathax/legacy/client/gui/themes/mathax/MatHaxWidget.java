package mathax.legacy.client.gui.themes.mathax;

import mathax.legacy.client.gui.renderer.GuiRenderer;
import mathax.legacy.client.gui.utils.BaseWidget;
import mathax.legacy.client.gui.widgets.WWidget;
import mathax.legacy.client.utils.render.color.Color;

public interface MatHaxWidget extends BaseWidget {
    default MatHaxGuiTheme theme() {
        return (MatHaxGuiTheme) getTheme();
    }

    default void renderBackground(GuiRenderer renderer, WWidget widget, boolean pressed, boolean mouseOver) {
        MatHaxGuiTheme theme = theme();
        double s = theme.scale(2);

        int r = theme.roundAmount();
        Color outlineColor = theme.outlineColor.get(pressed, mouseOver);
        renderer.quadRounded(widget.x + s, widget.y + s, widget.width - s * 2, widget.height - s * 2, theme.backgroundColor.get(pressed, mouseOver), r - s);
        renderer.quadOutlineRounded(widget, outlineColor, r, s);
    }
}
