//package mathax.client.systems.modules.movement;

import com.google.common.collect.Streams;
import mathax.client.eventbus.EventHandler;
import mathax.client.events.world.TickEvent;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import net.minecraft.item.Items;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;

import java.util.stream.Stream;

import static mathax.client.MatHax.mc;

//public class Ninja extends Module {

    //public Ninja() {
        //super(Categories.Movement, Items.DIAMOND_BOOTS, "ninja", "Automatically sneaks when you are about to fall.");
    //}
    //@EventHandler
    //private void onTick(TickEvent.Post event) {
        // Return early if the player is already sneaking or if the sneak key is being pressed
        //if (mc.player.isSneaking() || mc.options.sneakKey.isPressed()) {
            //return;
        //}

        // Return early if the player is pressing the jump key (since this module should only activate when the player is not jumping)
        //if (mc.options.jumpKey.isPressed()) {
            //return;
        //}

        // Create a box representing the player's bounding box and adjust it to be slightly lower (to account for the player's height)
        // and slightly smaller (to prevent false positives when checking for block collisions)
        //Box box = mc.player.getBoundingBox();
        //Box adjustedBox = box.offset(0, -0.5, 0).expand(-0.001, 0, -0.001);

        // Check if there are any block collisions with the adjusted bounding box
       // Stream<VoxelShape> blockCollisions = Streams.stream(mc.world.getBlockCollisions(mc.player, adjustedBox));
        //if (blockCollisions.findAny().isPresent()) {
      //      return;
      //  }

        // If no block collisions were found, set the player to sneaking
   //     mc.player.setSneaking(true);
 //   }
//}
