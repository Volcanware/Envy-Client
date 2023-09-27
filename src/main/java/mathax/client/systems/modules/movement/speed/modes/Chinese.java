package mathax.client.systems.modules.movement.speed.modes;

import mathax.client.eventbus.EventHandler;
import mathax.client.systems.modules.Modules;
import mathax.client.systems.modules.movement.speed.Speed;
import mathax.client.systems.modules.movement.speed.SpeedMode;
import mathax.client.systems.modules.movement.speed.SpeedModes;
import mathax.client.utils.algorithms.extra.MovementUtils;
import mathax.client.utils.player.MoveHelper;
import mathax.client.utils.player.PlayerUtils;

public class Chinese extends SpeedMode {
    public Chinese() {super(SpeedModes.Chinese);}
    @EventHandler
    public boolean onTick() {
        if (PlayerUtils.isMoving()) {
            if (mc.player.isOnGround()) {
                mc.player.jump();
            }
            if (mc.player.fallDistance > 0.2) {
                MoveHelper.setAirStrafeSpeed(mc.player, 0.1f);
            }
            if (mc.player.fallDistance == 0.4) {
                MovementUtils.strafe(7f);
            }
        }
        return false;
    }
    @Override
    public void onRubberband() {
        if (settings.rubberband.get()) {
            (Modules.get().get(Speed.class)).forceToggle(false);
        }
    }
}
