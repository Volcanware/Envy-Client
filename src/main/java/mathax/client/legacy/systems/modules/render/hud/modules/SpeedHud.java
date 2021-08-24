package mathax.client.legacy.systems.modules.render.hud.modules;

import mathax.client.legacy.systems.modules.Modules;
import mathax.client.legacy.systems.modules.render.hud.HUD;
import mathax.client.legacy.systems.modules.world.Timer;
import mathax.client.legacy.utils.Utils;

public class SpeedHud extends TripleTextHudElement {
    public SpeedHud(HUD hud) {
        super(hud, "speed", "Displays your horizontal speed.", "Speed: ");
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
