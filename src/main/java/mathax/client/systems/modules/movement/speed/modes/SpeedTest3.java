package mathax.client.systems.modules.movement.speed.modes;

import mathax.client.systems.modules.Modules;
import mathax.client.systems.modules.movement.speed.Speed;
import mathax.client.systems.modules.movement.speed.SpeedMode;
import mathax.client.systems.modules.movement.speed.SpeedModes;
import mathax.client.utils.player.MoveHelper;

public class SpeedTest3 extends SpeedMode {
    public SpeedTest3() {
        super(SpeedModes.Test3);
    }

    @Override
    public boolean onTick() {
        if (mc.player.isOnGround() == false) {
            MoveHelper.setAirStrafeSpeed(mc.player, 0.1f);
        }
        if (mc.player.isOnGround() == true && mc.player.isSprinting() == true) {
            mc.player.jump();
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
