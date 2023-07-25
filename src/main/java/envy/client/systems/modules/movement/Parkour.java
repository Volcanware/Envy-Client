package envy.client.systems.modules.movement;

import com.google.common.collect.Streams;
import envy.client.eventbus.EventHandler;
import envy.client.events.world.TickEvent;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import net.minecraft.item.Items;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;

import java.util.stream.Stream;
//We Should write a smart mode that is a lot more complex
//todo: write a smart mode
public class Parkour extends Module {
    public Parkour() {
        super(Categories.Minigame, Items.DIAMOND_BOOTS, "parkour", "Automatically jumps at the edges of blocks.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!mc.player.isOnGround() || mc.options.jumpKey.isPressed()) return;

        if (mc.player.isSneaking() || mc.options.sneakKey.isPressed()) return;

        Box box = mc.player.getBoundingBox();
        Box adjustedBox = box.offset(0, -0.5, 0).expand(-0.001, 0, -0.001);

        Stream<VoxelShape> blockCollisions = Streams.stream(mc.world.getBlockCollisions(mc.player, adjustedBox));

        if (blockCollisions.findAny().isPresent()) return;

        mc.player.jump();
    }
}
