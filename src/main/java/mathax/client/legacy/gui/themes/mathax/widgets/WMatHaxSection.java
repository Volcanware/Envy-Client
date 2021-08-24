package mathax.client.legacy.gui.themes.mathax.widgets;

import mathax.client.legacy.gui.renderer.GuiRenderer;
import mathax.client.legacy.gui.themes.mathax.MatHaxWidget;
import mathax.client.legacy.gui.widgets.WWidget;
import mathax.client.legacy.gui.widgets.containers.WSection;
import mathax.client.legacy.gui.widgets.pressable.WTriangle;

public class WMatHaxSection extends WSection {
    public WMatHaxSection(String title, boolean expanded, WWidget headerWidget) {
        super(title, expanded, headerWidget);
    }

    @Override
    protected WHeader createHeader() {
        return new WMatHaxHeader(title);
    }

    protected class WMatHaxHeader extends WHeader {
        private WTriangle triangle;

        public WMatHaxHeader(String title) {
            super(title);
        }

        @Override
        public void init() {
            add(theme.horizontalSeparator(title)).expandX();

            if (headerWidget != null) add(headerWidget);

            triangle = new WHeaderTriangle();
            triangle.theme = theme;
            triangle.action = this::onClick;

            add(triangle);
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            triangle.rotation = (1 - animProgress) * -90;
        }
    }

    protected static class WHeaderTriangle extends WTriangle implements MatHaxWidget {
        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            renderer.rotatedQuad(x, y, width, height, rotation, GuiRenderer.TRIANGLE, theme().textColor.get());
        }
    }
}
