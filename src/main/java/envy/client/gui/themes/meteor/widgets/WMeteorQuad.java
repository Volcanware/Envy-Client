package envy.client.gui.themes.meteor.widgets;

import envy.client.gui.renderer.GuiRenderer;
import envy.client.gui.widgets.WQuad;
import envy.client.utils.render.color.Color;

public class WMeteorQuad extends WQuad {
    public WMeteorQuad(Color color) {
        super(color);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        renderer.quadRounded(x, y, width, height, color, theme.roundAmount());
    }
}
