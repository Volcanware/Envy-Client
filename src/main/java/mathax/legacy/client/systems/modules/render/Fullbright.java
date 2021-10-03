package mathax.legacy.client.systems.modules.render;

import mathax.legacy.client.MatHaxLegacy;
import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.settings.EnumSetting;
import mathax.legacy.client.settings.IntSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.systems.modules.Modules;
import mathax.legacy.client.bus.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;

public class Fullbright extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("The mode to use for Fullbright.")
        .defaultValue(Mode.Gamma)
        .onChanged(mode -> {
            if (mode == Mode.Luminance) {
                mc.options.gamma = StaticListener.prevGamma;
            }
        })
        .build()
    );

    private final Setting<Integer> minimumLightLevel = sgGeneral.add(new IntSetting.Builder()
        .name("minimum-light-level")
        .description("Minimum light level when using Luminance mode.")
        .visible(() -> mode.get() == Mode.Gamma)
        .defaultValue(8)
        .onChanged(integer -> {
            if (mc.worldRenderer != null) mc.worldRenderer.reload();
        })
        .min(0)
        .max(15)
        .sliderMax(15)
        .build()
    );

    public Fullbright() {
        super(Categories.Render, Items.BEACON, "fullbright", "Allows you to see at any light level.");

        MatHaxLegacy.EVENT_BUS.subscribe(StaticListener.class);
    }

    @Override
    public void onActivate() {
        enable();

        if (mode.get() == Mode.Luminance) mc.worldRenderer.reload();
    }

    @Override
    public void onDeactivate() {
        disable();

        if (mode.get() == Mode.Luminance) mc.worldRenderer.reload();
    }

    public int getMinimumLightLevel() {
        if (!isActive() || mode.get() != Mode.Luminance) return 0;
        return minimumLightLevel.get();
    }

    public static boolean isEnabled() {
        return StaticListener.timesEnabled > 0;
    }

    public static void enable() {
        StaticListener.timesEnabled++;
    }

    public static void disable() {
        StaticListener.timesEnabled--;
    }

    private static class StaticListener {
        private static final MinecraftClient mc = MinecraftClient.getInstance();
        private static final Fullbright fullbright = Modules.get().get(Fullbright.class);

        private static int timesEnabled;
        private static int lastTimesEnabled;

        private static double prevGamma = mc.options.gamma;

        @EventHandler
        private static void onTick(TickEvent.Post event) {
            if (timesEnabled > 0 && lastTimesEnabled == 0) {
                prevGamma = mc.options.gamma;
            } else if (timesEnabled == 0 && lastTimesEnabled > 0) {
                if (fullbright.mode.get() == Mode.Gamma) {
                    mc.options.gamma = prevGamma == 16 ? 1 : prevGamma;
                }
            }

            if (timesEnabled > 0) {
                if (fullbright.mode.get() == Mode.Gamma) {
                    mc.options.gamma = 16;
                }
            }

            lastTimesEnabled = timesEnabled;
        }
    }

    public enum Mode {
        Gamma,
        Luminance
    }
}
