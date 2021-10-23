package mathax.legacy.client.systems.modules.render.hud.modules;

import mathax.legacy.client.renderer.GL;
import mathax.legacy.client.renderer.Renderer2D;
import mathax.legacy.client.settings.*;
import mathax.legacy.client.systems.modules.render.hud.HUD;
import mathax.legacy.client.systems.modules.render.hud.HUDElement;
import mathax.legacy.client.systems.modules.render.hud.HUDRenderer;
import mathax.legacy.client.utils.render.color.Color;
import net.minecraft.util.Identifier;

public class BigRatHUD extends HUDElement {
    private static final Identifier BIG_RAT = new Identifier("mathaxlegacy", "textures/big-rat.png");
    private final Color TEXTURE_COLOR = new Color(255, 255, 255, 255);

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Integer> width = sgGeneral.add(new IntSetting.Builder()
        .name("width")
        .description("The width of the image.")
        .defaultValue(225)
        .min(1)
        .sliderRange(1, 1800)
        .build()
    );

    private final Setting<Integer> height = sgGeneral.add(new IntSetting.Builder()
        .name("height")
        .description("The height of the image.")
        .defaultValue(150)
        .min(1)
        .sliderRange(1, 1200)
        .build()
    );

    public BigRatHUD(HUD hud) {
        super(hud, "big-rat", "Displays a BIG RAT.", false);
    }

    @Override
    public void update(HUDRenderer renderer) {
        box.setSize(width.get(), height.get());
    }

    @Override
    public void render(HUDRenderer renderer) {
        GL.bindTexture(BIG_RAT);
        Renderer2D.TEXTURE.begin();
        Renderer2D.TEXTURE.texQuad(box.getX(), box.getY(), box.width, box.height, TEXTURE_COLOR);
        Renderer2D.TEXTURE.render(null);
    }
}
