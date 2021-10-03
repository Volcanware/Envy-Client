package mathax.legacy.client.systems.modules.movement;

import baritone.api.BaritoneAPI;
import com.google.common.collect.Streams;
import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.utils.player.DamageUtils;
import mathax.legacy.client.bus.EventHandler;
import mathax.legacy.client.settings.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Items;

import java.util.Comparator;
import java.util.Optional;

public class Step extends Module {
    private float prevStepHeight;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Double> height = sgGeneral.add(new DoubleSetting.Builder()
        .name("height")
        .description("Step height.")
        .defaultValue(1)
        .min(0)
        .build()
    );

    private final Setting<ActiveWhen> activeWhen = sgGeneral.add(new EnumSetting.Builder<ActiveWhen>()
        .name("active-when")
        .description("Step is active when you meet these requirements.")
        .defaultValue(ActiveWhen.Always)
        .build()
    );

    private final Setting<Boolean> safeStep = sgGeneral.add(new BoolSetting.Builder()
        .name("safe-step")
        .description("Doesn't let you step out of a hole if you are low on health or there is a crystal nearby.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Integer> stepHealth = sgGeneral.add(new IntSetting.Builder()
        .name("step-health")
        .description("The health you stop being able to step at.")
        .defaultValue(5)
        .min(1)
        .max(36)
        .visible(safeStep::get)
        .build()
    );

    public Step() {
        super(Categories.Movement, Items.DIAMOND_BOOTS, "step", "Allows you to walk up full blocks instantly.");
    }

    @Override
    public void onActivate() {
        assert mc.player != null;
        prevStepHeight = mc.player.stepHeight;

        BaritoneAPI.getSettings().assumeStep.value = true;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        boolean work = (activeWhen.get() == ActiveWhen.Always) || (activeWhen.get() == ActiveWhen.Sneaking && mc.player.isSneaking()) || (activeWhen.get() == ActiveWhen.NotSneaking && !mc.player.isSneaking());
        mc.player.setBoundingBox(mc.player.getBoundingBox().offset(0, 1, 0));
        if (work && (!safeStep.get() || (getHealth() > stepHealth.get() && getHealth() - getExplosionDamage() > stepHealth.get()))){
            mc.player.stepHeight = height.get().floatValue();
        } else {
            mc.player.stepHeight = prevStepHeight;
        }
        mc.player.setBoundingBox(mc.player.getBoundingBox().offset(0, -1, 0));
    }

    @Override
    public void onDeactivate() {
        if (mc.player != null) mc.player.stepHeight = prevStepHeight;

        BaritoneAPI.getSettings().assumeStep.value = false;
    }

    private float getHealth(){
        assert mc.player != null;
        return mc.player.getHealth() + mc.player.getAbsorptionAmount();
    }

    private double getExplosionDamage(){
        assert mc.player != null;
        assert mc.world != null;
        Optional<EndCrystalEntity> crystal = Streams.stream(mc.world.getEntities())
            .filter(entity -> entity instanceof EndCrystalEntity)
            .filter(Entity::isAlive)
            .max(Comparator.comparingDouble(o -> DamageUtils.crystalDamage(mc.player, o.getPos())))
            .map(entity -> (EndCrystalEntity) entity);
        return crystal.map(endCrystalEntity -> DamageUtils.crystalDamage(mc.player, endCrystalEntity.getPos())).orElse(0.0);
    }

    public enum ActiveWhen {
        Always,
        Sneaking,
        NotSneaking
    }
}
