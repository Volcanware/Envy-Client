package mathax.legacy.client.utils.render.color;

import mathax.legacy.client.MatHaxLegacy;
import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.gui.GuiThemes;
import mathax.legacy.client.gui.WidgetScreen;
import mathax.legacy.client.gui.tabs.builtin.ConfigTab;
import mathax.legacy.client.settings.ColorSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.systems.waypoints.Waypoint;
import mathax.legacy.client.systems.waypoints.Waypoints;
import mathax.legacy.client.utils.misc.UnorderedArrayList;
import mathax.legacy.client.bus.EventHandler;

import java.util.List;

import static mathax.legacy.client.utils.Utils.mc;

public class RainbowColors {

    public static final RainbowColor GLOBAL = new RainbowColor().setSpeed(ConfigTab.rainbowSpeed.get() / 100);

    private static final List<Setting<SettingColor>> colorSettings = new UnorderedArrayList<>();
    private static final List<SettingColor> colors = new UnorderedArrayList<>();
    private static final List<Runnable> listeners = new UnorderedArrayList<>();

    public static void init() {
        MatHaxLegacy.EVENT_BUS.subscribe(RainbowColors.class);
    }

    public static void addSetting(Setting<SettingColor> setting) {
        colorSettings.add(setting);
    }

    public static void removeSetting(Setting<SettingColor> setting) {
        colorSettings.remove(setting);
    }

    public static void add(SettingColor color) {
        colors.add(color);
    }

    public static void register(Runnable runnable) {
        listeners.add(runnable);
    }

    @EventHandler
    private static void onTick(TickEvent.Post event) {
        GLOBAL.getNext();

        for (Setting<SettingColor> setting : colorSettings) {
            if (setting.module == null || setting.module.isActive()) setting.get().update();
        }

        for (SettingColor color : colors) {
            color.update();
        }

        for (Waypoint waypoint : Waypoints.get()) {
            waypoint.color.update();
        }

        if (mc.currentScreen instanceof WidgetScreen) {
            for (SettingGroup group : GuiThemes.get().settings) {
                for (Setting<?> setting : group) {
                    if (setting instanceof ColorSetting) ((SettingColor) setting.get()).update();
                }
            }
        }

        for (Runnable listener : listeners) listener.run();
    }
}
