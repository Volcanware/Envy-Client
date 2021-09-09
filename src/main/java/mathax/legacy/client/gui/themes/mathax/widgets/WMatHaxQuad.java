package mathax.legacy.client.gui.themes.mathax.widgets;

import mathax.legacy.client.gui.renderer.GuiRenderer;
import mathax.legacy.client.gui.widgets.WQuad;
import mathax.legacy.client.utils.render.color.Color;

public class WMatHaxQuad extends WQuad {
    public WMatHaxQuad(Color color) {
        super(color);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        renderer.quad(x, y, width, height, color);
    }
}
