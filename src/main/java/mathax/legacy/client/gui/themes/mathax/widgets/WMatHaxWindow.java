package mathax.legacy.client.gui.themes.mathax.widgets;

import mathax.legacy.client.gui.renderer.GuiRenderer;
import mathax.legacy.client.gui.themes.mathax.MatHaxWidget;
import mathax.legacy.client.gui.widgets.containers.WWindow;

public class WMatHaxWindow extends WWindow implements MatHaxWidget {
    public WMatHaxWindow(String title) {
        super(title);
    }

    @Override
    protected WHeader header() {
        return new WMatHaxHeader();
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        if (expanded || animProgress > 0) {
            renderer.quadRounded(x, y + header.height / 2, width, height - header.height / 2, theme().backgroundColor.get(), theme.roundAmount(), false);
        }
    }

    private class WMatHaxHeader extends WHeader {
        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            renderer.quadRounded(this, theme().accentColor.get(), theme.roundAmount());
        }
    }
}
