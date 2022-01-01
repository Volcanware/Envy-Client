package mathax.client.systems.modules.player;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import mathax.client.eventbus.EventHandler;
import mathax.client.events.world.TickEvent;
import mathax.client.mixin.StatusEffectInstanceAccessor;
import mathax.client.settings.Setting;
import mathax.client.settings.SettingGroup;
import mathax.client.settings.StatusEffectAmplifierMapSetting;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import mathax.client.utils.Utils;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Items;

public class PotionSpoof extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Object2IntMap<StatusEffect>> potions = sgGeneral.add(new StatusEffectAmplifierMapSetting.Builder()
        .name("potions")
        .description("Potions to add.")
        .defaultValue(Utils.createStatusEffectMap())
        .build()
    );

    public PotionSpoof() {
        super(Categories.Player, Items.POTION, "potion-spoof", "Spoofs specified potion effects for you. SOME effects DO NOT work.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        for (StatusEffect statusEffect : potions.get().keySet()) {
            int level = potions.get().getInt(statusEffect);
            if (level <= 0) continue;

            if (mc.player.hasStatusEffect(statusEffect)) {
                StatusEffectInstance instance = mc.player.getStatusEffect(statusEffect);
                ((StatusEffectInstanceAccessor) instance).setAmplifier(level - 1);
                if (instance.getDuration() < 20) ((StatusEffectInstanceAccessor) instance).setDuration(20);
            } else {
                mc.player.addStatusEffect(new StatusEffectInstance(statusEffect, 20, level - 1));
            }
        }
    }
}
