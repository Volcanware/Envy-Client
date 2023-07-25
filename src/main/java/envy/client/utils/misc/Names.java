package envy.client.utils.misc;

import envy.client.Envy;
import envy.client.eventbus.EventHandler;
import envy.client.events.game.ResourcePacksReloadedEvent;
import net.minecraft.block.Block;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringHelper;
import org.apache.commons.lang3.text.WordUtils;

import java.util.HashMap;
import java.util.Map;

public class Names {
    private static final Map<StatusEffect, String> statusEffectNames = new HashMap<>(16);
    private static final Map<Item, String> itemNames = new HashMap<>(128);
    private static final Map<Block, String> blockNames = new HashMap<>(128);
    private static final Map<Enchantment, String> enchantmentNames = new HashMap<>(16);
    private static final Map<EntityType<?>, String> entityTypeNames = new HashMap<>(64);
    private static final Map<ParticleType<?>, String> particleTypesNames = new HashMap<>(64);
    private static final Map<Identifier, String> soundNames = new HashMap<>(64);

    public static void init() {
        Envy.EVENT_BUS.subscribe(Names.class);
    }

    @EventHandler
    private static void onResourcePacksReloaded(ResourcePacksReloadedEvent event) {
        statusEffectNames.clear();
        itemNames.clear();
        blockNames.clear();
        enchantmentNames.clear();
        entityTypeNames.clear();
        particleTypesNames.clear();
        soundNames.clear();
    }

    public static String get(StatusEffect effect) {
        return statusEffectNames.computeIfAbsent(effect, effect1 -> StringHelper.stripTextFormat(effect1.getName().getString()));
    }

    public static String get(Item item) {
        return itemNames.computeIfAbsent(item, item1 -> StringHelper.stripTextFormat(item1.getName().getString()));
    }

    public static String get(Block block) {
        return blockNames.computeIfAbsent(block, block1 -> StringHelper.stripTextFormat(block1.getName().getString()));
    }

    public static String get(Enchantment enchantment) {
        return enchantmentNames.computeIfAbsent(enchantment, enchantment1 -> StringHelper.stripTextFormat(Text.translatable(enchantment1.getTranslationKey()).getString()));
    }

    public static String get(EntityType<?> entityType) {
        return entityTypeNames.computeIfAbsent(entityType, entityType1 -> StringHelper.stripTextFormat(entityType1.getName().getString()));
    }

    public static String get(ParticleType<?> type) {
        if (!(type instanceof ParticleEffect)) return "";
        return particleTypesNames.computeIfAbsent(type, effect1 -> WordUtils.capitalize(((ParticleEffect) effect1).asString().substring(10).replace("_", " ")));
    }

    public static String getSoundName(Identifier id) {
        return soundNames.computeIfAbsent(id, identifier -> {
            WeightedSoundSet soundSet = Envy.mc.getSoundManager().get(identifier);
            if (soundSet == null) return identifier.getPath();

            Text text = soundSet.getSubtitle();
            if (text == null) return identifier.getPath();

            return StringHelper.stripTextFormat(text.getString());
        });
    }
}
