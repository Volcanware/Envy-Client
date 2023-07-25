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

public class TNTHUD extends HudElement {
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

    public TNTHUD(HUD hud) {
        super(hud, "TNT", "Displays the amount of TNT in your inventory.", true);
    }

    @Override
    public void update(HudRenderer renderer) {
        box.setSize(16 * scale.get(), 16 * scale.get());
    }

    @Override
    public void render(HudRenderer renderer) {
        double x = box.getX();
        double y = box.getY();

        if (isInEditor()) RenderUtils.drawItem(Items.TNT.getDefaultStack(), (int) x, (int) y, scale.get(), true);
        else if (InvUtils.find(Items.TNT).count() > 0) RenderUtils.drawItem(new ItemStack(Items.TNT, InvUtils.find(Items.TNT).count()), (int) x, (int) y, scale.get(), true);
    }
}
