package mathax.legacy.client.systems.modules.render.hud.modules;

import mathax.legacy.client.systems.modules.Modules;
import mathax.legacy.client.systems.modules.render.Freecam;
import mathax.legacy.client.systems.modules.render.hud.HUD;
import mathax.legacy.client.systems.modules.render.hud.HUDElement;
import mathax.legacy.client.systems.modules.render.hud.HUDRenderer;
import mathax.legacy.client.utils.player.PlayerUtils;

public class PositionHUD extends HUDElement {
    private final String left1 = "XYZ ";
    private double left1Width;
    private String right1;

    private String left2;
    private double left2Width;
    private String right2;

    public PositionHUD(HUD hud) {
        super(hud, "position", "Displays your coordinates in the world", true);
    }

    @Override
    public void update(HUDRenderer renderer) {
        left1Width = renderer.textWidth(left1);
        left2 = null;

        if (isInEditor()) {
            right1 = "0,0 0,0 0,0";
            box.setSize(left1Width + renderer.textWidth(right1), renderer.textHeight() * 2 + 2);
            return;
        }

        Freecam freecam = Modules.get().get(Freecam.class);

        double x1 = freecam.isActive() ? mc.gameRenderer.getCamera().getPos().x : mc.player.getX();
        double y1 = freecam.isActive() ? mc.gameRenderer.getCamera().getPos().y - mc.player.getEyeHeight(mc.player.getPose()) : mc.player.getY();
        double z1 = freecam.isActive() ? mc.gameRenderer.getCamera().getPos().z : mc.player.getZ();

        right1 = String.format("%.1f %.1f %.1f", x1, y1, z1);

        switch (PlayerUtils.getDimension()) {
            case Overworld -> {
                left2 = "Nether XYZ ";
                right2 = String.format("%.1f %.1f %.1f", x1 / 8.0, y1, z1 / 8.0);
            }
            case Nether -> {
                left2 = "Overworld XYZ ";
                right2 = String.format("%.1f %.1f %.1f", x1 * 8.0, y1, z1 * 8.0);
            }
        }

        double width = left1Width + renderer.textWidth(right1);

        if (left2 != null) {
            left2Width = renderer.textWidth(left2);
            width = Math.max(width, left2Width + renderer.textWidth(right2));
        }

        box.setSize(width, renderer.textHeight() * 2 + 2);
    }

    @Override
    public void render(HUDRenderer renderer) {
        double x = box.getX();
        double y = box.getY();

        if (left2 != null) {
            renderer.text(left2, x, y, hud.primaryColor.get());
            renderer.text(right2, x + left2Width, y, hud.secondaryColor.get());
        }

        double xOffset = box.alignX(left1Width + renderer.textWidth(right1));
        double yOffset = renderer.textHeight() + 2;

        renderer.text(left1, x + xOffset, y + yOffset, hud.primaryColor.get());
        renderer.text(right1, x + xOffset + left1Width, y + yOffset, hud.secondaryColor.get());
    }
}
