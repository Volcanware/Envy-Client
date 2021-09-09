package mathax.legacy.client.gui.themes.mathax.widgets;

import mathax.legacy.client.gui.renderer.GuiRenderer;
import mathax.legacy.client.gui.themes.mathax.MatHaxWidget;
import mathax.legacy.client.gui.widgets.containers.WView;

public class WMatHaxView extends WView implements MatHaxWidget {
    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        if (canScroll && hasScrollBar) {
            renderer.quad(handleX(), handleY(), handleWidth(), handleHeight(), theme().scrollbarColor.get(handlePressed, handleMouseOver));
        }
    }
}
