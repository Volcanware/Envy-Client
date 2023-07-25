package envy.client.systems.modules.movement.speed.modes;

import envy.client.eventbus.EventHandler;
import envy.client.systems.modules.Modules;
import envy.client.systems.modules.movement.speed.Speed;
import envy.client.systems.modules.movement.speed.SpeedMode;
import envy.client.systems.modules.movement.speed.SpeedModes;
import envy.client.utils.algorithms.extra.MovementUtils;
import envy.client.utils.player.PlayerUtils;

public class Chinese extends SpeedMode {
    public Chinese() {super(SpeedModes.Chinese);}
    @EventHandler
    public boolean onTick() {
        if (PlayerUtils.isMoving()) {
            if (mc.player.isOnGround()) {
                mc.player.jump();
            }
            if (mc.player.fallDistance > 0.2) {
                mc.player.airStrafingSpeed = (float) 0.1;
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
