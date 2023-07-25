package envy.client.systems.modules.combat;

import envy.client.eventbus.EventHandler;
import envy.client.events.game.OpenScreenEvent;
import envy.client.settings.BoolSetting;
import envy.client.settings.Setting;
import envy.client.settings.SettingGroup;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.item.Items;

public class Suicide extends Module {
    public Suicide() {super(Categories.Combat, Items.BEDROCK, "Suicide", "Speeeeeeeed");}
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Boolean> offhand = sgGeneral.add(new BoolSetting.Builder()
        .name("Offhand")
        .description("Doesn't hold totem.")
        .defaultValue(true)
        .build()
    );

    public final Setting<Boolean> disableDeath = sgGeneral.add(new BoolSetting.Builder()
        .name("Disable On Death")
        .description("Disables the module on death.")
        .defaultValue(true)
        .build()
    );

    @EventHandler(priority = 6969)
    private void onDeath(OpenScreenEvent event) {
        if (event.screen instanceof DeathScreen && disableDeath.get()) {
            toggle();
            info("died");
        }
    }
}

