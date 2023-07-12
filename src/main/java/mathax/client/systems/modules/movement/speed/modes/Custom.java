package mathax.client.systems.modules.movement.speed.modes;

import mathax.client.systems.modules.Modules;
import mathax.client.systems.modules.movement.AutoJump;
import mathax.client.systems.modules.movement.speed.Speed;
import mathax.client.systems.modules.movement.speed.SpeedMode;
import mathax.client.systems.modules.movement.speed.SpeedModes;
import mathax.client.utils.EnvyUtils;
import mathax.client.utils.player.PlayerUtils;
import mathax.client.utils.world.TickRate;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;


public class Custom extends SpeedMode {
    public Custom() {
        super(SpeedModes.Custom);
    }

    @Override
    public boolean onTick() {
        if (settings.autojump.get() && mc.player.isOnGround()) {
            if (!settings.MoveOnly.get()) {
                mc.player.jump();
            }
            if (settings.MoveOnly.get() && PlayerUtils.isMoving()) {
                mc.player.jump();
            }
        }
        if (settings.autoSprint.get()) {
            mc.player.setSprinting(true);
        }
        if (!mc.player.isOnGround()) {
            mc.player.setVelocity(mc.player.getVelocity().x * settings.airstrafe.get(), mc.player.getVelocity().y, mc.player.getVelocity().z * settings.airstrafe.get());
        }
        if (mc.player.isOnGround()) {
            mc.player.setVelocity(mc.player.getVelocity().x * settings.groundStrafe.get(), mc.player.getVelocity().y, mc.player.getVelocity().z * settings.groundStrafe.get());
        }
        if (mc.player.isOnFire()) {
            mc.player.setVelocity(mc.player.getVelocity().x * settings.onfire.get(), mc.player.getVelocity().y, mc.player.getVelocity().z * settings.onfire.get());
        }
        if (!mc.player.isOnGround()) {
            mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().y / settings.floating.get(), mc.player.getVelocity().z);
        }
        if (settings.groundspoof.get()) {
            mc.player.setOnGround(true);
            settings.autojump.set(false);
            Modules.get().get(AutoJump.class).forceToggle(false);
        }
        if (mc.player.getOffHandStack().getItem() == Items.BOW) {
            mc.player.setVelocity(mc.player.getVelocity().x * settings.bowspeed.get(), mc.player.getVelocity().y, mc.player.getVelocity().z * settings.bowspeed.get());
        }
        if (mc.player.getMainHandStack().getItem() instanceof SwordItem) {
            mc.player.setVelocity(mc.player.getVelocity().x * settings.swordspeed.get(), mc.player.getVelocity().y, mc.player.getVelocity().z * settings.swordspeed.get());
        }
        if (mc.player.getHealth() < settings.lowhealthdisable.get()) {
            (Modules.get().get(Speed.class)).forceToggle(false);
        }
/*        if (settings.Strafe.get() && PlayerUtils.isMoving()) {
            MovementUtils.Vulcanstrafe();
        }*/
        if (settings.Fall.get()) {
            EnvyUtils.fall();
        }
        if (TickRate.INSTANCE.getTickRate() < settings.TPSDisable.get()) {
            (Modules.get().get(Speed.class)).forceToggle(false);
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
