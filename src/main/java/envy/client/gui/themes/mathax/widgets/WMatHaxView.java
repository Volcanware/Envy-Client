package envy.client.gui.themes.mathax.widgets;

import envy.client.gui.renderer.GuiRenderer;
import envy.client.gui.themes.mathax.MatHaxWidget;
import envy.client.gui.widgets.containers.WView;

public class WMatHaxView extends WView implements MatHaxWidget {
    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        if (canScroll && hasScrollBar) renderer.quad(handleX(), handleY(), handleWidth(), handleHeight(), theme().scrollbarColor.get(handlePressed, handleMouseOver));
    }
}
