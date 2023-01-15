package mathax.client.systems.modules.movement.speed.modes;

import mathax.client.eventbus.EventHandler;
import mathax.client.events.entity.player.PlayerMoveEvent;
import mathax.client.mixininterface.IVec3d;
import mathax.client.systems.modules.Modules;
import mathax.client.systems.modules.movement.speed.Speed;
import mathax.client.systems.modules.movement.speed.SpeedMode;
import mathax.client.systems.modules.movement.speed.SpeedModes;
import net.minecraft.entity.effect.StatusEffects;

import static net.minecraft.entity.effect.StatusEffects.SPEED;

public class Weird extends SpeedMode {
    private boolean isBhopEnabled = false;

    public Weird() {
        super(SpeedModes.Weird);
    }

    @Override
    public boolean onMove(PlayerMoveEvent event) {
        if (mc.player.isOnGround() == false) {
            double velX = event.movement.x * 2;
            double velZ = event.movement.z * 2;
            double velY = 0.4;
            mc.player.setVelocity(velX, velY, velZ);
            ((IVec3d) event.movement).set(velX, event.movement.y, velZ);
        }
        return false;
    }
    @EventHandler
    public void onTick() {
        settings.Strict.get();
        if (mc.player.hasStatusEffect(SPEED)) {
            Modules.get().get(Speed.class).forceToggle(false);

            if (mc.player.hasStatusEffect(StatusEffects.SLOWNESS)) {
                Modules.get().get(Speed.class).forceToggle(false);
            }
        }
    }
    @Override
    public void onRubberband() {
        (Modules.get().get(Speed.class)).forceToggle(false);
    }
}
