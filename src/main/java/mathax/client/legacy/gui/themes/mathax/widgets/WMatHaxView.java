package mathax.client.legacy.gui.themes.mathax.widgets;

import mathax.client.legacy.gui.renderer.GuiRenderer;
import mathax.client.legacy.gui.themes.mathax.MatHaxWidget;
import mathax.client.legacy.gui.widgets.containers.WView;

public class WMatHaxView extends WView implements MatHaxWidget {
    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        if (canScroll && hasScrollBar) {
            renderer.quad(handleX(), handleY(), handleWidth(), handleHeight(), theme().scrollbarColor.get(handlePressed, handleMouseOver));
        }
    }
}
