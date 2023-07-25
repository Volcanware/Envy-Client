package envy.client.systems.modules.combat;

import envy.client.Envy;
import envy.client.eventbus.EventHandler;
import envy.client.eventbus.EventPriority;
import envy.client.events.render.Render3DEvent;
import envy.client.events.world.TickEvent;
import envy.client.mixin.AbstractBlockAccessor;
import envy.client.renderer.ShapeMode;
import envy.client.settings.*;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import envy.client.utils.misc.Pool;
import envy.client.utils.player.FindItemResult;
import envy.client.utils.player.InvUtils;
import envy.client.utils.render.color.Color;
import envy.client.utils.render.color.SettingColor;
import envy.client.utils.world.BlockIterator;
import envy.client.utils.world.BlockUtils;
import envy.client.utils.world.Dir;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

public class HoleFiller extends Module {
    private final Pool<Hole> holePool = new Pool<>(Hole::new);
    private final List<Hole> holes = new ArrayList<>();
    private final byte NULL = 0;

    private int timer;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");

    // General

    private final Setting<List<Block>> blocks = sgGeneral.add(new BlockListSetting.Builder()
        .name("blocks")
        .description("Which blocks can be used to fill holes.")
        .defaultValue(Blocks.OBSIDIAN)
        .build()
    );

    private final Setting<Integer> horizontalRadius = sgGeneral.add(new IntSetting.Builder()
        .name("horizontal-radius")
        .description("Horizontal radius in which to search for holes.")
        .defaultValue(4)
        .min(0)
        .sliderMax(6)
        .build()
    );

    private final Setting<Integer> verticalRadius = sgGeneral.add(new IntSetting.Builder()
        .name("vertical-radius")
        .description("Vertical radius in which to search for holes.")
        .defaultValue(4)
        .min(0)
        .sliderMax(6)
        .build()
    );


    private final Setting<Boolean> doubles = sgGeneral.add(new BoolSetting.Builder()
        .name("doubles")
        .description("Fills double holes.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Integer> placeDelay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("The ticks delay between placement.")
        .defaultValue(1)
        .min(0)
        .build()
    );

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
        .name("rotate")
        .description("Automatically rotates towards the holes being filled.")
        .defaultValue(true)
        .build()
    );

    // Render

    private final Setting<Boolean> render = sgRender.add(new BoolSetting.Builder()
        .name("render")
        .description("Renders an overlay where blocks will be placed.")
        .defaultValue(true)
        .build()
    );

    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode")
        .description("Determines how the shapes are rendered.")
        .defaultValue(ShapeMode.Both)
        .build()
    );

    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
        .name("side-color")
        .description("The side color of the target block rendering.")
        .defaultValue(new SettingColor(Envy.INSTANCE.MATHAX_BACKGROUND_COLOR.r, Envy.INSTANCE.MATHAX_BACKGROUND_COLOR.g, Envy.INSTANCE.MATHAX_BACKGROUND_COLOR.b, 75))
        .build()
    );

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
        .name("line-color")
        .description("The line color of the target block rendering.")
        .defaultValue(new SettingColor(Envy.INSTANCE.MATHAX_BACKGROUND_COLOR.r, Envy.INSTANCE.MATHAX_BACKGROUND_COLOR.g, Envy.INSTANCE.MATHAX_BACKGROUND_COLOR.b))
        .build()
    );

    private final Setting<SettingColor> nextSideColor = sgRender.add(new ColorSetting.Builder()
        .name("next-side-color")
        .description("The side color of the next block to be placed.")
        .defaultValue(new SettingColor(Envy.INSTANCE.MATHAX_COLOR.r, Envy.INSTANCE.MATHAX_COLOR.g, Envy.INSTANCE.MATHAX_COLOR.b, 75))
        .build()
    );

    private final Setting<SettingColor> nextLineColor = sgRender.add(new ColorSetting.Builder()
        .name("next-line-color")
        .description("The line color of the next block to be placed.")
        .defaultValue(new SettingColor(Envy.INSTANCE.MATHAX_COLOR.r, Envy.INSTANCE.MATHAX_COLOR.g, Envy.INSTANCE.MATHAX_COLOR.b))
        .build()
    );

    public HoleFiller() {
        super(Categories.Combat, Items.OBSIDIAN, "hole-filler", "Fills holes with specified blocks.");
    }

    @Override
    public boolean onActivate() {
        timer = 0;
        return false;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        for (Hole hole : holes) holePool.free(hole);
        holes.clear();

        FindItemResult block = InvUtils.findInHotbar(itemStack -> itemStack.getItem() instanceof BlockItem && blocks.get().contains(Block.getBlockFromItem(itemStack.getItem())));
        if (!block.found()) return;

        BlockIterator.register(horizontalRadius.get(), verticalRadius.get(), (blockPos, blockState) -> {
            if (!validHole(blockPos)) return;

            int bedrock = 0, obsidian = 0;
            Direction air = null;

            for (Direction direction : Direction.values()) {
                if (direction == Direction.UP) continue;

                BlockState state = mc.world.getBlockState(blockPos.offset(direction));

                if (state.getBlock() == Blocks.BEDROCK) bedrock++;
                else if (state.getBlock() == Blocks.OBSIDIAN) obsidian++;
                else if (direction == Direction.DOWN) return;
                else if (validHole(blockPos.offset(direction)) && air == null) {
                    for (Direction dir : Direction.values()) {
                        if (dir == direction.getOpposite() || dir == Direction.UP) continue;

                        BlockState blockState1 = mc.world.getBlockState(blockPos.offset(direction).offset(dir));

                        if (blockState1.getBlock() == Blocks.BEDROCK) bedrock++;
                        else if (blockState1.getBlock() == Blocks.OBSIDIAN) obsidian++;
                        else return;
                    }

                    air = direction;
                }
            }

            if (obsidian + bedrock == 5 && air == null) holes.add(holePool.get().set(blockPos, NULL));
            else if (obsidian + bedrock == 8 && doubles.get() && air != null) holes.add(holePool.get().set(blockPos, Dir.get(air)));
        });
    }

    @EventHandler
    private void onTickPost(TickEvent.Post event) {
        if (timer <= 0 && !holes.isEmpty()) {
            FindItemResult block = InvUtils.findInHotbar(itemStack -> itemStack.getItem() instanceof BlockItem && blocks.get().contains(Block.getBlockFromItem(itemStack.getItem())));

            BlockUtils.place(holes.get(0).blockPos, block, rotate.get(), 10, true);

            timer = placeDelay.get();
        }

        timer--;
    }

    private boolean validHole(BlockPos pos) {
        if (mc.player.getBlockPos().equals(pos)) return false;
        if (((AbstractBlockAccessor) mc.world.getBlockState(pos).getBlock()).isCollidable()) return false;
        return !((AbstractBlockAccessor) mc.world.getBlockState(pos.up()).getBlock()).isCollidable();
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onRender3D(Render3DEvent event) {
        if (!render.get()) return;

        for (Hole hole : holes) {
            boolean isFirst = hole == holes.get(0);

            Color side = isFirst ? nextSideColor.get() : sideColor.get();
            Color line = isFirst ? nextLineColor.get() : lineColor.get();

            event.renderer.box(hole.blockPos, side, line, shapeMode.get(), hole.exclude);
        }
    }

    private static class Hole {
        public BlockPos.Mutable blockPos = new BlockPos.Mutable();
        public byte exclude;

        public Hole set(BlockPos blockPos, byte exclude) {
            this.blockPos.set(blockPos);
            this.exclude = exclude;

            return this;
        }
    }
}
