package mathax.client.legacy.gui.themes.mathax.widgets.input;

import mathax.client.legacy.gui.renderer.GuiRenderer;
import mathax.client.legacy.gui.themes.mathax.MatHaxGuiTheme;
import mathax.client.legacy.gui.themes.mathax.MatHaxWidget;
import mathax.client.legacy.gui.utils.CharFilter;
import mathax.client.legacy.gui.widgets.input.WTextBox;
import mathax.client.legacy.utils.Utils;

public class WMatHaxTextBox extends WTextBox implements MatHaxWidget {
    private boolean cursorVisible;
    private double cursorTimer;

    private double animProgress;

    public WMatHaxTextBox(String text, CharFilter filter) {
        super(text, filter);
    }

    @Override
    protected void onCursorChanged() {
        cursorVisible = true;
        cursorTimer = 0;
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        if (cursorTimer >= 1) {
            cursorVisible = !cursorVisible;
            cursorTimer = 0;
        }
        else {
            cursorTimer += delta * 1.75;
        }

        renderBackground(renderer, this, false, false);
        renderTextAndCursor(renderer, delta);
    }

    private void renderTextAndCursor(GuiRenderer renderer, double delta) {
        MatHaxGuiTheme theme = theme();
        double pad = pad();

        double overflowWidth = getOverflowWidthForRender();

        if (!text.isEmpty()) {
            renderer.scissorStart(x + pad, y + pad, width - pad * 2, height - pad * 2);
            renderer.text(text, x + pad - overflowWidth, y + pad, theme.textColor.get(), false);
            renderer.scissorEnd();
        }

        animProgress += delta * 10 * (focused && cursorVisible ? 1 : -1);
        animProgress = Utils.clamp(animProgress, 0, 1);

        if ((focused && cursorVisible) || animProgress > 0) {
            renderer.setAlpha(animProgress);
            renderer.quad(x + pad + getCursorTextWidth() - overflowWidth, y + pad, theme.scale(1), theme.textHeight(), theme.textColor.get());
            renderer.setAlpha(1);
        }
    }
}
