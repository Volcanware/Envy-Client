package mathax.client.systems.modules.movement.speed.modes;

import mathax.client.events.entity.player.PlayerMoveEvent;
import mathax.client.systems.modules.Modules;
import mathax.client.systems.modules.movement.AutoJump;
import mathax.client.systems.modules.movement.speed.Speed;
import mathax.client.systems.modules.movement.speed.SpeedMode;
import mathax.client.systems.modules.movement.speed.SpeedModes;
import mathax.client.systems.modules.world.Timer;
import mathax.client.utils.algorithms.extra.MovementUtils;
import mathax.client.utils.player.PlayerUtils;

public class _5b5t extends SpeedMode {

    int ticks;

    public _5b5t() {
        super(SpeedModes._5b5t);
    }

    @Override
    public boolean onMove(PlayerMoveEvent event) {


        if (PlayerUtils.isMoving()) {
            (Modules.get().get(AutoJump.class)).toggle();
        }
        if (!PlayerUtils.isMoving()) {
            (Modules.get().get(AutoJump.class)).forceToggle(false);
        }

        if (mc.player.isOnGround() && PlayerUtils.isMoving()) {
            ticks = 0;

            MovementUtils.Vulcanstrafe();
            if (MovementUtils.getSpeed() < 0.5f) {
                MovementUtils.VulcanMoveStrafe(0.484f);
            }
        }

        if (!mc.player.isOnGround()) {
            ticks++;
        }
        if (ticks == 4) {
            mc.player.setVelocity(mc.player.getVelocity().getX(), mc.player.getVelocity().getY() - 0.17, mc.player.getVelocity().getZ());
        }

        if (ticks == 1) {
            MovementUtils.strafe(0.33f);
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