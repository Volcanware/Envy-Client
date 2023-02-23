package mathax.client.systems.modules.movement.speed.modes;

import mathax.client.systems.modules.movement.speed.SpeedMode;
import mathax.client.systems.modules.movement.speed.SpeedModes;

public class LegitHop  extends SpeedMode {

    public LegitHop() {
        super(SpeedModes.LegitHop);
    }

    @Override
    public void onTick() {
        if (mc.player.isOnGround()) {
            mc.player.jump();
            mc.player.setSprinting(true);
        }
    }
}
