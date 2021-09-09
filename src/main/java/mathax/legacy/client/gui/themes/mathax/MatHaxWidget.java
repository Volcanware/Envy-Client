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

        renderer.quad(widget.x + s, widget.y + s, widget.width - s * 2, widget.height - s * 2, theme.backgroundColor.get(pressed, mouseOver));

        Color outlineColor = theme.outlineColor.get(pressed, mouseOver);
        renderer.quad(widget.x, widget.y, widget.width, s, outlineColor);
        renderer.quad(widget.x, widget.y + widget.height - s, widget.width, s, outlineColor);
        renderer.quad(widget.x, widget.y + s, s, widget.height - s * 2, outlineColor);
        renderer.quad(widget.x + widget.width - s, widget.y + s, s, widget.height - s * 2, outlineColor);
    }
}
