package envy.client.systems.modules.player;

import envy.client.eventbus.EventHandler;
import envy.client.events.packets.PacketEvent;
import envy.client.settings.BoolSetting;
import envy.client.settings.Setting;
import envy.client.settings.SettingGroup;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class AntiSpawnpoint extends Module {
    private final SettingGroup sgDefault = settings.getDefaultGroup();

    private final Setting<Boolean> fakeUse = sgDefault.add(new BoolSetting.Builder()
        .name("fake-use")
        .description("Fake using the bed or anchor.")
        .defaultValue(true)
        .build()
    );

    public AntiSpawnpoint() {
        super(Categories.Player, Items.RED_BED, "anti-spawnpoint", "Protects the player from losing the respawn point.");
    }

    @EventHandler
    private void onSendPacket(PacketEvent.Send event) {
        if (mc.world == null) return;
        if (!(event.packet instanceof PlayerInteractBlockC2SPacket)) return;

        BlockPos blockPos = ((PlayerInteractBlockC2SPacket) event.packet).getBlockHitResult().getBlockPos();
        boolean IsOverWorld = mc.world.getDimension().bedWorks();
        boolean IsNetherWorld = mc.world.getDimension().respawnAnchorWorks();
        boolean BlockIsBed = mc.world.getBlockState(blockPos).getBlock() instanceof BedBlock;
        boolean BlockIsAnchor = mc.world.getBlockState(blockPos).getBlock().equals(Blocks.RESPAWN_ANCHOR);

        if (fakeUse.get()) {
            if (BlockIsBed && IsOverWorld) {
                mc.player.swingHand(Hand.MAIN_HAND);
                mc.player.updatePosition(blockPos.getX(),blockPos.up().getY(),blockPos.getZ());
            } else if (BlockIsAnchor && IsNetherWorld) mc.player.swingHand(Hand.MAIN_HAND);
        }

        if ((BlockIsBed && IsOverWorld)||(BlockIsAnchor && IsNetherWorld)) event.cancel();
    }
}
