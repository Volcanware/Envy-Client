package mathax.client.legacy.systems.modules.combat;

import mathax.client.legacy.bus.EventHandler;
import mathax.client.legacy.events.world.TickEvent;
import mathax.client.legacy.systems.modules.Categories;
import mathax.client.legacy.systems.modules.Module;
import mathax.client.legacy.systems.modules.Modules;
import mathax.client.legacy.utils.player.ChatUtils;
import net.minecraft.util.Formatting;

public class BedAuraTwo extends Module {

    public BedAuraTwo() {
        super(Categories.Combat, "bed-aura-two", "Automatically places and explodes beds in the Nether and End.");
    }

    @Override
    public void onActivate() {
        ChatUtils.info("Bed Aura Two", "Disabled " + Formatting.WHITE + "Bed Aura" + Formatting.GRAY + "...");
        if (Modules.get().isActive(BedAura.class)) Modules.get().get(BedAura.class).toggle();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (Modules.get().isActive(BedAura.class)) {
            ChatUtils.info("Bed Aura Two", "Disabled because " + Formatting.WHITE + "Bed Aura" + Formatting.GRAY + " is active...");
            toggle();
        }
    }
}
