package envy.client.systems.modules.combat;

import envy.client.eventbus.EventHandler;
import envy.client.events.world.TickEvent;
import envy.client.settings.BoolSetting;
import envy.client.settings.EntityTypeListSetting;
import envy.client.settings.Setting;
import envy.client.settings.SettingGroup;
import envy.client.systems.friends.Friends;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class TriggerBot extends Module {
    private Entity target;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Object2BooleanMap<EntityType<?>>> blacklistedEntities = sgGeneral.add(new EntityTypeListSetting.Builder()
        .name("blacklist")
        .description("Entities to not attack.")
        .onlyAttackable()
        .build()
    );

    private final Setting<Boolean> ignoreFriends = sgGeneral.add(new BoolSetting.Builder()
        .name("ignore-friends")
        .description("Won't attack friends.")
        .defaultValue(false)
        .build()
    );

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
        if (!mc.targetedEntity.isAlive()) return;
        if (blacklistedEntities.get().containsKey(mc.targetedEntity.getType())) return;
        if (ignoreFriends.get() && mc.targetedEntity instanceof PlayerEntity player) {
            if (Friends.get().isFriend(player)) return;
        }

        target = mc.targetedEntity;

        if (whenHoldingAttack.get()) {
            if (mc.options.attackKey.isPressed()) attack(target);
        } else attack(target);
    }

    private void attack(Entity entity) {
        mc.interactionManager.attackEntity(mc.player, entity);
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    @Override
    public String getInfoString() {
        if (target != null && target instanceof PlayerEntity playerTarget) return playerTarget.getGameProfile().getName();
        if (target != null) return target.getType().getName().getString();
        return null;
    }
}
