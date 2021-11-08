package mathax.legacy.client.systems.modules.render.hud.modules;

import mathax.legacy.client.renderer.GL;
import mathax.legacy.client.renderer.Renderer2D;
import mathax.legacy.client.systems.modules.render.hud.HUD;
import mathax.legacy.client.systems.modules.render.hud.HUDRenderer;
import mathax.legacy.client.systems.modules.render.hud.TripleTextHUDElement;
import mathax.legacy.client.utils.UpdateChecker;
import mathax.legacy.client.utils.Version;
import mathax.legacy.client.utils.render.color.Color;
import mathax.legacy.client.settings.DoubleSetting;
import mathax.legacy.client.settings.EnumSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import net.minecraft.util.Identifier;

public class WatermarkHUD extends TripleTextHUDElement {
    private static final Identifier MATHAX_LOGO = new Identifier("mathaxlegacy", "textures/icons/icon.png");
    private final Color TEXTURE_COLOR = new Color(255, 255, 255, 255);

    private String versionString;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("Determines what watermark style to use.")
        .defaultValue(Mode.Both)
        .build()
    );

    private final Setting<Double> scale = sgGeneral.add(new DoubleSetting.Builder()
        .name("scale")
        .description("The scale of the icon.")
        .defaultValue(1)
        .min(1)
        .sliderMin(1)
        .sliderMax(5)
        .visible(() -> mode.get() == Mode.Icon)
        .build()
    );

    public WatermarkHUD(HUD hud) {
        super(hud, "watermark", "Displays a MatHax Legacy watermark.", true);
    }

    protected String getLeft() {
        return "MatHax Legacy ";
    }

    protected String getRight() {
        return Version.getStylized();
    }

    protected String getEnd() {
        return checkForUpdate();
    }

    @Override
    public void update(HUDRenderer renderer) {
        if (mode.get() == Mode.Text) {
            double textWidth = renderer.textWidth(getLeft()) + renderer.textWidth(getRight()) + renderer.textWidth(getEnd());
            box.setSize(textWidth, renderer.textHeight());

            double x = box.getX();
            double y = box.getY();

            renderer.text(getLeft(), x, y, hud.primaryColor.get());
            renderer.text(getRight(), x + renderer.textWidth(getLeft()), y, hud.secondaryColor.get());
            renderer.text(getEnd(), x + textWidth - renderer.textWidth(getEnd()), y, hud.primaryColor.get());
        } else if (mode.get() == Mode.Icon) {
            double width = renderer.textHeight() * scale.get();
            double height = renderer.textHeight() * scale.get();

            box.setSize(width,  height);
        } else {
            double textWidth = renderer.textWidth(getLeft()) + renderer.textWidth(getRight()) + renderer.textWidth(getEnd());
            double width = renderer.textHeight();
            double height = renderer.textHeight();

            box.setSize(width + textWidth, height);

            double x = box.getX();
            double y = box.getY();

            renderer.text(getLeft(), x + renderer.textHeight(), y, hud.primaryColor.get());
            renderer.text(getRight(), x + renderer.textHeight() + renderer.textWidth(getLeft()), y, hud.secondaryColor.get());
            renderer.text(getEnd(), x + renderer.textHeight() + textWidth - renderer.textWidth(getEnd()), y, hud.primaryColor.get());
        }
    }

    @Override
    public void render(HUDRenderer renderer) {
        double x;
        double y;

        switch (mode.get()) {
            case Icon:
                x = box.getX();
                y = box.getY();

                drawIcon((int) x, (int) y, 0);
            case Both:
                x = box.getX();
                y = box.getY();

                double textWidth = renderer.textWidth(getLeft()) + renderer.textWidth(getRight()) + renderer.textWidth(getEnd());
                drawIcon((int) x, (int) y, (int) textWidth);
        }
    }

    private void drawIcon(int x, int y, int textWidth) {
        int w = 0;

        switch (mode.get()) {
            case Icon -> w = (int) box.width;
            case Both -> w = (int) box.width - textWidth;
        }

        int h = (int) box.height;

        GL.bindTexture(MATHAX_LOGO);
        Renderer2D.TEXTURE.begin();
        Renderer2D.TEXTURE.texQuad(x, y, w, h, TEXTURE_COLOR);
        Renderer2D.TEXTURE.render(null);
    }

    public String checkForUpdate() {
        if (versionString == null) versionString = "";

        if (UpdateChecker.didntCheckForLatest) {
            UpdateChecker.didntCheckForLatest = false;

            switch (UpdateChecker.checkLatest()) {
                case Cant_Check -> versionString = " [Could not get Latest Version]";
                case Newer_Found -> versionString = " [Outdated | Latest Version: v" + UpdateChecker.getLatest() + "]";
                default -> versionString = "";
            }
        }

        return versionString;
    }

    public enum Mode {
        Text,
        Icon,
        Both
    }
}
