package mathax.legacy.client.gui.widgets;

import mathax.legacy.client.gui.renderer.GuiRenderer;
import mathax.legacy.client.gui.tabs.Tab;
import mathax.legacy.client.gui.tabs.TabScreen;
import mathax.legacy.client.gui.tabs.Tabs;
import mathax.legacy.client.gui.widgets.containers.WHorizontalList;
import mathax.legacy.client.gui.widgets.pressable.WPressable;
import mathax.legacy.client.utils.render.color.Color;
import mathax.legacy.client.utils.Utils;
import net.minecraft.client.gui.screen.Screen;

import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;

public abstract class WTopBar extends WHorizontalList {
    protected abstract Color getButtonColor(boolean pressed, boolean hovered);

    protected abstract Color getNameColor();

    public WTopBar() {
        spacing = 0;
    }

    @Override
    public void init() {
        for (Tab tab : Tabs.get()) {
            add(new WTopBarButton(tab));
        }
    }

    protected int getState(WTopBarButton btn) {
        int a = 0;
        if (btn.equals(cells.get(0).widget()))
            a |= 1;
        if (btn.equals(cells.get(cells.size() - 1).widget()))
            a |= 2;
        return a;
    }

    protected class WTopBarButton extends WPressable {
        private final Tab tab;

        public WTopBarButton(Tab tab) {
            this.tab = tab;
        }

        @Override
        protected void onCalculateSize() {
            double pad = pad();

            width = pad + theme.textWidth(tab.name) + pad;
            height = pad + theme.textHeight() + pad;
        }

        @Override
        protected void onPressed(int button) {
            Screen screen = Utils.mc.currentScreen;

            if (!(screen instanceof TabScreen) || ((TabScreen) screen).tab != tab) {
                double mouseX = Utils.mc.mouse.getX();
                double mouseY = Utils.mc.mouse.getY();

                tab.openScreen(theme);
                glfwSetCursorPos(Utils.mc.getWindow().getHandle(), mouseX, mouseY);
            }
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            double pad = pad();
            Color color = getButtonColor(pressed || (Utils.mc.currentScreen instanceof TabScreen && ((TabScreen) Utils.mc.currentScreen).tab == tab), mouseOver);

            switch (getState(this)) {
                case 1:
                    renderer.quadRoundedSide(this, color, theme.roundAmount(), false);
                    break;
                case 2:
                    renderer.quadRoundedSide(this, color, theme.roundAmount(), true);
                    break;
                case 3:
                    renderer.quadRounded(this, color, theme.roundAmount());
                    break;
                default:
                    renderer.quad(this, color);
                    break;
            }
            renderer.text(tab.name, x + pad, y + pad, getNameColor(), false);
        }
    }
}
