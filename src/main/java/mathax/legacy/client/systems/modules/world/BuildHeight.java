package mathax.legacy.client.systems.modules.world;

import mathax.legacy.client.events.packets.PacketEvent;
import mathax.legacy.client.mixin.BlockHitResultAccessor;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.eventbus.EventHandler;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.math.Direction;

public class BuildHeight extends Module {
    public BuildHeight() {
        super(Categories.World, Items.SCAFFOLDING, "build-height", "Allows you to interact with objects at the build limit.");
    }

    @EventHandler
    private void onSendPacket(PacketEvent.Send event) {
        if (!(event.packet instanceof PlayerInteractBlockC2SPacket)) return;

        PlayerInteractBlockC2SPacket p = (PlayerInteractBlockC2SPacket) event.packet;
        if (p.getBlockHitResult().getPos().y >= 255 && p.getBlockHitResult().getSide() == Direction.UP) {
            ((BlockHitResultAccessor) p.getBlockHitResult()).setSide(Direction.DOWN);
        }
    }
}
