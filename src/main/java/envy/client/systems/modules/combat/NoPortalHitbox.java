package envy.client.systems.modules.combat;

import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import net.minecraft.item.Items;

public class NoPortalHitbox extends Module {
    public NoPortalHitbox() {
        super(Categories.Combat, Items.BEDROCK, "No-Portal-Hitbox", "Attack and smack ur enemies through portal");
    }
}
