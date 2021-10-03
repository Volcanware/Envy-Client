package mathax.legacy.client.systems.modules.player;

import mathax.legacy.client.events.game.OpenScreenEvent;
import mathax.legacy.client.systems.modules.render.WaypointsModule;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.systems.modules.Modules;
import mathax.legacy.client.bus.EventHandler;
import mathax.legacy.client.bus.EventPriority;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.item.Items;

public class AutoRespawn extends Module {
    public AutoRespawn() {
        super(Categories.Player, Items.COMMAND_BLOCK, "auto-respawn", "Automatically respawns after death.");
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onOpenScreenEvent(OpenScreenEvent event) {
        if (!(event.screen instanceof DeathScreen)) return;

        Modules.get().get(WaypointsModule.class).addDeath(mc.player.getPos());
        mc.player.requestRespawn();
        event.cancel();
    }
}
