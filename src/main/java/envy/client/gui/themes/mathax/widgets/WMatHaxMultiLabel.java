package envy.client.gui.themes.mathax.widgets;

import envy.client.gui.renderer.GuiRenderer;
import envy.client.gui.themes.mathax.MatHaxWidget;
import envy.client.gui.widgets.WMultiLabel;
import envy.client.utils.render.color.Color;

public class WMatHaxMultiLabel extends WMultiLabel implements MatHaxWidget {
    public WMatHaxMultiLabel(String text, boolean title, double maxWidth) {
        super(text, title, maxWidth);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        double h = theme.textHeight(title);
        Color defaultColor = theme().textColor.get();

        for (int i = 0; i < lines.size(); i++) {
            renderer.text(lines.get(i), x, y + h * i, color != null ? color : defaultColor, false);
        }
    }
}
