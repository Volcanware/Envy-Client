package mathax.client.legacy.systems.modules.movement.speed.modes;

import mathax.client.legacy.events.entity.player.PlayerMoveEvent;
import mathax.client.legacy.mixininterface.IVec3d;
import mathax.client.legacy.systems.modules.Modules;
import mathax.client.legacy.systems.modules.movement.Anchor;
import mathax.client.legacy.systems.modules.movement.speed.SpeedMode;
import mathax.client.legacy.systems.modules.movement.speed.SpeedModes;
import mathax.client.legacy.utils.player.PlayerUtils;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.Vec3d;

public class Vanilla extends SpeedMode {
    public Vanilla() {
        super(SpeedModes.Vanilla);
    }

    @Override
    public void onMove(PlayerMoveEvent event) {
        Vec3d vel = PlayerUtils.getHorizontalVelocity(settings.vanillaSpeed.get());
        double velX = vel.getX();
        double velZ = vel.getZ();

        if (mc.player.hasStatusEffect(StatusEffects.SPEED)) {
            double value = (mc.player.getStatusEffect(StatusEffects.SPEED).getAmplifier() + 1) * 0.205;
            velX += velX * value;
            velZ += velZ * value;
        }

        Anchor anchor = Modules.get().get(Anchor.class);
        if (anchor.isActive() && anchor.controlMovement) {
            velX = anchor.deltaX;
            velZ = anchor.deltaZ;
        }

        ((IVec3d) event.movement).set(velX, event.movement.y, velZ);
    }
}
