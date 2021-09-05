package mathax.client.legacy.systems.modules;

import mathax.client.legacy.utils.render.color.Color;
import net.minecraft.item.Items;

public class Categories {
    public static final int COMBAT_COLOR = Color.fromRGBA(225, 0, 0, 255);
    public static final int PLAYER_COLOR = Color.fromRGBA(245, 255, 100, 255);
    public static final int MOVEMENT_COLOR = Color.fromRGBA(0, 125, 255, 255);
    public static final int RENDER_COLOR = Color.fromRGBA(125, 255, 255, 255);
    public static final int WORLD_COLOR = Color.fromRGBA(0, 150, 0, 255);
    public static final int CHAT_COLOR = Color.fromRGBA(255, 255, 255, 255);
    public static final int FUN_COLOR = Color.fromRGBA(255, 0, 255, 255);
    public static final int MISC_COLOR = Color.fromRGBA(0, 50, 175, 255);

    public static final Category Combat = new Category("Combat", Items.DIAMOND_SWORD.getDefaultStack(), COMBAT_COLOR);
    public static final Category Player = new Category("Player", Items.ARMOR_STAND.getDefaultStack(), PLAYER_COLOR);
    public static final Category Movement = new Category("Movement", Items.DIAMOND_BOOTS.getDefaultStack(), MOVEMENT_COLOR);
    public static final Category Render = new Category("Render", Items.TINTED_GLASS.getDefaultStack(), RENDER_COLOR);
    public static final Category World = new Category("World", Items.GRASS_BLOCK.getDefaultStack(), WORLD_COLOR);
    public static final Category Chat = new Category("Chat", Items.BEACON.getDefaultStack(), CHAT_COLOR);
    public static final Category Fun = new Category("Fun", Items.NOTE_BLOCK.getDefaultStack(), FUN_COLOR);
    public static final Category Misc = new Category("Misc", Items.NETHER_STAR.getDefaultStack(), MISC_COLOR);

    public static void register() {
        Modules.registerCategory(Combat);
        Modules.registerCategory(Player);
        Modules.registerCategory(Movement);
        Modules.registerCategory(Render);
        Modules.registerCategory(World);
        Modules.registerCategory(Chat);
        Modules.registerCategory(Fun);
        Modules.registerCategory(Misc);
    }
}
