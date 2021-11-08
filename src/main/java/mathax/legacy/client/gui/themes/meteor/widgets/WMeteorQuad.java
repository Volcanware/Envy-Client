package mathax.legacy.client.gui.themes.meteor.widgets;

import mathax.legacy.client.gui.renderer.GuiRenderer;
import mathax.legacy.client.gui.widgets.WQuad;
import mathax.legacy.client.utils.render.color.Color;

public class WMeteorQuad extends WQuad {
    public WMeteorQuad(Color color) {
        super(color);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        renderer.quadRounded(x, y, width, height, color, theme.roundAmount());
    }
}
