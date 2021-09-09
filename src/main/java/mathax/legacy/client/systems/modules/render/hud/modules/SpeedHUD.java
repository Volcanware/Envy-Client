package mathax.legacy.client.systems.modules.render.hud.modules;

import mathax.legacy.client.systems.modules.render.hud.HUD;
import mathax.legacy.client.systems.modules.render.hud.TripleTextHUDElement;
import mathax.legacy.client.utils.Utils;

public class SpeedHUD extends TripleTextHUDElement {
    public SpeedHUD(HUD hud) {
        super(hud, "speed", "Displays your horizontal speed.", true);
    }

    @Override
    protected String getLeft() {
        return "Speed: ";
    }

    @Override
    protected String getRight() {
        if (isInEditor()) return "0,0";

        return String.format("%.1f", Utils.getPlayerSpeed());
    }

    @Override
    public String getEnd() {
        return "";
    }
}
