package mathax.client.systems.modules.movement.speed.modes;

import mathax.client.eventbus.EventHandler;
import mathax.client.systems.modules.Modules;
import mathax.client.systems.modules.movement.speed.Speed;
import mathax.client.systems.modules.movement.speed.SpeedMode;
import mathax.client.systems.modules.movement.speed.SpeedModes;
import mathax.client.utils.EnvyUtils;
import mathax.client.utils.algorithms.extra.MovementUtils;
import mathax.client.utils.player.MoveHelper;
import mathax.client.utils.player.PlayerUtils;

public class EnvyHop extends SpeedMode {

    public EnvyHop() {
        super(SpeedModes.EnvyHop);
    }

    @EventHandler
    public boolean onTick() {
        if (mc.player.isOnGround() && PlayerUtils.isMoving()) {
            mc.player.jump();
        }
        if (mc.player.fallDistance > 0.2f) {
            EnvyUtils.fall();
        }
        if (PlayerUtils.isMoving() && mc.player.fallDistance < 0.15f) {
            MoveHelper.setAirStrafeSpeed(mc.player, 0.1f);
        }
        MovementUtils.Vulcanstrafe();
        return false;
    }
    @Override
    public void onRubberband() {
        if (settings.rubberband.get()) {
            (Modules.get().get(Speed.class)).forceToggle(false);
        }
    }
}
