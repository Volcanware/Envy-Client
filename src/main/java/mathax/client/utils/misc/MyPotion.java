package mathax.client.utils.misc;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;

public enum MyPotion {
    Swiftness(Potions.SWIFTNESS, Items.NETHER_WART, Items.SUGAR),
    Swiftness_Long(Potions.LONG_SWIFTNESS, Items.NETHER_WART, Items.SUGAR, Items.REDSTONE),
    Swiftness_Strong(Potions.STRONG_SWIFTNESS, Items.NETHER_WART, Items.SUGAR, Items.GLOWSTONE_DUST),

    Slowness(Potions.SLOWNESS, Items.NETHER_WART, Items.SUGAR, Items.FERMENTED_SPIDER_EYE),
    Slowness_Long(Potions.LONG_SLOWNESS, Items.NETHER_WART, Items.SUGAR, Items.FERMENTED_SPIDER_EYE, Items.REDSTONE),
    Slowness_Strong(Potions.STRONG_SLOWNESS, Items.NETHER_WART, Items.SUGAR, Items.FERMENTED_SPIDER_EYE, Items.GLOWSTONE_DUST),

    JumpBoost(Potions.LEAPING, Items.NETHER_WART, Items.RABBIT_FOOT),
    JumpBoost_Long(Potions.LONG_LEAPING, Items.NETHER_WART, Items.RABBIT_FOOT, Items.REDSTONE),
    JumpBoost_Strong(Potions.STRONG_LEAPING, Items.NETHER_WART, Items.RABBIT_FOOT, Items.GLOWSTONE_DUST),

    Strength(Potions.STRENGTH, Items.NETHER_WART, Items.BLAZE_POWDER),
    Strength_Long(Potions.LONG_STRENGTH, Items.NETHER_WART, Items.BLAZE_POWDER, Items.REDSTONE),
    Strength_Strong(Potions.STRONG_STRENGTH, Items.NETHER_WART, Items.BLAZE_POWDER, Items.GLOWSTONE_DUST),

    Healing(Potions.HEALING, Items.NETHER_WART, Items.GLISTERING_MELON_SLICE),
    Healing_Strong(Potions.STRONG_HEALING, Items.NETHER_WART, Items.GLISTERING_MELON_SLICE, Items.GLOWSTONE_DUST),

    Harming(Potions.HARMING, Items.NETHER_WART, Items.GLISTERING_MELON_SLICE, Items.FERMENTED_SPIDER_EYE),
    Harming_Strong(Potions.STRONG_HARMING, Items.NETHER_WART, Items.GLISTERING_MELON_SLICE, Items.FERMENTED_SPIDER_EYE, Items.GLOWSTONE_DUST),

    Poison(Potions.POISON, Items.NETHER_WART, Items.SPIDER_EYE),
    Poison_Long(Potions.LONG_POISON, Items.NETHER_WART, Items.SPIDER_EYE, Items.REDSTONE),
    Poison_Strong(Potions.STRONG_POISON, Items.NETHER_WART, Items.SPIDER_EYE, Items.GLOWSTONE_DUST),

    Regeneration(Potions.REGENERATION, Items.NETHER_WART, Items.GHAST_TEAR),
    Regeneration_Long(Potions.LONG_REGENERATION, Items.NETHER_WART, Items.GHAST_TEAR, Items.REDSTONE),
    Regeneration_Strong(Potions.STRONG_REGENERATION, Items.NETHER_WART, Items.GHAST_TEAR, Items.GLOWSTONE_DUST),

    FireResistance(Potions.FIRE_RESISTANCE, Items.NETHER_WART, Items.MAGMA_CREAM),
    FireResistance_Long(Potions.LONG_FIRE_RESISTANCE, Items.NETHER_WART, Items.MAGMA_CREAM, Items.REDSTONE),

    WaterBreathing(Potions.WATER_BREATHING, Items.NETHER_WART, Items.PUFFERFISH),
    WaterBreathing_Long(Potions.LONG_WATER_BREATHING, Items.NETHER_WART, Items.PUFFERFISH, Items.REDSTONE),

    NightVision(Potions.NIGHT_VISION, Items.NETHER_WART, Items.GOLDEN_CARROT),
    NightVision_Long(Potions.LONG_NIGHT_VISION, Items.NETHER_WART, Items.GOLDEN_CARROT, Items.REDSTONE),

    Invisibility(Potions.INVISIBILITY, Items.NETHER_WART, Items.GOLDEN_CARROT, Items.FERMENTED_SPIDER_EYE),
    Invisibility_Long(Potions.LONG_INVISIBILITY, Items.NETHER_WART, Items.GOLDEN_CARROT, Items.FERMENTED_SPIDER_EYE, Items.REDSTONE),

    TurtleMaster(Potions.TURTLE_MASTER, Items.NETHER_WART, Items.TURTLE_HELMET),
    TurtleMaster_Long(Potions.LONG_TURTLE_MASTER, Items.NETHER_WART, Items.TURTLE_HELMET, Items.REDSTONE),
    TurtleMaster_Strong(Potions.STRONG_TURTLE_MASTER, Items.NETHER_WART, Items.TURTLE_HELMET, Items.GLOWSTONE_DUST),

    SlowFalling(Potions.SLOW_FALLING, Items.NETHER_WART, Items.PHANTOM_MEMBRANE),
    SlowFalling_Long(Potions.LONG_SLOW_FALLING, Items.NETHER_WART, Items.PHANTOM_MEMBRANE, Items.REDSTONE),

    Weakness(Potions.WEAKNESS, Items.FERMENTED_SPIDER_EYE),
    Weakness_Long(Potions.LONG_WEAKNESS, Items.FERMENTED_SPIDER_EYE, Items.REDSTONE);

    public final ItemStack potion;
    public final Item[] ingredients;

    MyPotion(Potion potion, Item... ingredients) {
        this.potion = PotionUtil.setPotion(new ItemStack(Items.POTION), potion);
        this.ingredients = ingredients;
    }

    @Override
    public String toString() {
        return super.toString().replace("_", " ");
    }
}
