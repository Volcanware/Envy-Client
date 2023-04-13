package mathax.client.systems.modules.render;

import mathax.client.eventbus.EventHandler;
import mathax.client.events.world.TickEvent;
import mathax.client.mixin.CameraMixin;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.item.Items;

public class BetterVisuals extends Module {

    public BetterVisuals() {
        super(Categories.Render, Items.AIR, "BetterVisuals" ,"Perfect For Videos");
    }
}
