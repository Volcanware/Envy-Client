package mathax.client.legacy.systems.modules.combat;

import mathax.client.legacy.bus.EventHandler;
import mathax.client.legacy.events.world.TickEvent;
import mathax.client.legacy.systems.modules.Categories;
import mathax.client.legacy.systems.modules.Module;
import mathax.client.legacy.systems.modules.Modules;
import mathax.client.legacy.utils.player.ChatUtils;
import net.minecraft.util.Formatting;

public class CrystalAuraTwo extends Module {

    public CrystalAuraTwo() {
        super(Categories.Combat, "crystal-aura-two", "Automatically places and attacks crystals.");
    }

    @Override
    public void onActivate() {
        ChatUtils.info("Crystal Aura Two", "Disabled " + Formatting.WHITE + "Crystal Aura" + Formatting.GRAY + "...");
        if (Modules.get().isActive(CrystalAura.class)) Modules.get().get(CrystalAura.class).toggle();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (Modules.get().isActive(CrystalAura.class)) {
            ChatUtils.info("Crystal Aura Two", "Disabled because " + Formatting.WHITE + "Crystal Aura" + Formatting.GRAY + " is active...");
            toggle();
        }
    }
}
