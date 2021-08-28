package mathax.client.legacy.systems.modules.combat;

import mathax.client.legacy.bus.EventHandler;
import mathax.client.legacy.events.world.TickEvent;
import mathax.client.legacy.systems.modules.Categories;
import mathax.client.legacy.systems.modules.Module;
import mathax.client.legacy.systems.modules.Modules;
import mathax.client.legacy.utils.player.ChatUtils;
import net.minecraft.util.Formatting;

public class BedAuraPlus extends Module {

    public BedAuraPlus() {
        super(Categories.Combat, "bed-aura-two", "Automatically places and explodes beds in the Nether and End.");
    }

    @Override
    public void onActivate() {
        /*if (Modules.get().isActive(BedAura.class)) {
            ChatUtils.error("Bed Aura+", "Bed Aura was enabled while enabling Bed Aura+, disabling Bed Aura...");
            Modules.get().get(BedAura.class).toggle();
        }*/
    }
}
