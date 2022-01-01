package mathax.client.systems.modules.render.hud.modules;

import mathax.client.mixin.MinecraftClientAccessor;
import mathax.client.systems.modules.render.hud.DoubleTextHudElement;
import mathax.client.systems.modules.render.hud.HUD;

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
