package envy.client.gui.themes.mathax.widgets;

import envy.client.gui.renderer.GuiRenderer;
import envy.client.gui.themes.mathax.MatHaxWidget;
import envy.client.gui.widgets.WLabel;

public class WMatHaxLabel extends WLabel implements MatHaxWidget {
    public WMatHaxLabel(String text, boolean title) {
        super(text, title);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        if (!text.isEmpty()) {
            renderer.text(text, x, y, color != null ? color : (title ? theme().titleTextColor.get() : theme().textColor.get()), title);
        }
    }
}
