package envy.client.gui.themes.meteor.widgets;

import envy.client.gui.renderer.GuiRenderer;
import envy.client.gui.themes.meteor.MeteorWidget;
import envy.client.gui.widgets.WTooltip;

public class WMeteorTooltip extends WTooltip implements MeteorWidget {
    public WMeteorTooltip(String text) {
        super(text);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        renderer.quad(this, theme().backgroundColor.get());
    }
}
