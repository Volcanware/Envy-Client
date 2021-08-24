package mathax.client.legacy.utils.tooltip;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;

public interface MatHaxTooltipData extends TooltipData {
    MinecraftClient mc = MinecraftClient.getInstance();

    TooltipComponent getComponent();
}
