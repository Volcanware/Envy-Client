package mathax.client.systems.modules.render.hud.modules;

import mathax.client.utils.world.TickRate;
import mathax.client.systems.modules.render.hud.DoubleTextHudElement;
import mathax.client.systems.modules.render.hud.HUD;

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
