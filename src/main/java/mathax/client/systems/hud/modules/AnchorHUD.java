package mathax.client.systems.hud.modules;

import mathax.client.settings.DoubleSetting;
import mathax.client.settings.Setting;
import mathax.client.settings.SettingGroup;
import mathax.client.utils.player.InvUtils;
import mathax.client.utils.render.RenderUtils;
import mathax.client.systems.hud.HUD;
import mathax.client.systems.hud.HudElement;
import mathax.client.systems.hud.HudRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class AnchorHUD extends HudElement {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Double> scale = sgGeneral.add(new DoubleSetting.Builder()
        .name("scale")
        .description("The scale.")
        .defaultValue(2)
        .min(1)
        .sliderRange(1, 5)
        .build()
    );

    public AnchorHUD(HUD hud) {
        super(hud, "Anchor", "Displays the amount of Respawn Anchor's in your inventory.", true);
    }

    @Override
    public void update(HudRenderer renderer) {
        box.setSize(16 * scale.get(), 16 * scale.get());
    }

    @Override
    public void render(HudRenderer renderer) {
        double x = box.getX();
        double y = box.getY();

        if (isInEditor()) RenderUtils.drawItem(renderer.context, Items.RESPAWN_ANCHOR.getDefaultStack(), (int) x, (int) y, scale.get().floatValue(), true);
        else if (InvUtils.find(Items.RESPAWN_ANCHOR).count() > 0) RenderUtils.drawItem(renderer.context, new ItemStack(Items.RESPAWN_ANCHOR, InvUtils.find(Items.RESPAWN_ANCHOR).count()), (int) x, (int) y, scale.get().floatValue(), true);
    }
}
