package envy.client.systems.modules.movement.speed.modes;

import envy.client.eventbus.EventHandler;
import envy.client.systems.modules.Modules;
import envy.client.systems.modules.movement.speed.Speed;
import envy.client.systems.modules.movement.speed.SpeedMode;
import envy.client.systems.modules.movement.speed.SpeedModes;
import envy.client.utils.EnvyUtils;
import envy.client.utils.algorithms.extra.MovementUtils;
import envy.client.utils.player.PlayerUtils;

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
            mc.player.airStrafingSpeed = 0.1f;
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
