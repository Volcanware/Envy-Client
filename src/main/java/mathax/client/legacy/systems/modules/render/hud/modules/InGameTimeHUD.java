package mathax.client.legacy.systems.modules.render.hud.modules;

import mathax.client.legacy.systems.modules.render.hud.HUD;
import mathax.client.legacy.systems.modules.render.hud.TripleTextHUDElement;
import mathax.client.legacy.utils.Utils;

public class InGameTimeHUD extends TripleTextHUDElement {
    /*private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    public final Setting<Boolean> euFormat = sgGeneral.add(new BoolSetting.Builder()
        .name("eu-format")
        .description("Changes the time to Europian format.")
        .defaultValue(false)
        .build()
    );*/
    //TODO: Change default to AM & PM. Add EU format setting 12:00.

    public InGameTimeHUD(HUD hud) {
        super(hud, "in-game-time", "Displays the in-game time.", true);
    }

    @Override
    protected String getLeft() {
        return "In-Game Time: ";
    }

    @Override
    protected String getRight() {
                if (isInEditor()) return "12:00";

                return Utils.getWorldTime();
    }

    @Override
    public String getEnd() {
        return "";
    }
}
