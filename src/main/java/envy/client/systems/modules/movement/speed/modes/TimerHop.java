package envy.client.systems.modules.movement.speed.modes;

import envy.client.events.entity.player.PlayerMoveEvent;
import envy.client.systems.modules.Modules;
import envy.client.systems.modules.movement.AutoJump;
import envy.client.systems.modules.movement.speed.Speed;
import envy.client.systems.modules.movement.speed.SpeedMode;
import envy.client.systems.modules.movement.speed.SpeedModes;
import envy.client.systems.modules.world.Timer;
import envy.client.utils.player.PlayerUtils;

public class TimerHop extends SpeedMode {


    public TimerHop() {
        super(SpeedModes.TimerHop);
    }

    @Override
    public boolean onMove(PlayerMoveEvent event) {

        if (PlayerUtils.isMoving()) {
            (Modules.get().get(AutoJump.class)).toggle();
        }
        if (!PlayerUtils.isMoving()) {
            (Modules.get().get(AutoJump.class)).forceToggle(false);
        }

        if (mc.player.fallDistance <= 0.1)
            Modules.get().get(Timer.class).setOverride(1.7f);
        else if (mc.player.fallDistance < 1.3)
            Modules.get().get(Timer.class).setOverride(0.8f);
        else
            Modules.get().get(Timer.class).setOverride(1.0f);
        return false;
    }

    @Override
    public void onDeactivate() {
        (Modules.get().get(AutoJump.class)).forceToggle(false);
    }

    @Override
    public void onRubberband() {
        (Modules.get().get(Speed.class)).forceToggle(false);
    }
}
