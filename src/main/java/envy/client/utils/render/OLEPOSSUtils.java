package envy.client.utils.render;

import envy.client.Envy;
import envy.client.utils.Utils;
import envy.client.utils.misc.ChatUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class OLEPOSSUtils extends Utils {
    static ItemStack obbyStack = new ItemStack(Items.OBSIDIAN);
    public static double distance(Vec3d v1, Vec3d v2) {
        double dX = Math.abs(v1.x - v2.x);
        double dY = Math.abs(v1.y - v2.y);
        double dZ = Math.abs(v1.z - v2.z);
        return Math.sqrt(dX * dX + dY * dY + dZ * dZ);
    }

    public static Direction[] horizontals = new Direction[] {
        Direction.EAST,
        Direction.WEST,
        Direction.NORTH,
        Direction.SOUTH
    };

    public static Item[] bedItems = new Item[] {
        Items.BLACK_BED,
        Items.BLUE_BED,
        Items.BROWN_BED,
        Items.CYAN_BED,
        Items.GRAY_BED,
        Items.GREEN_BED,
        Items.LIGHT_BLUE_BED,
        Items.LIGHT_GRAY_BED,
        Items.LIME_BED,
        Items.MAGENTA_BED,
        Items.ORANGE_BED,
        Items.PINK_BED,
        Items.RED_BED,
        Items.WHITE_BED,
        Items.YELLOW_BED,
        Items.PURPLE_BED
    };
    public static Block[] bedBlocks = new Block[] {
        Blocks.BLACK_BED,
        Blocks.BLUE_BED,
        Blocks.BROWN_BED,
        Blocks.CYAN_BED,
        Blocks.GRAY_BED,
        Blocks.GREEN_BED,
        Blocks.LIGHT_BLUE_BED,
        Blocks.LIGHT_GRAY_BED,
        Blocks.LIME_BED,
        Blocks.MAGENTA_BED,
        Blocks.ORANGE_BED,
        Blocks.PINK_BED,
        Blocks.RED_BED,
        Blocks.WHITE_BED,
        Blocks.YELLOW_BED,
        Blocks.PURPLE_BED
    };

    public static Item[] helmets = new Item[] {
        Items.LEATHER_HELMET,
        Items.IRON_HELMET,
        Items.GOLDEN_HELMET,
        Items.CHAINMAIL_HELMET,
        Items.DIAMOND_HELMET,
        Items.NETHERITE_HELMET,
        Items.TURTLE_HELMET,
    };
    public static Item[] chestPlates = new Item[] {
        Items.LEATHER_CHESTPLATE,
        Items.IRON_CHESTPLATE,
        Items.GOLDEN_CHESTPLATE,
        Items.CHAINMAIL_CHESTPLATE,
        Items.DIAMOND_CHESTPLATE,
        Items.NETHERITE_CHESTPLATE,
    };
    public static Item[] leggings = new Item[] {
        Items.LEATHER_LEGGINGS,
        Items.IRON_LEGGINGS,
        Items.GOLDEN_LEGGINGS,
        Items.CHAINMAIL_LEGGINGS,
        Items.DIAMOND_LEGGINGS,
        Items.NETHERITE_LEGGINGS,
    };
    public static Item[] boots = new Item[] {
        Items.LEATHER_BOOTS,
        Items.IRON_BOOTS,
        Items.GOLDEN_BOOTS,
        Items.CHAINMAIL_BOOTS,
        Items.DIAMOND_BOOTS,
        Items.NETHERITE_BOOTS,
    };
    public static Item[] swords = new Item[] {
        Items.NETHERITE_SWORD,
        Items.DIAMOND_SWORD,
        Items.IRON_SWORD,
        Items.GOLDEN_SWORD,
        Items.STONE_SWORD,
        Items.WOODEN_SWORD,
    };
    public static Block[] anvils = new Block[] {
        Blocks.ANVIL,
        Blocks.CHIPPED_ANVIL,
        Blocks.DAMAGED_ANVIL,
    };
    public static StatusEffect[] effects = new StatusEffect[] {
        StatusEffects.SPEED,
        StatusEffects.ABSORPTION,
        StatusEffects.BAD_OMEN,
        StatusEffects.BLINDNESS,
        StatusEffects.CONDUIT_POWER,
        StatusEffects.DARKNESS,
        StatusEffects.DOLPHINS_GRACE,
        StatusEffects.FIRE_RESISTANCE,
        StatusEffects.GLOWING,
        StatusEffects.HASTE,
        StatusEffects.HEALTH_BOOST,
        StatusEffects.HERO_OF_THE_VILLAGE,
        StatusEffects.HUNGER,
        StatusEffects.INSTANT_DAMAGE,
        StatusEffects.INSTANT_HEALTH,
        StatusEffects.INVISIBILITY,
        StatusEffects.JUMP_BOOST,
        StatusEffects.LEVITATION,
        StatusEffects.LUCK,
        StatusEffects.MINING_FATIGUE,
        StatusEffects.NAUSEA,
        StatusEffects.NIGHT_VISION,
        StatusEffects.POISON,
        StatusEffects.REGENERATION,
        StatusEffects.RESISTANCE,
        StatusEffects.SATURATION,
        StatusEffects.SLOW_FALLING,
        StatusEffects.SLOWNESS,
        StatusEffects.STRENGTH,
        StatusEffects.SPEED,
        StatusEffects.UNLUCK,
        StatusEffects.WATER_BREATHING,
        StatusEffects.WEAKNESS,
        StatusEffects.WITHER
    };


    public static boolean isBedItem(Item item) {
        for (Item i : bedItems) {
            if (i.equals(item)) {return true;}
        }
        return false;
    }
    public static boolean isBedBlock(Block block) {
        for (Block b : bedBlocks) {
            if (b.equals(block)) {return true;}
        }
        return false;
    }
    public static boolean isHelmet(Item item) {
        for (Item i : helmets) {
            if (i.equals(item)) {return true;}
        }
        return false;
    }
    public static boolean isChestPlate(Item item) {
        for (Item i : chestPlates) {
            if (i.equals(item)) {return true;}
        }
        return false;
    }
    public static boolean isLegging(Item item) {
        for (Item i : leggings) {
            if (i.equals(item)) {return true;}
        }
        return false;
    }
    public static boolean isBoot(Item item) {
        for (Item i : boots) {
            if (i.equals(item)) {return true;}
        }
        return false;
    }
    public static boolean isSword(Item item) {
        for (Item i : swords) {
            if (i.equals(item)) {return true;}
        }
        return false;
    }
    public static boolean isArmor(Item item) {
        return isHelmet(item) || isChestPlate(item) || isLegging(item) || isBoot(item);
    }
    public static String armorCategory(Item item) {
        return isHelmet(item) ? "helmet" : isChestPlate(item) ? "chestplate" : isLegging(item) ? "leggings" : isBoot(item) ? "boots" : null;
    }
    public static boolean isAnvilBlock(Block block) {
        for (Block i : anvils) {
            if (i.equals(block)) {return true;}
        }
        return false;
    }

    public static Vec3d getMiddle(BlockPos pos) {
        return new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }
    public static Vec3d getMiddle(net.minecraft.util.math.Box box) {
        return new Vec3d((box.minX + box.maxX) / 2, (box.minY + box.maxY) / 2, (box.minZ + box.maxZ) / 2);
    }

    public static void sendBlockPos(BlockPos pos) {
        ChatUtils.sendMsg(Text.of("x" + pos.getX() + "  y" + pos.getY() + "  z" + pos.getZ()));
    }
    public static boolean inside(PlayerEntity en, net.minecraft.util.math.Box bb) {
        if (Envy.mc.world == null) {return false;}
        return Envy.mc.world.getBlockCollisions(en, bb).iterator().hasNext();
    }
    public static net.minecraft.util.math.Box getBox(BlockPos pos) {
        return new net.minecraft.util.math.Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
    }
    public static int closerToZero(int x) {return x - Math.round((float) x / Math.abs(x));}
    public static boolean isHole(BlockPos pos, World world, int height) {
        if (world == null) {return false;}
        for (Direction dir : horizontals) {
            if (world.getBlockState(pos.offset(dir)).getBlock() == Blocks.AIR) {return false;}
        }
        for (int i = 0; i < height; i++) {
            if (!world.getBlockState(pos.up(i)).getBlock().equals(Blocks.AIR)) {
                return false;
            }
        }
        return world.getBlockState(pos.down()).getBlock() != Blocks.AIR;
    }
    public static Direction closestDir(BlockPos pos, Vec3d vec) {
        Direction closest = null;
        double closestDist = -1;
        for (Direction dir : Direction.values()) {
            double dist = distance(new Vec3d(pos.getX() + 0.5 + dir.getOffsetX() / 2f, pos.getY() + 0.5 + dir.getOffsetY() / 2f, pos.getZ() + 0.5 + dir.getOffsetZ() / 2f), vec);

            if (closest == null || dist < closestDist) {
                closest = dir;
                closestDist = dist;
            }
        }
        return closest;
    }

    public static Vec3d getClosest(Vec3d pPos, Vec3d middle, double width, double height) {
        return new Vec3d(Math.min(Math.max(pPos.x, middle.x - width / 2), middle.x + width / 2),
            Math.min(Math.max(pPos.y, middle.y), middle.y + height),
            Math.min(Math.max(pPos.z, middle.z - width / 2), middle.z + width / 2));
    }

    public static boolean strictDir(BlockPos pos, Direction dir) {
        if (dir.getOffsetY() > 0 && Envy.mc.player.getEyePos().y >= pos.getY() + 0.5) {return true;}
        if (dir.getOffsetY() < 0 && Envy.mc.player.getEyePos().y <= pos.getY() + 0.5) {return true;}
        if (dir.getOffsetX() > 0 && Envy.mc.player.getX() >= pos.getX() + 1) {return true;}
        if (dir.getOffsetX() < 0 && Envy.mc.player.getX() < pos.getX()) {return true;}
        if (dir.getOffsetZ() > 0 && Envy.mc.player.getZ() >= pos.getZ() + 1) {return true;}
        if (dir.getOffsetZ() < 0 && Envy.mc.player.getZ() < pos.getZ()) {return true;}
        return false;
    }
    public static net.minecraft.util.math.Box getCrystalBox(BlockPos pos) {
        return new Box(pos.getX() - 0.5, pos.getY(), pos.getZ() - 0.5, pos.getX() + 1.5, pos.getY() + 2, pos.getZ() + 1.5);
    }
    public static boolean isCrystalBlock(Block block) {
        return block == Blocks.OBSIDIAN || block == Blocks.BEDROCK;
    }
    public static boolean canPlaceOn(BlockPos block) {
        return Envy.mc.world != null && Envy.mc.world.getBlockState(block).getBlock().hasDynamicBounds();
    }
}
