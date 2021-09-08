package mathax.client.legacy.systems.modules.render.hud.modules;

import mathax.client.legacy.MatHaxClientLegacy;
import mathax.client.legacy.renderer.GL;
import mathax.client.legacy.renderer.Renderer2D;
import mathax.client.legacy.settings.*;
import mathax.client.legacy.systems.modules.render.hud.HUD;
import mathax.client.legacy.systems.modules.render.hud.HUDRenderer;
import mathax.client.legacy.systems.modules.render.hud.TripleTextHUDElement;
import mathax.client.legacy.utils.Utils;
import mathax.client.legacy.Version;
import mathax.client.legacy.utils.network.Http;
import mathax.client.legacy.utils.render.color.Color;
import net.minecraft.util.Identifier;

public class WatermarkHUD extends TripleTextHUDElement {
    private final Color visiblityColor = new Color(255, 255, 255, 255);
    private static final Identifier mathaxTexture = new Identifier("mathaxlegacy", "textures/icons/icon.png");

    private String newUpdateString = "";
    private static String space = "    ";

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

    public String getEnd() {
        return getNewUpdate();
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
            renderer.text(getEnd(), x + textWidth, y, hud.primaryColor.get());
        } else if (mode.get() == Mode.Icon) {
            double width = 32 * scale.get();
            double height = 32 * scale.get();
            box.setSize(width,  height);
        } else {
            double spaceWidth = renderer.textWidth(space);
            double textWidth = spaceWidth + renderer.textWidth(getLeft()) + renderer.textWidth(getRight()) + renderer.textWidth(getEnd());
            double width = renderer.textHeight();
            double height = renderer.textHeight();
            box.setSize(width + textWidth - spaceWidth + 1, height);
            double x = box.getX();
            double y = box.getY();
            renderer.text(getLeft(), x + spaceWidth, y, hud.primaryColor.get());
            renderer.text(getRight(), x + spaceWidth + renderer.textWidth(getLeft()), y, hud.secondaryColor.get());
            renderer.text(getEnd(), x + textWidth - renderer.textWidth(getEnd()), y, hud.primaryColor.get());
        }
    }

    @Override
    public void render(HUDRenderer renderer) {
        double x = 0;
        double y = 0;
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

        GL.bindTexture(mathaxTexture);
        Renderer2D.TEXTURE.begin();
        Renderer2D.TEXTURE.texQuad(x, y, w, h, visiblityColor);
        Renderer2D.TEXTURE.render(null);
    }

    public String getNewUpdate() {
        if (Utils.didntCheckForLatestVersion) {
            Utils.didntCheckForLatestVersion = false;
            String apiLatestVer = Http.get(MatHaxClientLegacy.URL + "Version/Legacy/1-17-1").sendString();
            String processedApiLatestVer = apiLatestVer.replace("\n", "");
            if (processedApiLatestVer == null) {
                newUpdateString = " [Could not get Latest Version]";
                return newUpdateString;
            }
            Version latestVer = new Version(processedApiLatestVer);
            Version currentVer = new Version(Version.get());
            if (latestVer.isHigherThan(currentVer)) {
                newUpdateString = " [Outdated | Latest Version: v" + latestVer + "]";
            } else {
                newUpdateString = "";
            }
        }
        return newUpdateString;
    }

    public enum Mode {
        Text,
        Icon,
        Both
    }
}
