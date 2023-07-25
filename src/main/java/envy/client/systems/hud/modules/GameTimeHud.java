package envy.client.systems.hud.modules;

import envy.client.systems.hud.DoubleTextHudElement;
import envy.client.systems.hud.HUD;
import envy.client.utils.Utils;

public class GameTimeHud extends DoubleTextHudElement {

    public GameTimeHud(HUD hud) {
        super(hud, "game-time", "Displays the in-game time.", true);
    }

    @Override
    protected String getLeft() {
        return "Game Time: ";
    }

    @Override
    protected String getRight() {
        if (isInEditor()) return "12:00:00";

        return Utils.getWorldTime();
    }
}
