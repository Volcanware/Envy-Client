package envy.client.systems.modules.render;

import envy.client.eventbus.EventHandler;
import envy.client.settings.IntSetting;
import envy.client.settings.Setting;
import envy.client.settings.SettingGroup;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import net.minecraft.item.Items;

public class Australia extends Module {

    public Australia() {
        super(Categories.Render, Items.AIR, "australia", "Makes you look like you're in Australia.");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> FOV = sgGeneral.add(new IntSetting.Builder()
        .name("FOV")
        .description("FOV to set back to on disable.")
        .defaultValue(110)
        .min(30)
        .sliderMax(110)
        .build()
    );

    @EventHandler
    public boolean onActivate() {
        mc.options.getFov().setValue(260);
        info("You are now in Australia.");
        return false; //hes prob gay
    }

    @EventHandler
    public void onDeactivate() {
        mc.options.getFov().setValue(FOV.get());
        info("You have been deported from Australia.");
    }
}
