package mathax.client.legacy.systems.modules.render.hud.modules;

import mathax.client.legacy.settings.BoolSetting;
import mathax.client.legacy.settings.Setting;
import mathax.client.legacy.settings.SettingGroup;
import mathax.client.legacy.systems.modules.render.hud.HUD;
import mathax.client.legacy.systems.modules.render.hud.TripleTextHudElement;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateHud extends TripleTextHudElement {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    public final Setting<Boolean> euDate = sgGeneral.add(new BoolSetting.Builder()
        .name("EU-date")
        .description("Changes the date to Europian format.")
        .defaultValue(false)
        .build()
    );

    public DateHud(HUD hud) {
        super(hud, "date", "Displays current date.", true);
    }

    @Override
    protected String getLeft() {
        return "Date: ";
    }

    @Override
    protected String getRight() {
        if (euDate.get()) {
            return getDate() + " EU";
        } else {
            return getDate() + " US";
        }
    }

    @Override
    public String getEnd() {
        return "";
    }

    private String getDate() {
        if (euDate.get()) {
            return new SimpleDateFormat("dd/MM/yy").format(Calendar.getInstance().getTime());
        } else {
            return new SimpleDateFormat("MM/dd/yy").format(Calendar.getInstance().getTime());
        }
    }
}
