package envy.client.gui.themes.mathax.widgets;

import envy.client.gui.renderer.GuiRenderer;
import envy.client.gui.themes.mathax.MatHaxWidget;
import envy.client.gui.widgets.WTooltip;

public class WMatHaxTooltip extends WTooltip implements MatHaxWidget {
    public WMatHaxTooltip(String text) {
        super(text);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        renderer.quad(this, theme().backgroundColor.get());
    }
}
