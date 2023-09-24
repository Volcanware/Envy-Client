package mathax.client.gui.widgets;

import mathax.client.gui.renderer.GuiRenderer;
import net.minecraft.item.ItemStack;

public class WItem extends WWidget {
    protected ItemStack itemStack;

    public WItem(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    protected void onCalculateSize() {
        double s = theme.scale(32);

        width = s;
        height = s;
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        renderer.post(() -> {
            double s = theme.scale(2);
            renderer.item(itemStack, (int) x, (int) y, (float) s, true);
        });
    }

    public void set(ItemStack itemStack) {
        this.itemStack = itemStack;
    }
}
