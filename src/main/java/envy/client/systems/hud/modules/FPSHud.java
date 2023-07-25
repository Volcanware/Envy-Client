package envy.client.systems.hud.modules;

import envy.client.mixin.MinecraftClientAccessor;
import envy.client.systems.hud.DoubleTextHudElement;
import envy.client.systems.hud.HUD;

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
