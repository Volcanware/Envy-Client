package envy.client.systems.modules.movement.speed.modes;

import envy.client.events.entity.player.PlayerMoveEvent;
import envy.client.mixininterface.IVec3d;
import envy.client.systems.modules.Modules;
import envy.client.systems.modules.movement.speed.Speed;
import envy.client.systems.modules.movement.speed.SpeedMode;
import envy.client.systems.modules.movement.speed.SpeedModes;
import envy.client.utils.player.PlayerUtils;
import net.minecraft.util.math.Vec3d;

public class EnvyAnarchy extends SpeedMode {

    public EnvyAnarchy() {
        super(SpeedModes.EnvyAnarchy);
    }

    @Override
    public boolean onMove(PlayerMoveEvent event) {
        Vec3d vel = PlayerUtils.getHorizontalVelocity(settings.EnvyAnarchy.get());
        double velX = vel.getX() * 1.5;
        double velZ = vel.getZ() * 1.5;
        double velY = vel.getY() - 1;

        if (mc.options.jumpKey.isPressed()) {
            mc.options.jumpKey.setPressed(false);
        }
        mc.player.setVelocity(velX, velY, velZ);


        ((IVec3d) event.movement).set(velX, event.movement.y, velZ);
        return false;
    }
    @Override
    public void onRubberband() {
        if (settings.rubberband.get()) {
            (Modules.get().get(Speed.class)).forceToggle(false);
        }
    }
}

