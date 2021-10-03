package mathax.legacy.client.systems.modules.misc;

import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.systems.modules.world.Timer;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Modules;
import mathax.legacy.client.utils.world.TickRate;
import mathax.legacy.client.eventbus.EventHandler;
import net.minecraft.item.Items;

public class TPSSync extends Module {
    public TPSSync() {
        super(Categories.Misc, Items.COMMAND_BLOCK, "TPS-sync", "Syncs the clients TPS with the server TPS.");
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
