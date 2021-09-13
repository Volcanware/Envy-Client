package mathax.legacy.client.systems.modules.render.hud.modules;

import mathax.legacy.client.settings.BoolSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.systems.modules.render.hud.HUD;
import mathax.legacy.client.systems.modules.render.hud.TripleTextHUDElement;
import mathax.legacy.client.utils.Utils;

import java.text.SimpleDateFormat;

public class InGameTimeHUD extends TripleTextHUDElement {

    public InGameTimeHUD(HUD hud) {
        super(hud, "in-game-time", "Displays the in-game time.", true);
    }

    @Override
    protected String getLeft() {
        return "In-Game Time: ";
    }

    @Override
    protected String getRight() {
        if (isInEditor()) return "12:00:00";

        return Utils.getWorldTime();
    }

    @Override
    public String getEnd() {
        return "";
    }
}
