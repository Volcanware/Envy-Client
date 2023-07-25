package envy.client.gui.themes.mathax.widgets.pressable;

import envy.client.gui.renderer.GuiRenderer;
import envy.client.gui.themes.mathax.MatHaxWidget;
import envy.client.gui.widgets.pressable.WTriangle;

public class WMatHaxTriangle extends WTriangle implements MatHaxWidget {
    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        renderer.rotatedQuad(x, y, width, height, rotation, GuiRenderer.TRIANGLE, theme().backgroundColor.get(pressed, mouseOver));
    }
}
