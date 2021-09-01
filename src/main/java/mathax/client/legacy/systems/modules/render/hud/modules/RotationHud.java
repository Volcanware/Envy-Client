package mathax.client.legacy.systems.modules.render.hud.modules;

import mathax.client.legacy.systems.modules.render.hud.HUD;
import mathax.client.legacy.systems.modules.render.hud.TripleTextHudElement;
import mathax.client.legacy.utils.misc.HorizontalDirection;

public class RotationHud extends TripleTextHudElement {
    public RotationHud(HUD hud) {
        super(hud, "rotation", "Displays your rotation.");
    }

    @Override
    protected String getLeft() {
        return "";
    }

    @Override
    protected String getRight() {
        float yaw = mc.gameRenderer.getCamera().getYaw() % 360;
        if (yaw < 0) yaw += 360;
        if (yaw > 180) yaw -= 360;

        float pitch = mc.gameRenderer.getCamera().getPitch() % 360;
        if (pitch < 0) pitch += 360;
        if (pitch > 180) pitch -= 360;

        HorizontalDirection dir = HorizontalDirection.get(mc.gameRenderer.getCamera().getYaw());
        setLeft(String.format("%s %s ", dir.name, dir.axis));

        return String.format("(%.1f, %.1f)", yaw, pitch);
    }

    @Override
    protected String getEnd() {
        return "";
    }
}
