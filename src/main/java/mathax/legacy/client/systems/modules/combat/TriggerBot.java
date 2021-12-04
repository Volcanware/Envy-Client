package mathax.legacy.client.systems.modules.combat;

import mathax.legacy.client.eventbus.EventHandler;
import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.settings.BoolSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class TriggerBot extends Module {
    private Entity target;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Boolean> whenHoldingAttack = this.sgGeneral.add(new BoolSetting.Builder()
        .name("when-holding-attack")
        .description("Attacks only when you are holding bound attack key.")
        .defaultValue(false)
        .build()
    );

    public TriggerBot() {
        super(Categories.Combat, Items.COMMAND_BLOCK, "trigger-bot", "Automatically swings when you look at entities.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        target = null;

        if (mc.player.getHealth() <= 0.0f || mc.player.getAttackCooldownProgress(0.5f) < 1.0f) return;
        if (!(mc.targetedEntity instanceof LivingEntity)) return;
        if (((LivingEntity) mc.targetedEntity).getHealth() <= 0.0f) return;

        target = mc.targetedEntity;

        if (whenHoldingAttack.get()) {
            if (mc.options.keyAttack.isPressed()) attack(target);
        } else attack(target);
    }

    private void attack(Entity entity) {
        mc.interactionManager.attackEntity(mc.player, entity);
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    @Override
    public String getInfoString() {
        if (target != null && target instanceof PlayerEntity) return target.getEntityName();
        if (target != null) return target.getType().getName().getString();
        return null;
    }
}

