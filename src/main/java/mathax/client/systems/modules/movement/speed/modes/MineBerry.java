package mathax.client.systems.modules.movement.speed.modes;

import mathax.client.events.entity.player.PlayerMoveEvent;
import mathax.client.systems.modules.Modules;
import mathax.client.systems.modules.movement.AutoJump;
import mathax.client.systems.modules.movement.speed.SpeedMode;
import mathax.client.systems.modules.movement.speed.SpeedModes;
import mathax.client.systems.modules.world.Timer;
import mathax.client.utils.player.PlayerUtils;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.Vec3d;

public class MineBerry extends SpeedMode {
    public MineBerry() {
        super(SpeedModes.MineBerry);
    }

    @Override
    public boolean onMove(PlayerMoveEvent event) {
        Vec3d vel = PlayerUtils.getHorizontalVelocity(settings.TimerHop.get());
        double velX = vel.getX();
        double velZ = vel.getZ();
        double velY = vel.getY();

        if (PlayerUtils.isMoving()) {
            (Modules.get().get(AutoJump.class)).toggle();
        }

        if (mc.player.fallDistance <= 0.1)
            Modules.get().get(Timer.class).setOverride(1.2f);
        else if (mc.player.fallDistance < 1.3)
            Modules.get().get(Timer.class).setOverride(0.7f);
        else
            Modules.get().get(Timer.class).setOverride(1.0f);
        return false;
    }

    @Override
    public void onDeactivate() {
        (Modules.get().get(AutoJump.class)).forceToggle(false);
    }

    @Override
    public void onTick() {
        if (mc.player.hasStatusEffect(StatusEffects.SLOWNESS)) {
            mc.player.removeStatusEffect(StatusEffects.SLOWNESS);
        }
    }
}
