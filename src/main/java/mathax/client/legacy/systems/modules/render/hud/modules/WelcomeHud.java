package mathax.client.legacy.systems.modules.render.hud.modules;

import mathax.client.legacy.settings.ColorSetting;
import mathax.client.legacy.settings.Setting;
import mathax.client.legacy.settings.SettingGroup;
import mathax.client.legacy.systems.modules.Modules;
import mathax.client.legacy.systems.modules.misc.NameProtect;
import mathax.client.legacy.systems.modules.render.hud.HUD;
import mathax.client.legacy.utils.Utils;
import mathax.client.legacy.utils.render.color.SettingColor;

public class WelcomeHud extends TripleTextHudElement {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<SettingColor> usernameColor = sgGeneral.add(new ColorSetting.Builder()
        .name("username-color")
        .description("Color of the username.")
        .defaultValue(new SettingColor(0, 255, 100, true))
        .build()
    );

    public WelcomeHud(HUD hud) {
        super(hud, "welcome", "Displays a welcome message.", "Welcome to MatHax Client Legacy, ");
        rightColor = usernameColor.get();
    }

    @Override
    protected String getRight() {
        return Modules.get().get(NameProtect.class).getName(mc.getSession().getUsername());
    }

    @Override
    public String getEnd() {
        return "!";
    }
}
