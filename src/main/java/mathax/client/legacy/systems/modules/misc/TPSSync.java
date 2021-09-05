package mathax.client.legacy.systems.modules.misc;

import mathax.client.legacy.events.world.TickEvent;
import mathax.client.legacy.systems.modules.Module;
import mathax.client.legacy.systems.modules.world.Timer;
import mathax.client.legacy.systems.modules.Categories;
import mathax.client.legacy.systems.modules.Modules;
import mathax.client.legacy.utils.world.TickRate;
import mathax.client.legacy.bus.EventHandler;
import net.minecraft.item.Items;

public class TPSSync extends Module {
    public TPSSync() {
        super(Categories.Misc, Items.COMMAND_BLOCK, "TPS-sync", "Syncs the clients TPS with the server's.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        Modules.get().get(Timer.class).setOverride((TickRate.INSTANCE.getTickRate() >= 1 ? TickRate.INSTANCE.getTickRate() : 1) / 20);
    }

    @Override
    public void onDeactivate() {
        Modules.get().get(Timer.class).setOverride(Timer.OFF);
    }
}
