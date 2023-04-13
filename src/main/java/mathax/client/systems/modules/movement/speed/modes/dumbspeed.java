package mathax.client.systems.modules.movement.speed.modes;

import mathax.client.eventbus.EventHandler;
import mathax.client.systems.modules.movement.speed.SpeedMode;
import mathax.client.systems.modules.movement.speed.SpeedModes;
import mathax.client.utils.player.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

public class dumbspeed extends SpeedMode {

    public dumbspeed() {
        super(SpeedModes.dumbspeed);
    }
    @EventHandler
    public boolean onTick() {
        double velx = mc.player.getVelocity().x * settings.dumbspeed.get();
        double velz = mc.player.getVelocity().z * settings.dumbspeed.get();
        double xlim = mc.player.getVelocity().x;
        double zlim = mc.player.getVelocity().z;



        if (mc.player.isOnGround() && PlayerUtils.isMoving()) {
            mc.player.jump();

            mc.player.setVelocity(velx, mc.player.getVelocity().y, velz);
        }
        if (mc.player.fallDistance >= 4 && !mc.player.isSprinting()) {

            mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().y -4, mc.player.getVelocity().z);

        }
        if (xlim > 1 || zlim > 1) {
            mc.player.setVelocity(1, mc.player.getVelocity().y, 1);
        }
        if (xlim > 1 && zlim > 1) {
            mc.player.setVelocity(1, mc.player.getVelocity().y, 1);
        }
        Block jebusv2down = mc.world.getBlockState(mc.player.getBlockPos().down()).getBlock();
        Block jebusv2up = mc.world.getBlockState(mc.player.getBlockPos().up()).getBlock();

        if (jebusv2down == Blocks.WATER) {
            mc.player.setVelocity(mc.player.getVelocity().x, -1, mc.player.getVelocity().z);
        }
        return false;
    }


}
