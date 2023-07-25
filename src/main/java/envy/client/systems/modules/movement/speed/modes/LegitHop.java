package envy.client.systems.modules.movement.speed.modes;

import envy.client.systems.modules.Modules;
import envy.client.systems.modules.movement.speed.Speed;
import envy.client.systems.modules.movement.speed.SpeedMode;
import envy.client.systems.modules.movement.speed.SpeedModes;

public class LegitHop  extends SpeedMode {

    public LegitHop() {
        super(SpeedModes.LegitHop);
    }

    @Override
    public boolean onTick() {
        if (mc.player.isOnGround()) {
            mc.player.jump();
            mc.player.setSprinting(true);
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
