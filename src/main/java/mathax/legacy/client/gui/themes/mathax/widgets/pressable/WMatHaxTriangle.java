package mathax.legacy.client.gui.themes.mathax.widgets.pressable;

import mathax.legacy.client.gui.renderer.GuiRenderer;
import mathax.legacy.client.gui.themes.mathax.MatHaxWidget;
import mathax.legacy.client.gui.widgets.pressable.WTriangle;

public class WMatHaxTriangle extends WTriangle implements MatHaxWidget {
    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        renderer.rotatedQuad(x, y, width, height, rotation, GuiRenderer.TRIANGLE, theme().backgroundColor.get(pressed, mouseOver));
    }
}
