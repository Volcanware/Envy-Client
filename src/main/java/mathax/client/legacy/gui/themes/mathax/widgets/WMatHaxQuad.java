package mathax.client.legacy.gui.themes.mathax.widgets;

import mathax.client.legacy.gui.renderer.GuiRenderer;
import mathax.client.legacy.gui.widgets.WQuad;
import mathax.client.legacy.utils.render.color.Color;

public class WMatHaxQuad extends WQuad {
    public WMatHaxQuad(Color color) {
        super(color);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        renderer.quad(x, y, width, height, color);
    }
}
