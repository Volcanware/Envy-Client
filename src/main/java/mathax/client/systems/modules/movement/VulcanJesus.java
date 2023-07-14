package mathax.client.systems.modules.movement;

import mathax.client.eventbus.EventHandler;
import mathax.client.events.world.TickEvent;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import mathax.client.utils.algorithms.extra.MovementUtils;
import mathax.client.utils.vayzeutils.VulcanBooster;
import net.minecraft.item.Items;

public class VulcanJesus extends Module {
    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (MovementUtils.isMoving()) {
            assert mc.player != null;
            if (mc.player.isTouchingWater()) {
                mc.player.setVelocity(mc.player.getVelocity().x, 0.3, mc.player.getVelocity().z);
                VulcanBooster.VulcanBoost(0.14);
            }
            else {
                if (!mc.player.isOnGround()) {
                    mc.player.setVelocity(mc.player.getVelocity().x, -0.25, mc.player.getVelocity().z);
                }
            }
        }
        else {
            assert mc.player != null;
            if (mc.player.isTouchingWater()) {
                mc.player.setVelocity(mc.player.getVelocity().x, 0.01, mc.player.getVelocity().z);
            }
            else {
                mc.player.setVelocity(mc.player.getVelocity().x,-0.01 ,mc.player.getVelocity().z);
            }
        }
    }
    public VulcanJesus() {
        super(Categories.Movement, Items.WATER_BUCKET, "Vulcan Jesus", "Jesus Bypass for Vulcan AntiCheat.");
    }
}
