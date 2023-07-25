package envy.client.systems.hud.modules;

import envy.client.settings.DoubleSetting;
import envy.client.settings.Setting;
import envy.client.settings.SettingGroup;
import envy.client.systems.hud.HUD;
import envy.client.systems.hud.HudElement;
import envy.client.systems.hud.HudRenderer;
import envy.client.utils.player.InvUtils;
import envy.client.utils.render.RenderUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class CrystalHud extends HudElement {
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

    public CrystalHud(HUD hud) {
        super(hud, "crystal", "Displays the amount of crystals in your inventory.", true);
    }

    @Override
    public void update(HudRenderer renderer) {
        box.setSize(16 * scale.get(), 16 * scale.get());
    }

    @Override
    public void render(HudRenderer renderer) {
        double x = box.getX();
        double y = box.getY();

        if (isInEditor()) RenderUtils.drawItem(Items.END_CRYSTAL.getDefaultStack(), (int) x, (int) y, scale.get(), true);
        else if (InvUtils.find(Items.END_CRYSTAL).count() > 0) RenderUtils.drawItem(new ItemStack(Items.END_CRYSTAL, InvUtils.find(Items.END_CRYSTAL).count()), (int) x, (int) y, scale.get(), true);
    }
}
