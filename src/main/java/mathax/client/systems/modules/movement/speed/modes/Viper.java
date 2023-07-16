package mathax.client.systems.modules.movement.speed.modes;

import mathax.client.eventbus.EventHandler;
import mathax.client.events.world.TickEvent;
import mathax.client.systems.modules.movement.speed.SpeedMode;
import mathax.client.systems.modules.movement.speed.SpeedModes;
import mathax.client.utils.player.PlayerUtils;

public class Viper extends SpeedMode {


    public Viper() {
        super(SpeedModes.Viper);
    }


    @Override
    public boolean onTick() {
        if (PlayerUtils.isMoving()) {
            if (mc.player.isOnGround()) {
                mc.player.setVelocity(mc.player.getVelocity().getX(), 0.42, mc.player.getVelocity().getZ());
            }
        }

        return false;
    }

    @Override
    public void onRubberband() {
        // (Modules.get().get(Speed.class)).forceToggle(false);
    }
}
