package envy.client.systems.hud.modules;

import envy.client.settings.BoolSetting;
import envy.client.settings.Setting;
import envy.client.settings.SettingGroup;
import envy.client.systems.hud.DoubleTextHudElement;
import envy.client.systems.hud.HUD;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RealTimeHud extends DoubleTextHudElement {
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

    public RealTimeHud(HUD hud) {
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
}
