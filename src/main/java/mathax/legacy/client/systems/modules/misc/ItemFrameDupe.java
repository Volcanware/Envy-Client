package mathax.legacy.client.systems.modules.misc;

import mathax.legacy.client.eventbus.EventHandler;
import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.settings.BoolSetting;
import mathax.legacy.client.settings.IntSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.utils.Utils;
import mathax.legacy.client.utils.player.FindItemResult;
import mathax.legacy.client.utils.player.InvUtils;
import mathax.legacy.client.utils.world.BlockUtils;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

/*/----------------------------------------------------------------------------------------------------------------------------------/*/
/*/ Used from Item Frame Dupe addon by Wide Cat                                                                                      /*/
/*/ https://github.com/Wide-Cat/item-frame-dupe-addon/blob/main/src/main/java/widecat/itemframedupe/addon/modules/ItemFrameDupe.java /*/
/*/----------------------------------------------------------------------------------------------------------------------------------/*/

public class ItemFrameDupe extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> distance = sgGeneral.add(new IntSetting.Builder()
        .name("distance")
        .description("The max distance to search for pistons.")
        .defaultValue(5)
        .range(0, 6)
           .sliderRange(0, 6)
        .build()
    );

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("The delay between placements.")
        .defaultValue(1)
        .sliderMax(10)
        .build()
    );

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
        .name("rotate")
        .description("Whether or not to rotate when placing.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> swapBack = sgGeneral.add(new BoolSetting.Builder()
        .name("swap-back")
        .description("Whether or not to swap back to the previous held item after placing.")
        .defaultValue(true)
        .build()
    );

    public ItemFrameDupe() {
        super(Categories.Misc, Items.ITEM_FRAME, "item-frame-dupe", "Assists with the item frame dupe by placing item frames on piston heads.");
    }

    private int timer;
    private ArrayList<BlockPos> positions = new ArrayList<>();
    private static final ArrayList<BlockPos> blocks = new ArrayList<>();

    @Override
    public void onActivate() {
        timer = delay.get();
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (!Utils.canUpdate()) return;

        if (timer > 0) {
            timer--;
            return;
        }
        else timer = delay.get();

        FindItemResult itemResult = InvUtils.findInHotbar(Items.ITEM_FRAME, Items.GLOW_ITEM_FRAME);
        if (!itemResult.found()) {
            error("No item frames found in hotbar, disabling...");
            toggle();
            return;
        }

        for (BlockPos blockPos : getSphere(mc.player.getBlockPos(), distance.get(), distance.get())) {
            if (mc.world.getBlockState(blockPos).getBlock() instanceof PistonBlock) {
                if (shouldPlace(blockPos)) positions.add(blockPos);
            }
        }

        for (BlockPos blockPos : positions) {
            if (!(mc.world.getBlockState(blockPos).getBlock() instanceof PistonBlock)) {
                positions.remove(blockPos);
                return;
            }

            Direction direction = mc.world.getBlockState(blockPos).get(FacingBlock.FACING);
            BlockPos placePos = getBlockPosFromDirection(direction, blockPos);
            BlockUtils.place(placePos, itemResult, rotate.get(), 50, true, true, swapBack.get());

            if (delay.get() != 0) {
                positions.clear();
                break;
            }
        }
    }

    private boolean shouldPlace(BlockPos pistonPos) {
        Direction direction = mc.world.getBlockState(pistonPos).get(FacingBlock.FACING);
        BlockPos iFramePos = getBlockPosFromDirection(direction, pistonPos);

        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof ItemFrameEntity) {
                BlockPos entityPos = new BlockPos(Math.floor(entity.getPos().x), Math.floor(entity.getPos().y), Math.floor(entity.getPos().z));
                if (iFramePos.equals(entityPos)) {
                    return false;
                }
            }
        }

        return true;
    }

    private static List<BlockPos> getSphere(BlockPos centerPos, int radius, int height) {
        blocks.clear();

        for (int i = centerPos.getX() - radius; i < centerPos.getX() + radius; i++) {
            for (int j = centerPos.getY() - height; j < centerPos.getY() + height; j++) {
                for (int k = centerPos.getZ() - radius; k < centerPos.getZ() + radius; k++) {
                    BlockPos pos = new BlockPos(i, j, k);
                    if (distanceBetween(centerPos, pos) <= radius && !blocks.contains(pos)) blocks.add(pos);
                }
            }
        }

        return blocks;
    }

    private static double distanceBetween(BlockPos pos1, BlockPos pos2) {
        double d = pos1.getX() - pos2.getX();
        double e = pos1.getY() - pos2.getY();
        double f = pos1.getZ() - pos2.getZ();
        return MathHelper.sqrt((float) (d * d + e * e + f * f));
    }

    private BlockPos getBlockPosFromDirection(Direction direction, BlockPos orginalPos) {
        return switch (direction) {
            case UP -> orginalPos.up();
            case DOWN -> orginalPos.down();
            case EAST -> orginalPos.east();
            case WEST -> orginalPos.west();
            case NORTH -> orginalPos.north();
            case SOUTH -> orginalPos.south();
        };
    }
}
