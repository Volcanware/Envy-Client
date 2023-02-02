package mathax.client.systems.modules.movement.speed.modes;

import mathax.client.systems.modules.Modules;
import mathax.client.systems.modules.movement.speed.SpeedMode;
import mathax.client.systems.modules.movement.speed.SpeedModes;
import mathax.client.systems.modules.world.Timer;
import mathax.client.utils.player.PlayerUtils;

public class Matrix extends SpeedMode {

    public Matrix() {
        super(SpeedModes.Matrix);
    }
    public void onTick() {
        if (mc.player.isSubmergedInWater()) return;
        if (PlayerUtils.isMoving())
        if (mc.player.isOnGround()) {
            mc.player.jump();
            mc.player.airStrafingSpeed = 0.02098f;
            Modules.get().get(Timer.class).setOverride(1.055f);
        } else {

        } else {
        Modules.get().get(Timer.class).setOverride(1);
        }
    }
}

