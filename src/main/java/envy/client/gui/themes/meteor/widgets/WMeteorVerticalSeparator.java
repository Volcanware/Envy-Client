package envy.client.gui.themes.meteor.widgets;

import envy.client.gui.renderer.GuiRenderer;
import envy.client.gui.themes.meteor.MeteorGuiTheme;
import envy.client.gui.themes.meteor.MeteorWidget;
import envy.client.gui.widgets.WVerticalSeparator;
import envy.client.utils.render.color.Color;

public class WMeteorVerticalSeparator extends WVerticalSeparator implements MeteorWidget {
    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        MeteorGuiTheme theme = theme();
        Color colorEdges = theme.separatorEdges.get();
        Color colorCenter = theme.separatorCenter.get();

        double s = theme.scale(1);
        double offsetX = Math.round(width / 2.0);

        renderer.quad(x + offsetX, y, s, height / 2, colorEdges, colorEdges, colorCenter, colorCenter);
        renderer.quad(x + offsetX, y + height / 2, s, height / 2, colorCenter, colorCenter, colorEdges, colorEdges);
    }
}
