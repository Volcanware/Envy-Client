package mathax.client.legacy.systems.modules.render.hud.modules;

import mathax.client.legacy.settings.*;
import mathax.client.legacy.systems.modules.render.hud.HUD;
import mathax.client.legacy.systems.modules.render.hud.HudElement;
import mathax.client.legacy.systems.modules.render.hud.HudRenderer;
import mathax.client.legacy.utils.render.color.SettingColor;
import net.minecraft.util.math.MathHelper;

public class CompassHud extends HudElement {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("type")
        .description("Which type of axis to show.")
        .defaultValue(Mode.Axis)
        .build()
    );

    private final Setting<SettingColor> northColor = sgGeneral.add(new ColorSetting.Builder()
        .name("north-color")
        .description("Color of North.")
        .defaultValue(new SettingColor(255, 0, 0))
        .build()
    );

    private final Setting<Double> scale = sgGeneral.add(new DoubleSetting.Builder()
        .name("scale")
        .description("The scale.")
        .defaultValue(2)
        .min(1)
        .sliderMin(1)
        .sliderMax(5)
        .build()
    );

    private double yaw, pitch;

    public CompassHud(HUD hud) {
        super(hud, "compass", "Displays a compass.");
    }

    @Override
    public void update(HudRenderer renderer) {
        if (!isInEditor()) pitch = mc.player.getPitch();
        else pitch = 90;

        pitch = MathHelper.clamp(pitch + 30, -90, 90);
        pitch = Math.toRadians(pitch);

        if (!isInEditor()) yaw = mc.player.getYaw();
        else yaw = 180;

        yaw = MathHelper.wrapDegrees(yaw);
        yaw = Math.toRadians(yaw);

        box.setSize(100 *  scale.get(), 100 *  scale.get());
    }

    @Override
    public void render(HudRenderer renderer) {
        double x = box.getX() + (box.width / 2);
        double y = box.getY() + (box.height / 2);

        for (Direction dir : Direction.values()) {
            String axis = mode.get() == Mode.Axis ? dir.getAlternate() : dir.name();

            renderer.text(axis, (x + getX(dir)) - (renderer.textWidth(axis) / 2), (y + getY(dir)) - (renderer.textHeight() / 2), dir == Direction.N ? northColor.get() : hud.primaryColor.get());
        }
    }

    private double getX(Direction dir) {
        return Math.sin(getPosOnCompass(dir)) * scale.get() * 40;
    }

    private double getY(Direction dir) {
        return Math.cos(getPosOnCompass(dir)) * Math.sin(pitch) * scale.get() * 40;
    }

    private double getPosOnCompass(Direction dir) {
        return yaw + dir.ordinal() * Math.PI / 2;
    }

    private enum Direction {
        N("Z-"),
        W("X-"),
        S("Z+"),
        E("X+");

        String alternate;

        Direction(String alternate) {
            this.alternate = alternate;
        }

        public String getAlternate() {
            return alternate;
        }
    }

    public enum Mode {
        Axis, Pole
    }
}
