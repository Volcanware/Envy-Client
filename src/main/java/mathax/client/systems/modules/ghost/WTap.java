package mathax.client.systems.modules.ghost;

import mathax.client.eventbus.EventHandler;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import net.minecraft.item.Items;

public class WTap extends Module {
            public WTap() {
            super(Categories.Ghost, Items.BARRIER, "WTap", "Sprint On Hit");
        }
    @EventHandler
    public <SwingArmEvent> void onSwingArm(SwingArmEvent event) {
        // Only send the packet if the player is not sprinting already
            // Send a player movement packet with sprinting set to true
            boolean onGround = mc.player.isOnGround();
            mc.player.setSprinting(true);
        }
    }
