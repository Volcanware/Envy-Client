package mathax.client.systems.modules.combat;

import mathax.client.eventbus.EventHandler;
import mathax.client.events.entity.player.AttackEntityEvent;
import mathax.client.events.world.TickEvent;
import mathax.client.settings.BoolSetting;
import mathax.client.settings.Setting;
import mathax.client.settings.SettingGroup;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import mathax.client.utils.chinaman.FloorUtil;
import mathax.client.utils.player.Rotations;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ShieldBypass extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
        .name("rotate")
        .description("Rotate towards enemy.")
        .defaultValue(true)
        .build()
    );

    public ShieldBypass() {
        super (Categories.Combat, Items.SHIELD, "shield-bypass", "Attempts to teleport you behind enemies to bypass shields.");
    }

    private Vec3d originalPos;
    private Entity target;

    @Override
    public void onDeactivate() {
        originalPos = null;
        target = null;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (originalPos != null && target != null) {
            mc.interactionManager.attackEntity(mc.player, target);
            mc.player.setPosition(originalPos);
            if (rotate.get()) Rotations.rotate(-mc.player.getYaw(), mc.player.getPitch(), -10);
        }
        originalPos = null;
        target = null;
    }

    @EventHandler
    private void onAttackEntity(AttackEntityEvent event) {
        if (event.entity instanceof LivingEntity e && e.isBlocking()) {
            if (originalPos != null) return;

            Vec3d originalPos = mc.player.getPos();

            // Shield check
            Vec3d vec3d3 = originalPos.relativize(e.getPos()).normalize();
            if (new Vec3d(vec3d3.x, 0.0d, vec3d3.z).dotProduct(e.getRotationVec(1.0f)) >= 0.0d) return;

            double range = mc.player.distanceTo(e);
            while (range >= 0) {
                Vec3d tp = Vec3d.fromPolar(0, mc.player.getYaw()).normalize().multiply(range);
                Vec3d newPos = tp.add(e.getPos());
                BlockPos pos = FloorUtil.ofFloored(newPos);
                for (int i = -2; i <= 2; i++) {
                    if (mc.world.getBlockState(pos.up(i)).isAir() && mc.world.getBlockState(pos).isAir()) {
                        this.originalPos = originalPos;
                        if (rotate.get()) Rotations.rotate(-mc.player.getYaw(), mc.player.getPitch(),-10);
                        target = e;
                        event.cancel();
                        mc.player.setPosition(newPos.add(0, i, 0));
                        return;
                    }
                }
                range--;
            }
        }
    }
}
