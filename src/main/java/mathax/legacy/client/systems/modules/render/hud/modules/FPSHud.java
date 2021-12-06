package mathax.legacy.client.systems.modules.render.hud.modules;

import mathax.legacy.client.mixin.MinecraftClientAccessor;
import mathax.legacy.client.systems.modules.render.hud.DoubleTextHudElement;
import mathax.legacy.client.systems.modules.render.hud.HUD;
import mathax.legacy.client.systems.modules.render.hud.TripleTextHudElement;

public class FPSHud extends DoubleTextHudElement {
    public FPSHud(HUD hud) {
        super(hud, "fps", "Displays your FPS.", true);
    }

    @Override
    protected String getLeft() {
        return "FPS: ";
    }

    @Override
    protected String getRight() {
        return Integer.toString(MinecraftClientAccessor.getFps());
    }
}
