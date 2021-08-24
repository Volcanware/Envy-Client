package mathax.client.legacy.systems.modules.render.hud.modules;

import mathax.client.legacy.mixin.MinecraftClientAccessor;
import mathax.client.legacy.systems.modules.render.hud.HUD;

public class FpsHud extends TripleTextHudElement {
    public FpsHud(HUD hud) {
        super(hud, "fps", "Displays your FPS.");
    }

    @Override
    protected String getLeft() {
        return "FPS: ";
    }

    @Override
    protected String getRight() {
        return Integer.toString(((MinecraftClientAccessor) mc).getFps());
    }

    @Override
    public String getEnd() {
        return "";
    }
}
