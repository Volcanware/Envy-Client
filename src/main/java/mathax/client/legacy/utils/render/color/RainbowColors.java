package mathax.client.legacy.utils.render.color;

import mathax.client.legacy.MatHaxClientLegacy;
import mathax.client.legacy.events.world.TickEvent;
import mathax.client.legacy.gui.GuiThemes;
import mathax.client.legacy.gui.WidgetScreen;
import mathax.client.legacy.gui.tabs.builtin.ConfigTab;
import mathax.client.legacy.settings.ColorSetting;
import mathax.client.legacy.settings.Setting;
import mathax.client.legacy.settings.SettingGroup;
import mathax.client.legacy.systems.waypoints.Waypoint;
import mathax.client.legacy.systems.waypoints.Waypoints;
import mathax.client.legacy.utils.misc.UnorderedArrayList;
import mathax.client.legacy.bus.EventHandler;

import java.util.List;

import static mathax.client.legacy.utils.Utils.mc;

public class RainbowColors {

    public static final RainbowColor GLOBAL = new RainbowColor().setSpeed(ConfigTab.rainbowSpeed.get() / 100);

    private static final List<Setting<SettingColor>> colorSettings = new UnorderedArrayList<>();
    private static final List<SettingColor> colors = new UnorderedArrayList<>();
    private static final List<Runnable> listeners = new UnorderedArrayList<>();

    public static void init() {
        MatHaxClientLegacy.EVENT_BUS.subscribe(RainbowColors.class);
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
