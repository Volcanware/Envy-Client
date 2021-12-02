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

    private final Setting<Double> scale = sgGeneral.add(new DoubleSetting.Builder()
        .name("scale")
        .description("The scale of big rat.")
        .defaultValue(0.25)
        .min(0.001)
        .sliderRange(0.1, 2)
        .build()
    );

    public BigRatHUD(HUD hud) {
        super(hud, "big-rat", "Displays a BIG RAT.", false);
    }

    @Override
    public void update(HUDRenderer renderer) {
        box.setSize(900 * scale.get(), 600 * scale.get());
    }

    @Override
    public void render(HUDRenderer renderer) {
        GL.bindTexture(BIG_RAT);
        Renderer2D.TEXTURE.begin();
        Renderer2D.TEXTURE.texQuad(box.getX(), box.getY(), box.width, box.height, TEXTURE_COLOR);
        Renderer2D.TEXTURE.render(null);
    }
}
