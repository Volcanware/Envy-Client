package mathax.client.legacy.gui.themes.mathax.widgets;

import mathax.client.legacy.gui.renderer.GuiRenderer;
import mathax.client.legacy.gui.themes.mathax.MatHaxWidget;
import mathax.client.legacy.gui.widgets.WMultiLabel;
import mathax.client.legacy.utils.render.color.Color;

public class WMatHaxMultiLabel extends WMultiLabel implements MatHaxWidget {
    public WMatHaxMultiLabel(String text, boolean title, double maxWidth) {
        super(text, title, maxWidth);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        double h = theme.textHeight(title);
        Color color = theme().textColor.get();

        for (int i = 0; i < lines.size(); i++) {
            renderer.text(lines.get(i), x, y + h * i, color, false);
        }
    }
}
