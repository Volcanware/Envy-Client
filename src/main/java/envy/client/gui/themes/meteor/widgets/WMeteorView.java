package envy.client.gui.themes.meteor.widgets;

import envy.client.gui.renderer.GuiRenderer;
import envy.client.gui.themes.meteor.MeteorWidget;
import envy.client.gui.widgets.containers.WView;

public class WMeteorView extends WView implements MeteorWidget {
    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        if (canScroll && hasScrollBar) renderer.quad(handleX(), handleY(), handleWidth(), handleHeight(), theme().scrollbarColor.get(handlePressed, handleMouseOver));
    }
}
