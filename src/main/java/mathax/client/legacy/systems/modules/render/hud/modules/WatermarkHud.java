package mathax.client.legacy.systems.modules.render.hud.modules;

import mathax.client.legacy.MatHaxClientLegacy;
import mathax.client.legacy.renderer.GL;
import mathax.client.legacy.renderer.Renderer2D;
import mathax.client.legacy.settings.*;
import mathax.client.legacy.systems.modules.render.hud.HUD;
import mathax.client.legacy.systems.modules.render.hud.HudRenderer;
import mathax.client.legacy.systems.modules.render.hud.TripleTextHudElement;
import mathax.client.legacy.utils.Utils;
import mathax.client.legacy.utils.misc.Version;
import mathax.client.legacy.utils.network.Http;
import mathax.client.legacy.utils.render.color.Color;
import net.minecraft.util.Identifier;

public class WatermarkHud extends TripleTextHudElement {
    private static final Identifier mathaxTexture = new Identifier("mathaxlegacy", "textures/logo/big-text.png");

    private String newUpdateString = "";

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("Determines what watermark style to use.")
        .defaultValue(Mode.Image)
        .build()
    );

    public WatermarkHud(HUD hud) {
        super(hud, "watermark", "Displays a MatHax Client Legacy watermark.", true);
    }

    protected String getLeft() {
        return "MatHax Client Legacy ";
    }

    protected String getRight() {
        return MatHaxClientLegacy.clientVersionWithV;
    }

    public String getEnd() {
        return getNewUpdate();
    }

    @Override
    public void update(HudRenderer renderer) {
        if (mode.get() == Mode.Text) {
            double textWidth = renderer.textWidth(getLeft()) + renderer.textWidth(getRight());
            box.setSize(textWidth + renderer.textWidth(getEnd()), renderer.textHeight());
            double x = box.getX();
            double y = box.getY();
            renderer.text(getLeft(), x, y, hud.primaryColor.get());
            renderer.text(getRight(), x + renderer.textWidth(getLeft()), y, hud.secondaryColor.get());
            renderer.text(getEnd(), x + textWidth, y, hud.primaryColor.get());
        } else {
            double width = 1392 / 6;
            double height = 128 / 6;
            double height2 = height / 4.5;
            double textWidth = 4 + renderer.textWidth(getRight()) + renderer.textWidth(getEnd());
            box.setSize(width + textWidth,  height);
            double x = box.getX() + width;
            double y = box.getY() + height2;
            renderer.text(" " + getRight(), 4 + x, y, hud.secondaryColor.get());
            renderer.text(getEnd(), x + 1 + renderer.textWidth(getRight()), y, hud.primaryColor.get());
        }
    }

    @Override
    public void render(HudRenderer renderer) {
        if (mode.get() == Mode.Image) {
            double x = box.getX();
            double y = box.getY();
            double textWidth = renderer.textWidth(getRight()) + renderer.textWidth(getEnd());
            drawBackground((int) x, (int) textWidth, (int) y);
        }
    }

    private Color textureColor = new Color(255, 255, 255, 255);

    private void drawBackground(int x, int textWidth, int y) {
        int w = (int) box.width - textWidth;
        int h = (int) box.height;

        GL.bindTexture(mathaxTexture);
        Renderer2D.TEXTURE.begin();
        Renderer2D.TEXTURE.texQuad(x, y, w, h, textureColor);
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
            Version currentVer = new Version(MatHaxClientLegacy.versionNumber);
            if (latestVer.isHigherThan(currentVer)) {
                newUpdateString = " [Outdated | Latest Version: v" + latestVer + "]";
            } else {
                newUpdateString = "";
            }
        }
        return newUpdateString;
    }

    public enum Mode {
        Image,
        Text
    }
}
