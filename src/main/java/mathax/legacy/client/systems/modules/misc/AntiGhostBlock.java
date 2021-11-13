package mathax.legacy.client.systems.modules.misc;

import mathax.legacy.client.eventbus.EventHandler;
import mathax.legacy.client.events.entity.player.StartBreakingBlockEvent;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class AntiGhostBlock extends Module {
    private int counter = 0;

    public AntiGhostBlock() {
        super(Categories.Misc, Items.COMMAND_BLOCK, "anti-ghost-block", "Prevents ghost blocks from being made.");
    }

    @EventHandler
    private void onStartBreakingBlock(StartBreakingBlockEvent event) {
        ClientPlayNetworkHandler connection = mc.getNetworkHandler();
        counter++;

        if (connection == null || counter < 20) return;
        BlockPos pos = mc.player.getBlockPos();
        for (int dx = -4; dx <= 4; ++dx) {
            for (int dy = -4; dy <= 4; ++dy) {
                for (int dz = -4; dz <= 4; ++dz) {
                    PlayerActionC2SPacket packet = new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, new BlockPos(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz), Direction.UP);
                    connection.sendPacket(packet);
                }
            }
        }

        counter = 0;
    }
}
