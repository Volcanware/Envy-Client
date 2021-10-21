package mathax.legacy.client.systems.modules.render.hud.modules;

import mathax.legacy.client.settings.BoolSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
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

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> accurate = sgGeneral.add(new BoolSetting.Builder()
        .name("accurate")
        .description("Shows position with decimal points.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> oppositeDimension = sgGeneral.add(new BoolSetting.Builder()
        .name("opposite-dimension")
        .description("Displays the coordinates of the opposite dimension (Nether or Overworld).")
        .defaultValue(true)
        .build()
    );

    public PositionHUD(HUD hud) {
        super(hud, "position", "Displays your coordinates in the world", true);
    }

    @Override
    public void update(HUDRenderer renderer) {
        left1Width = renderer.textWidth(left1);
        left2 = null;
        right2 = null;

        double height = renderer.textHeight();
        if (oppositeDimension.get()) height = height * 2 + 2;

        if (isInEditor()) {
            right1 = accurate.get() ? "0,0 0,0 0,0" : "0, 0, 0";
            if (oppositeDimension.get()) {
                left2 = "Editor XYZ ";
                right2 = right1;
            }

            double widthEditor = left1Width + renderer.textWidth(right1);

            if (left2 != null) {
                left2Width = renderer.textWidth(left2);
                widthEditor = Math.max(widthEditor, left2Width + renderer.textWidth(right2));
            }

            box.setSize(widthEditor, height);
            return;
        }

        Freecam freecam = Modules.get().get(Freecam.class);
        double x, y, z;

        if (accurate.get()) {
            x = freecam.isActive() ? mc.gameRenderer.getCamera().getPos().x : mc.player.getX();
            y = freecam.isActive() ? mc.gameRenderer.getCamera().getPos().y - mc.player.getEyeHeight(mc.player.getPose()) : mc.player.getY();
            z = freecam.isActive() ? mc.gameRenderer.getCamera().getPos().z : mc.player.getZ();

            right1 = String.format("%.1f %.1f %.1f", x, y, z);
        } else {
            x = freecam.isActive() ? mc.gameRenderer.getCamera().getBlockPos().getX() : mc.player.getBlockX();
            y = freecam.isActive() ? mc.gameRenderer.getCamera().getBlockPos().getY() : mc.player.getBlockY();
            z = freecam.isActive() ? mc.gameRenderer.getCamera().getBlockPos().getZ() : mc.player.getBlockZ();

            right1 = String.format("%d %d %d", (int) x, (int) y, (int) z);
        }

        if (oppositeDimension.get()) {
            switch (PlayerUtils.getDimension()) {
                case Overworld -> {
                    left2 = "Nether XYZ ";
                    right2 = accurate.get() ? String.format("%.1f %.1f %.1f", x / 8.0, y, z / 8.0) : String.format("%d %d %d", (int) x / 8, (int) y, (int) z / 8);
                }
                case Nether -> {
                    left2 = "Overworld XYZ ";
                    right2 = accurate.get() ? String.format("%.1f %.1f %.1f", x * 8.0, y, z * 8.0) : String.format("%d %d %d", (int) x * 8, (int) y, (int) z / 8);
                }
            }
        }

        double width = left1Width + renderer.textWidth(right1);

        if (left2 != null) {
            left2Width = renderer.textWidth(left2);
            width = Math.max(width, left2Width + renderer.textWidth(right2));
        }

        box.setSize(width, height);
    }

    @Override
    public void render(HUDRenderer renderer) {
        double x = box.getX();
        double y = box.getY();

        double xOffset = box.alignX(left1Width + renderer.textWidth(right1));
        double yOffset = oppositeDimension.get() ? renderer.textHeight() + 2 : 0;

        if (left2 != null) {
            renderer.text(left2, x, y, hud.primaryColor.get());
            renderer.text(right2, x + left2Width, y, hud.secondaryColor.get());
        }

        renderer.text(left1, x + xOffset, y + yOffset, hud.primaryColor.get());
        renderer.text(right1, x + xOffset + left1Width, y + yOffset, hud.secondaryColor.get());
    }
}
