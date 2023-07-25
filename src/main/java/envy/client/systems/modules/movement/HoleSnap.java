package envy.client.systems.modules.movement;

import envy.client.eventbus.EventHandler;
import envy.client.events.world.TickEvent;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import envy.client.utils.player.PlayerUtils;
import net.minecraft.item.Items;

public class HoleSnap extends Module {
    public HoleSnap() {
        super(Categories.Movement, Items.AIR, "hole-snap", "Attempts to Snap downwards when outside of a hole || Very Aggressive");
    }

    @EventHandler
    public boolean onTick(TickEvent.Post event) {
        assert mc.player != null;
        if (!PlayerUtils.isInHole(mc.player) && (!mc.player.isOnGround())) {
            assert mc.world != null;

            if (mc.world.getBlockState(mc.player.getBlockPos().down()).isAir()) {
                mc.player.setVelocity(mc.player.getVelocity().x, -50, mc.player.getVelocity().z);
            }
        }
        return false;
    }
}
