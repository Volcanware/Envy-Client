package mathax.client.systems.hud.modules;

import mathax.client.mixin.ClientPlayerInteractionManagerAccessor;
import mathax.client.systems.hud.DoubleTextHudElement;
import mathax.client.systems.hud.HUD;

public class BreakingBlockHud extends DoubleTextHudElement {
    public BreakingBlockHud(HUD hud) {
        super(hud, "breaking-block", "Displays percentage of the block you are breaking.", true);
    }

    @Override
    protected String getLeft() {
        return "Breaking Block: ";
    }

    @Override
    protected String getRight() {
        if (isInEditor()) return "0%";
        return String.format("%.0f%%", ((ClientPlayerInteractionManagerAccessor) mc.interactionManager).getBreakingProgress() * 100);
    }
}
