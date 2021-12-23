package mathax.legacy.client.systems.modules.render.hud.modules;

import mathax.legacy.client.systems.modules.render.hud.DoubleTextHudElement;
import mathax.legacy.client.systems.modules.render.hud.HUD;
import mathax.legacy.client.utils.world.TickRate;

public class TPSHud extends DoubleTextHudElement {
    public TPSHud(HUD hud) {
        super(hud, "tps", "Displays the server's TPS.", true);
    }

    @Override
    protected String getLeft() {
        return "TPS: ";
    }

    @Override
    protected String getRight() {
        return String.format("%.1f", TickRate.INSTANCE.getTickRate()).replace(",", ".");
    }
}
