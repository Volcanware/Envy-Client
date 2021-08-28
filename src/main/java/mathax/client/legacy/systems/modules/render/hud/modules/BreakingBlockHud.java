package mathax.client.legacy.systems.modules.render.hud.modules;

import mathax.client.legacy.mixin.ClientPlayerInteractionManagerAccessor;
import mathax.client.legacy.systems.modules.render.hud.HUD;
import mathax.client.legacy.systems.modules.render.hud.TripleTextHudElement;

public class BreakingBlockHud extends TripleTextHudElement {
    public BreakingBlockHud(HUD hud) {
        super(hud, "breaking-block", "Displays percentage of the block you are breaking.");
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

    @Override
    public String getEnd() {
        return "";
    }
}
