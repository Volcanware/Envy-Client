package envy.client.systems.modules.world;

import envy.client.settings.BoolSetting;
import envy.client.settings.IntSetting;
import envy.client.settings.Setting;
import envy.client.settings.SettingGroup;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class AntiGhostBlock extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Boolean> onlyBlastProof = sgGeneral.add(new BoolSetting.Builder()
        .name("only-blast-proof")
        .description("Only checks for blast proof blocks to limit spamming packets")
        .defaultValue(true)
        .build()
    );

    private final Setting<Integer> underFeet = sgGeneral.add(new IntSetting.Builder()
        .name("under-feet")
        .description("How many blocks under your feet it should start counting for horizontal")
        .defaultValue(0)
        .sliderRange(-5, 5)
        .build()
    );

    private final Setting<Integer> horizontalRange = sgGeneral.add(new IntSetting.Builder()
        .name("horizontal-range")
        .description("The horizontal range.")
        .defaultValue(4)
        .sliderRange(1, 6)
        .build()
    );

    private final Setting<Integer> verticalRange = sgGeneral.add(new IntSetting.Builder()
        .name("vertical-range")
        .description("The vertical range.")
        .defaultValue(4)
        .sliderRange(1, 6)
        .build()
    );

    public AntiGhostBlock() {
        super(Categories.World, Items.COMMAND_BLOCK, "anti-ghost-block", "Tries to remove nearby ghost blocks.");
    }

    private static final BlockPos.Mutable blockPos = new BlockPos.Mutable();
    //just have good ping ez
    @Override
    public boolean onActivate() {
        if (mc.getNetworkHandler() == null) return false;

        BlockPos pos = mc.player.getBlockPos();
        for (int dz = -horizontalRange.get(); dz <= horizontalRange.get(); dz++) {
            for (int dx = -horizontalRange.get(); dx <= horizontalRange.get(); dx++) {
                for (int dy = -verticalRange.get(); dy <= verticalRange.get(); dy++) {
                    blockPos.set(pos.getX() + dx, (pos.getY() + underFeet.get()) + dy, pos.getZ() + dz);

                    BlockState blockState = mc.world.getBlockState(blockPos);
                    if (!blockState.isAir() && !blockState.isOf(Blocks.BEDROCK) && ((blockState.getBlock().getBlastResistance() >= 600 && onlyBlastProof.get()) || !onlyBlastProof.get())) {
                        PlayerActionC2SPacket packet = new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, new BlockPos(pos.getX() + dx, (pos.getY() + underFeet.get()) + dy, pos.getZ() + dz), Direction.UP);
                        mc.getNetworkHandler().sendPacket(packet);
                    }
                }
            }
        }

        toggle();
        return false;
    }
}
