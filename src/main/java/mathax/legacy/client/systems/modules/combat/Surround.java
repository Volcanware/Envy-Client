package mathax.legacy.client.systems.modules.combat;

import mathax.legacy.client.MatHaxLegacy;
import mathax.legacy.client.events.render.Render3DEvent;
import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.renderer.ShapeMode;
import mathax.legacy.client.settings.*;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.utils.misc.Pool;
import mathax.legacy.client.utils.player.ChatUtils;
import mathax.legacy.client.utils.player.InvUtils;
import mathax.legacy.client.utils.player.PlayerUtils;
import mathax.legacy.client.utils.render.color.Color;
import mathax.legacy.client.utils.render.color.SettingColor;
import mathax.legacy.client.utils.world.BlockUtils;
import mathax.legacy.client.bus.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Surround extends Module {
    private final Pool<RenderSurroundBlock> renderSurroundBlockPool = new Pool<>(RenderSurroundBlock::new);
    private final List<RenderSurroundBlock> renderSurroundBlocks = new ArrayList<>();
    private final BlockPos.Mutable blockPos = new BlockPos.Mutable();
    private boolean return_;
    boolean underHeightPlaced = false;
    boolean doubleHeightPlaced = false;
    private boolean p1;
    private boolean p6;
    private boolean p7;
    private boolean p8;
    private boolean p9;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");

    private final Setting<Boolean> underHeight = sgGeneral.add(new BoolSetting.Builder()
        .name("under-height")
        .description("Places obsidian next to the block you are standing on. (Bypasses some anti cheats)")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> doubleHeight = sgGeneral.add(new BoolSetting.Builder()
        .name("double-height")
        .description("Places obsidian on top of the original surround blocks to prevent people from face-placing you.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> onlyOnGround = sgGeneral.add(new BoolSetting.Builder()
        .name("only-on-ground")
        .description("Works only when you standing on blocks.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> onlyWhenSneaking = sgGeneral.add(new BoolSetting.Builder()
        .name("only-when-sneaking")
        .description("Places blocks only after sneaking.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> turnOff = sgGeneral.add(new BoolSetting.Builder()
        .name("turn-off")
        .description("Toggles off when all blocks are placed.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> center = sgGeneral.add(new BoolSetting.Builder()
        .name("center")
        .description("Teleports you to the center of the block.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> disableOnJump = sgGeneral.add(new BoolSetting.Builder()
        .name("disable-on-jump")
        .description("Automatically disables when you jump.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> disableOnYChange = sgGeneral.add(new BoolSetting.Builder()
        .name("disable-on-y-change")
        .description("Automatically disables when your y level (step, jumping, atc).")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
        .name("rotate")
        .description("Automatically faces towards the obsidian being placed.")
        .defaultValue(true)
        .build()
    );

    private final Setting<List<Block>> blocks = sgGeneral.add(new BlockListSetting.Builder()
        .name("block")
        .description("What blocks to use for surround.")
        .defaultValue(Collections.singletonList(Blocks.OBSIDIAN))
        .filter(this::blockFilter)
        .build()
    );

    // Render

    private final Setting<Boolean> render = sgRender.add(new BoolSetting.Builder()
        .name("render")
        .description("Renders a block overlay where the obsidian will be placed.")
        .defaultValue(true)
        .build()
    );

    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode")
        .description("How the shapes are rendered.")
        .defaultValue(ShapeMode.Both)
        .build()
    );

    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
        .name("side-color")
        .description("The color of the sides of the blocks being rendered.")
        .defaultValue(new SettingColor(MatHaxLegacy.INSTANCE.MATHAX_COLOR.r, MatHaxLegacy.INSTANCE.MATHAX_COLOR.g, MatHaxLegacy.INSTANCE.MATHAX_COLOR.b, 50))
        .build()
    );

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
        .name("line-color")
        .description("The color of the lines of the blocks being rendered.")
        .defaultValue(new SettingColor(MatHaxLegacy.INSTANCE.MATHAX_COLOR.r, MatHaxLegacy.INSTANCE.MATHAX_COLOR.g, MatHaxLegacy.INSTANCE.MATHAX_COLOR.b, 255))
        .build()
    );

    public Surround() {
        super(Categories.Combat, Items.OBSIDIAN, "surround", "Surrounds you in blocks to prevent you from taking lots of damage");
    }

    @Override
    public void onActivate() {
        if (center.get()) PlayerUtils.centerPlayer();

        for (RenderSurroundBlock renderSurroundBlock : renderSurroundBlocks) renderSurroundBlockPool.free(renderSurroundBlock);
        renderSurroundBlocks.clear();
    }

    @Override
    public void onDeactivate() {
        for (RenderSurroundBlock renderSurroundBlock : renderSurroundBlocks) renderSurroundBlockPool.free(renderSurroundBlock);
        renderSurroundBlocks.clear();
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        // Ticking fade animation
        renderSurroundBlocks.forEach(RenderSurroundBlock::tick);
        renderSurroundBlocks.removeIf(renderSurroundBlock -> renderSurroundBlock.ticks <= 0);

        if ((disableOnJump.get() && (mc.options.keyJump.isPressed() || mc.player.input.jumping)) || (disableOnYChange.get() && mc.player.prevY < mc.player.getY())) {
            ChatUtils.sendMsg(this.hashCode(), "Surround", Formatting.DARK_RED, "You jumped, disabling...", Formatting.GRAY);
            toggle();
            return;
        }

        if (onlyOnGround.get() && !mc.player.isOnGround()) return;
        if (onlyWhenSneaking.get() && !mc.options.keySneak.isPressed()) return;

        // Place
        return_ = false;

        // Bottom
        p1 = place(0, -1, 0);
        if (return_) return;

        // Under height
        underHeightPlaced = false;
        if (underHeight.get()) {
            boolean p2 = place(1, -1, 0);
            if (return_) return;
            boolean p3 = place(-1, -1, 0);
            if (return_) return;
            boolean p4 = place(0, -1, 1);
            if (return_) return;
            boolean p5 = place(0, -1, -1);
            if (return_) return;

            if (p2 && p3 && p4 && p5) underHeightPlaced = true;
        }

        // Sides
        p6 = place(1, 0, 0);
        if (return_) return;
        p7 = place(-1, 0, 0);
        if (return_) return;
        p8 = place(0, 0, 1);
        if (return_) return;
        p9 = place(0, 0, -1);
        if (return_) return;

        // Sides up
        doubleHeightPlaced = false;
        if (doubleHeight.get()) {
            boolean p10 = place(1, 1, 0);
            if (return_) return;
            boolean p11 = place(-1, 1, 0);
            if (return_) return;
            boolean p12 = place(0, 1, 1);
            if (return_) return;
            boolean p13 = place(0, 1, -1);
            if (return_) return;

            if (p10 && p11 && p12 && p13) doubleHeightPlaced = true;
        }

        // Auto turn off
        if (turnOff.get() && p1 && p6 && p7 && p8 &&p9) {
            if (underHeightPlaced || !underHeight.get()) toggle();
            if (doubleHeightPlaced || !doubleHeight.get()) toggle();
        }
    }

    private boolean blockFilter(Block block) {
        return block == Blocks.OBSIDIAN ||
            block == Blocks.CRYING_OBSIDIAN ||
            block == Blocks.NETHERITE_BLOCK ||
            block == Blocks.ENDER_CHEST ||
            block == Blocks.RESPAWN_ANCHOR;
    }

    private boolean place(int x, int y, int z) {
        setBlockPos(x, y, z);
        BlockState blockState = mc.world.getBlockState(blockPos);

        if (!blockState.getMaterial().isReplaceable()) return true;

        if (BlockUtils.place(blockPos, InvUtils.findInHotbar(itemStack -> blocks.get().contains(Block.getBlockFromItem(itemStack.getItem()))), rotate.get(), 100, true)) {
            return_ = true;
        }

        // Render block if was placed
        renderSurroundBlocks.add(renderSurroundBlockPool.get().set(blockPos));

        return false;
    }

    private void setBlockPos(int x, int y, int z) {
        blockPos.set(mc.player.getX() + x, mc.player.getY() + y, mc.player.getZ() + z);
    }

    public static class RenderSurroundBlock {
        public BlockPos.Mutable pos = new BlockPos.Mutable();
        public int ticks;

        public RenderSurroundBlock set(BlockPos blockPos) {
            pos.set(blockPos);
            ticks = 8;

            return this;
        }

        public void tick() {
            ticks--;
        }

        public void render(Render3DEvent event, Color sides, Color lines, ShapeMode shapeMode) {
            int preSideA = sides.a;
            int preLineA = lines.a;

            sides.a *= (double) ticks / 8;
            lines.a *= (double) ticks / 8;

            event.renderer.box(pos, sides, lines, shapeMode, 0);

            sides.a = preSideA;
            lines.a = preLineA;
        }
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (!render.get() || blockPos == null || blockPos.getY() > 256) return;
        if (p1 && p6 && p7 && p8 &&p9) {
            if (underHeightPlaced || !underHeight.get()) return;
            if (doubleHeightPlaced || !doubleHeight.get()) return;
        }
        renderSurroundBlocks.sort(Comparator.comparingInt(o -> -o.ticks));
        renderSurroundBlocks.forEach(renderSurroundBlock -> renderSurroundBlock.render(event, sideColor.get(), lineColor.get(), shapeMode.get()));
        event.renderer.box(blockPos, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
    }
}
