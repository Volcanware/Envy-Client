package mathax.legacy.client.systems.modules;

import mathax.legacy.client.utils.render.color.Color;
import net.minecraft.item.Items;

public class Categories {
    public static final Category Combat = new Category("Combat", Items.DIAMOND_SWORD.getDefaultStack(),Color.fromRGBA(225, 0, 0, 255));
    public static final Category Movement = new Category("Movement", Items.DIAMOND_BOOTS.getDefaultStack(), Color.fromRGBA(0, 125, 255, 255));
    public static final Category Render = new Category("Render", Items.TINTED_GLASS.getDefaultStack(), Color.fromRGBA(125, 255, 255, 255));
    public static final Category Player = new Category("Player", Items.ARMOR_STAND.getDefaultStack(), Color.fromRGBA(245, 255, 100, 255));
    public static final Category World = new Category("World", Items.GRASS_BLOCK.getDefaultStack(), Color.fromRGBA(0, 150, 0, 255));
    public static final Category Chat = new Category("Chat", Items.BEACON.getDefaultStack(), Color.fromRGBA(255, 255, 255, 255));
    public static final Category Misc = new Category("Misc", Items.NETHER_STAR.getDefaultStack(), Color.fromRGBA(0, 50, 175, 255));

    public static void register() {
        Modules.registerCategory(Combat);
        Modules.registerCategory(Movement);
        Modules.registerCategory(Render);
        Modules.registerCategory(Player);
        Modules.registerCategory(World);
        Modules.registerCategory(Chat);
        Modules.registerCategory(Misc);
    }
}
