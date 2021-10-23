package mathax.legacy.client.systems.modules.render.hud.modules;

import mathax.legacy.client.settings.DoubleSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.systems.modules.render.hud.HUD;
import mathax.legacy.client.systems.modules.render.hud.HUDElement;
import mathax.legacy.client.systems.modules.render.hud.HUDRenderer;
import mathax.legacy.client.utils.player.InvUtils;
import mathax.legacy.client.utils.render.RenderUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class CrystalHUD extends HUDElement {
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

    public CrystalHUD(HUD hud) {
        super(hud, "crystal", "Displays the amount of crystals in your inventory.", true);
    }

    @Override
    public void update(HUDRenderer renderer) {
        box.setSize(16 * scale.get(), 16 * scale.get());
    }

    @Override
    public void render(HUDRenderer renderer) {
        double x = box.getX();
        double y = box.getY();

        if (isInEditor()) {
            RenderUtils.drawItem(Items.END_CRYSTAL.getDefaultStack(), (int) x, (int) y, scale.get(), true);
        } else if (InvUtils.find(Items.END_CRYSTAL).getCount() > 0) {
            RenderUtils.drawItem(new ItemStack(Items.END_CRYSTAL, InvUtils.find(Items.END_CRYSTAL).getCount()), (int) x, (int) y, scale.get(), true);
        }
    }
}
