package envy.client.gui.themes.meteor.widgets.pressable;

import envy.client.gui.renderer.GuiRenderer;
import envy.client.gui.themes.meteor.MeteorWidget;
import envy.client.gui.widgets.pressable.WTriangle;

public class WMeteorTriangle extends WTriangle implements MeteorWidget {
    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        renderer.rotatedQuad(x, y, width, height, rotation, GuiRenderer.TRIANGLE, theme().backgroundColor.get(pressed, mouseOver));
    }
}
