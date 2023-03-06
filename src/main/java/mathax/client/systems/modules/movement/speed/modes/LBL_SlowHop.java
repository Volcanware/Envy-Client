package mathax.client.systems.modules.movement.speed.modes;

import mathax.client.systems.modules.movement.speed.SpeedMode;
import mathax.client.systems.modules.movement.speed.SpeedModes;
import mathax.client.utils.player.PlayerUtils;

public class LBL_SlowHop extends SpeedMode {

    public LBL_SlowHop() {
        super(SpeedModes.LBL_SlowHop);
    }

    @Override
    public boolean onTick() {
        if (mc.player.isSubmergedInWater()) return false;
        if (PlayerUtils.isMoving()) {
            if (mc.player.isOnGround()) mc.player.jump(); else mc.player.airStrafingSpeed = 0.05f;
        }
        return false;
    }
}
