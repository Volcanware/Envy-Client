package envy.client.systems.modules.movement.speed.modes;

import envy.client.events.entity.player.PlayerMoveEvent;
import envy.client.mixininterface.IVec3d;
import envy.client.systems.modules.Modules;
import envy.client.systems.modules.movement.Anchor;
import envy.client.systems.modules.movement.speed.Speed;
import envy.client.systems.modules.movement.speed.SpeedMode;
import envy.client.systems.modules.movement.speed.SpeedModes;
import envy.client.utils.Utils;
import envy.client.utils.misc.Vec2;
import envy.client.utils.player.PlayerUtils;

public class Strafe extends SpeedMode {

    //Add Themis Button that fakes a 2 Block Tall Wall as it somehow bypasses ¯\_(ツ)_/¯
    private long timer = 0L;

    public Strafe() {
        super(SpeedModes.Strafe);
    }

    @Override
    public boolean onMove(PlayerMoveEvent event) {
        switch (stage) {
            case 0:
                if (PlayerUtils.isMoving()) {
                    stage++;
                    speed = 1.18f * getDefaultSpeed() - 0.01;
                }
            case 1:
                if (!PlayerUtils.isMoving() || !mc.player.isOnGround()) break;

                ((IVec3d) event.movement).setY(getHop(0.40123128));
                speed *= settings.ncpSpeed.get();
                stage++;
                break;
            case 2: speed = distance - 0.76 * (distance - getDefaultSpeed()); stage++; break;
            case 3:
                if (!mc.world.isSpaceEmpty(mc.player.getBoundingBox().offset(0.0, mc.player.getVelocity().y, 0.0)) || mc.player.verticalCollision && stage > 0) {
                    stage = 0;
                }
                speed = distance - (distance / 159.0);
                break;
        }

        speed = Math.max(speed, getDefaultSpeed());

        if (settings.ncpSpeedLimit.get()) {
            if (Utils.getCurrentTimeMillis() - timer > 2500L) timer = Utils.getCurrentTimeMillis();

            speed = Math.min(speed, Utils.getCurrentTimeMillis() - timer > 1250L ? 0.44D : 0.43D);
        }

        Vec2 change = transformStrafe(speed);

        double velX = change.x;
        double velZ = change.y;

        Anchor anchor = Modules.get().get(Anchor.class);
        if (anchor.isActive() && anchor.controlMovement) {
            velX = anchor.deltaX;
            velZ = anchor.deltaZ;
        }

        ((IVec3d) event.movement).setXZ(velX, velZ);
        return false;
    }

    private Vec2 transformStrafe(double speed) {
        float forward = mc.player.input.movementForward;
        float side = mc.player.input.movementSideways;
        float yaw = mc.player.prevYaw + (mc.player.getYaw() - mc.player.prevYaw) * mc.getTickDelta();

        double velX, velZ;

        if (forward == 0.0f && side == 0.0f) return new Vec2(0, 0);

        else if (forward != 0.0f) {
            if (side >= 1.0f) {
                yaw += (float) (forward > 0.0f ? -45 : 45);
                side = 0.0f;
            } else if (side <= -1.0f) {
                yaw += (float) (forward > 0.0f ? 45 : -45);
                side = 0.0f;
            }

            if (forward > 0.0f) forward = 1.0f;
            else if (forward < 0.0f) forward = -1.0f;
        }

        double mx = Math.cos(Math.toRadians(yaw + 90.0f));
        double mz = Math.sin(Math.toRadians(yaw + 90.0f));

        velX = (double) forward * speed * mx + (double) side * speed * mz;
        velZ = (double) forward * speed * mz - (double) side * speed * mx;

        return new Vec2(velX, velZ);
    }
    @Override
    public void onRubberband() {
        (Modules.get().get(Speed.class)).forceToggle(false);
    }

    @Override
    public boolean onTick() {
        distance = Math.sqrt((mc.player.getX() - mc.player.prevX) * (mc.player.getX() - mc.player.prevX) + (mc.player.getZ() - mc.player.prevZ) * (mc.player.getZ() - mc.player.prevZ));
        return false;
    }
}
