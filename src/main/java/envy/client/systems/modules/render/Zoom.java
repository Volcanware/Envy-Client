package envy.client.systems.modules.render;

import envy.client.Envy;
import envy.client.eventbus.EventHandler;
import envy.client.events.mathax.MouseScrollEvent;
import envy.client.events.render.GetFovEvent;
import envy.client.events.render.Render3DEvent;
import envy.client.events.world.TickEvent;
import envy.client.settings.BoolSetting;
import envy.client.settings.DoubleSetting;
import envy.client.settings.Setting;
import envy.client.settings.SettingGroup;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import envy.client.utils.Utils;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;

public class Zoom extends Module {
    private boolean enabled;
    private boolean preCinematic;
    private double preMouseSensitivity;
    private double value;
    private double lastFov;
    private double time;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Double> zoom = sgGeneral.add(new DoubleSetting.Builder()
        .name("zoom")
        .description("How much to zoom.")
        .defaultValue(6)
        .min(1)
        .build()
    );

    private final Setting<Double> scrollSensitivity = sgGeneral.add(new DoubleSetting.Builder()
        .name("scroll-sensitivity")
        .description("Allows you to change zoom value using scroll wheel. 0 to disable.")
        .defaultValue(1)
        .min(0)
        .build()
    );

    private final Setting<Boolean> smooth = sgGeneral.add(new BoolSetting.Builder()
        .name("smooth")
        .description("Smooth transition.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> cinematic = sgGeneral.add(new BoolSetting.Builder()
        .name("cinematic")
        .description("Enables cinematic camera.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> renderHands = sgGeneral.add(new BoolSetting.Builder()
        .name("show-hands")
        .description("Whether or not to render your hands.")
        .defaultValue(false)
        .build()
    );

    public Zoom() {
        super(Categories.Render, Items.SPYGLASS, "zoom", "Zooms your view.");
        autoSubscribe = false;
    }

    @Override
    public boolean onActivate() { //zoom go brrrr
        if (!enabled) {
            preCinematic = mc.options.smoothCameraEnabled;
            preMouseSensitivity = mc.options.getMouseSensitivity().getValue();
            value = zoom.get();
            lastFov = mc.options.getFov().getValue();
            time = 0.001;

            Envy.EVENT_BUS.subscribe(this);
            enabled = true;
        }
        return false;
    }

    public void onStop() {
        mc.options.smoothCameraEnabled = preCinematic;
        mc.options.getMouseSensitivity().setValue(preMouseSensitivity);

        mc.worldRenderer.scheduleTerrainUpdate();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        mc.options.smoothCameraEnabled = cinematic.get();

        if (!cinematic.get()) mc.options.getMouseSensitivity().setValue(preMouseSensitivity / Math.max(value() * 0.5, 1));

        if (time == 0) {
            Envy.EVENT_BUS.unsubscribe(this);
            enabled = false;

            onStop();
        }
    }

    @EventHandler
    private void onMouseScroll(MouseScrollEvent event) {
        if (scrollSensitivity.get() > 0 && isActive()) {
            value += event.value * 0.25 * (scrollSensitivity.get() * value);

            if (value < 1) value = 1;

            event.cancel();
        }
    }

    @EventHandler
    private void onRender3D(Render3DEvent event) {
        if (!smooth.get()) {
            time = isActive() ? 1 : 0;
            return;
        }

        if (isActive()) time += event.frameTime * 5;
        else time -= event.frameTime * 5;

        time = Utils.clamp(time, 0, 1);
    }

    @EventHandler
    private void onGetFov(GetFovEvent event) {
        event.fov /= value();

        if (lastFov != event.fov) mc.worldRenderer.scheduleTerrainUpdate();

        lastFov = event.fov;
    }

    private double value() {
        return MathHelper.lerp(time < 0.5 ? 4 * time * time * time : 1 - Math.pow(-2 * time + 2, 3) / 2, 1, value);
    }

    public boolean renderHands() {
        return !isActive() || renderHands.get();
    }
}
