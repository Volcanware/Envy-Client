package mathax.legacy.client.systems.modules.render.hud.modules;

import mathax.legacy.client.settings.BoolSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.systems.modules.render.hud.HUD;
import mathax.legacy.client.systems.modules.render.hud.TripleTextHUDElement;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RealTimeHUD extends TripleTextHUDElement {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private final SimpleDateFormat timeFormatSeconds = new SimpleDateFormat("HH:mm:ss");

    // General

    private final Setting<Boolean> seconds = sgGeneral.add(new BoolSetting.Builder()
        .name("seconds")
        .description("Shows seconds.")
        .defaultValue(true)
        .build()
    );

    public RealTimeHUD(HUD hud) {
        super(hud, "real-time", "Displays real world time.", true);
    }

    @Override
    protected String getLeft() {
        return "Time: ";
    }

    @Override
    protected String getRight() {
        if (seconds.get()) return timeFormatSeconds.format(new Date());
        return timeFormat.format(new Date());
    }

    @Override
    public String getEnd() {
        return "";
    }
}
