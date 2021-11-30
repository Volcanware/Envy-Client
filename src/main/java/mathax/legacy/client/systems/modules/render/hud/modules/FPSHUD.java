package mathax.legacy.client.systems.modules.render.hud.modules;

import mathax.legacy.client.mixin.MinecraftClientAccessor;
import mathax.legacy.client.systems.modules.render.hud.HUD;
import mathax.legacy.client.systems.modules.render.hud.TripleTextHUDElement;

public class FPSHUD extends TripleTextHUDElement {
    public FPSHUD(HUD hud) {
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

    @Override
    public String getEnd() {
        return "";
    }
}
