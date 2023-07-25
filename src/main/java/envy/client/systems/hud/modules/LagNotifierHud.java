package envy.client.systems.hud.modules;

import envy.client.settings.ColorSetting;
import envy.client.settings.Setting;
import envy.client.settings.SettingGroup;
import envy.client.systems.hud.DoubleTextHudElement;
import envy.client.systems.hud.HUD;
import envy.client.utils.render.color.SettingColor;
import envy.client.utils.world.TickRate;

public class LagNotifierHud extends DoubleTextHudElement {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<SettingColor> badColor = sgGeneral.add(new ColorSetting.Builder()
        .name("bad-color")
        .description("Color of lag length.")
        .defaultValue(new SettingColor(255, 165, 50))
        .build()
    );

    private final Setting<SettingColor> severeColor = sgGeneral.add(new ColorSetting.Builder()
        .name("severe-color")
        .description("Color of lag length.")
        .defaultValue(new SettingColor(155, 0, 0))
        .build()
    );

    public LagNotifierHud(HUD hud) {
        super(hud, "lag-notifier", "Displays if the server is lagging in seconds.", true);
    }

    @Override
    protected String getLeft() {
        return "Server is lagging ";
    }

    @Override
    protected String getRight() {
        if (isInEditor()) {
            rightColor = hud.secondaryColor.get();
            visible = true;
            return "4,3s";
        }

        float timeSinceLastTick = TickRate.INSTANCE.getTimeSinceLastTick();

        if (timeSinceLastTick > 10) rightColor = severeColor.get();
        else rightColor = badColor.get();

        visible = timeSinceLastTick >= 1f;

        return String.format("%.1f", timeSinceLastTick) + "s";
    }
}
