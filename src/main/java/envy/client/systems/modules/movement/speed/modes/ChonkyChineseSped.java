package envy.client.systems.modules.movement.speed.modes;

import envy.client.eventbus.EventHandler;
import envy.client.systems.modules.Modules;
import envy.client.systems.modules.movement.speed.Speed;
import envy.client.systems.modules.movement.speed.SpeedMode;
import envy.client.systems.modules.movement.speed.SpeedModes;
import envy.client.systems.modules.world.Timer;
import envy.client.utils.player.PlayerUtils;

public class ChonkyChineseSped extends SpeedMode {
    public ChonkyChineseSped() {super(SpeedModes.ChonkyChineseSped);}

    @EventHandler
    public boolean onTick(){
        if (mc.player.isOnGround() && PlayerUtils.isMoving()) {
            mc.player.jump();
        }if (mc.player.fallDistance <= 0.4){
            mc.player.airStrafingSpeed = 2;
        }if (mc.player.fallDistance <= 0.1)
            Modules.get().get(Timer.class).setOverride(1.7f);
        else if (mc.player.fallDistance < 1.3)
            Modules.get().get(Timer.class).setOverride(0.8f);
        else
            Modules.get().get(Timer.class).setOverride(1.0f);


        return false;
    }
    @Override
    public void onRubberband() {
        if (settings.rubberband.get()) {
            (Modules.get().get(Speed.class)).forceToggle(false);
        }
    }
}
