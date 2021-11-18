package mathax.legacy.client.systems.modules.combat;

import mathax.legacy.client.MatHaxLegacy;
import mathax.legacy.client.events.render.Render3DEvent;
import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.renderer.ShapeMode;
import mathax.legacy.client.settings.*;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.systems.modules.Modules;
import mathax.legacy.client.systems.modules.movement.Blink;
import mathax.legacy.client.systems.modules.movement.Scaffold;
import mathax.legacy.client.utils.misc.KeyBind;
import mathax.legacy.client.utils.misc.Pool;
import mathax.legacy.client.utils.misc.Timer;
import mathax.legacy.client.utils.misc.ChatUtils;
import mathax.legacy.client.utils.player.InvUtils;
import mathax.legacy.client.utils.player.PlayerUtils;
import mathax.legacy.client.eventbus.EventHandler;
import mathax.legacy.client.utils.render.color.Color;
import mathax.legacy.client.utils.render.color.SettingColor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class SurroundPlus extends Module {
    private final Pool<Scaffold.RenderBlock> renderBlockPool = new Pool<>(Scaffold.RenderBlock::new);
    private final List<Scaffold.RenderBlock> renderBlocks = new ArrayList<>();

    private static final Timer surroundInstanceDelay = new Timer();

    private BlockPos lastPos = new BlockPos(0, -100, 0);

    private int timeToStart = 0;
    private int ticks = 0;

    private final SettingGroup sgHorizontalExpanding = settings.createGroup("Horizontal Expanding");
    private final SettingGroup sgVerticalExpanding = settings.createGroup("Vertical Expanding");
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");

    // Horizontal Expanding

    private final Setting<HorizontalMode> horizontalMode = sgHorizontalExpanding.add(new EnumSetting.Builder<HorizontalMode>()
        .name("mode")
        .description("Determines how big the surround is.")
        .defaultValue(HorizontalMode.Normal)
        .build()
    );

    private final Setting<KeyBind> bigKeyBind = sgHorizontalExpanding.add(new KeyBindSetting.Builder()
        .name("force-big")
        .description("Toggles big surround when held.")
        .build()
    );

    private final Setting<KeyBind> giantKeyBind = sgHorizontalExpanding.add(new KeyBindSetting.Builder()
        .name("force-giant")
        .description("Toggles giant surround when held.")
        .build()
    );

    // Vertical Expanding

    private final Setting<VerticalMode> verticalMode = sgVerticalExpanding.add(new EnumSetting.Builder<VerticalMode>()
        .name("vertical-mode")
        .description("Places obsidian on top or under of the original surround blocks to prevent people from face-placing you or anticheat from bugging you out.")
        .defaultValue(VerticalMode.None)
        .build()
    );

    private final Setting<KeyBind> underHeightKeyBind = sgVerticalExpanding.add(new KeyBindSetting.Builder()
        .name("force-under")
        .description("Toggles under height when held.")
        .build()
    );

    private final Setting<KeyBind> doubleHeightKeyBind = sgVerticalExpanding.add(new KeyBindSetting.Builder()
        .name("force-double")
        .description("Toggles double height when held.")
        .build()
    );

    // General

    private final Setting<Primary> primary = sgGeneral.add(new EnumSetting.Builder<Primary>()
        .name("primary-block")
        .description("Primary block to use.")
        .defaultValue(Primary.Obsidian)
        .build()
    );

    private final Setting<Boolean> allBlocks = sgGeneral.add(new BoolSetting.Builder()
        .name("blastproof-only")
        .description("Places blastproof blocks only.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("Delay between block placements in ticks.")
        .defaultValue(0)
        .sliderRange(0, 10)
        .build()
    );

    private final Setting<Boolean> center = sgGeneral.add(new BoolSetting.Builder()
        .name("center")
        .description("Teleports you to the center of the block.")
        .defaultValue(true)
        .build()
    );

    private final Setting<KeyBind> centerKeyBind = sgGeneral.add(new KeyBindSetting.Builder()
        .name("force-center")
        .description("Toggles center when held.")
        .build()
    );

    private final Setting<Boolean> onlyOnGround = sgGeneral.add(new BoolSetting.Builder()
        .name("only-on-ground")
        .description("Makes surround only work on ground.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> disableOnJump = sgGeneral.add(new BoolSetting.Builder()
        .name("jump-disable")
        .description("Automatically disables when you jump.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> disableOnYChange = sgGeneral.add(new BoolSetting.Builder()
        .name("y-change-disable")
        .description("Automatically disables when your y level (step, jumping, atc).")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> stayOnBlink = sgGeneral.add(new BoolSetting.Builder()
        .name("blinkers")
        .description("Surround stays on when you are in blink.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> placeOnCrystal = sgGeneral.add(new BoolSetting.Builder()
        .name("place-on-crystal")
        .description("Places the surround on end crystal placement.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
        .name("rotate")
        .description("Makes you rotate when placing.")
        .defaultValue(false)
        .build()
    );

    // Render

    private final Setting<Boolean> swing = sgRender.add(new BoolSetting.Builder()
        .name("swing")
        .description("Swings your hand client-side when placing.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> render = sgRender.add(new BoolSetting.Builder()
        .name("render")
        .description("Renders currently placed blocks.")
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
        .description("The side color of the placed block.")
        .defaultValue(new SettingColor(MatHaxLegacy.INSTANCE.MATHAX_COLOR.r, MatHaxLegacy.INSTANCE.MATHAX_COLOR.g, MatHaxLegacy.INSTANCE.MATHAX_COLOR.b, 50))
        .build()
    );

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
        .name("line-color")
        .description("The line color of the placed block.")
        .defaultValue(new SettingColor(MatHaxLegacy.INSTANCE.MATHAX_COLOR.r, MatHaxLegacy.INSTANCE.MATHAX_COLOR.g, MatHaxLegacy.INSTANCE.MATHAX_COLOR.b))
        .build()
    );

    public SurroundPlus() {
        super(Categories.Combat, Items.OBSIDIAN, "surround+", "Surrounds you in blocks to prevent you from taking lots of damage.");
    }

    @Override
    public void onActivate() {
        if (Modules.get().isActive(Surround.class)) {
            ChatUtils.sendMsg(this.hashCode(), "Surround+", Formatting.DARK_RED, Formatting.WHITE + "Surround" + Formatting.DARK_RED + " is already enabled, disabling...", Formatting.GRAY);
            toggle();
            return;
        }

        lastPos = (mc.player.isOnGround() ? PlayerUtils.roundBlockPos(mc.player.getPos()) : mc.player.getBlockPos());
        if (center.get()) PlayerUtils.centerPlayer();

        for (Scaffold.RenderBlock renderBlock : renderBlocks) renderBlockPool.free(renderBlock);
        renderBlocks.clear();
    }

    @Override
    public void onDeactivate() {
        ticks = 0;
        timeToStart = 0;

        for (Scaffold.RenderBlock renderBlock : renderBlocks) renderBlockPool.free(renderBlock);
        renderBlocks.clear();
    }

    @EventHandler
    private void onTick(final TickEvent.Pre event) {
        // Ticking fade animation
        renderBlocks.forEach(Scaffold.RenderBlock::tick);
        renderBlocks.removeIf(renderBlock -> renderBlock.ticks <= 0);

        if ((disableOnJump.get() && (mc.options.keyJump.isPressed() || mc.player.input.jumping)) || (disableOnYChange.get() && mc.player.prevY < mc.player.getY())) {
            ChatUtils.sendMsg(hashCode(), "Surround+", Formatting.DARK_RED, "You jumped, disabling...", Formatting.GRAY);
            toggle();
            return;
        }

        if ((centerKeyBind.get()).isPressed()) PlayerUtils.centerPlayer();

        final BlockPos roundedPos = PlayerUtils.roundBlockPos(mc.player.getPos());
        if (onlyOnGround.get() && !mc.player.isOnGround() && roundedPos.getY() <= lastPos.getY()) lastPos = PlayerUtils.roundBlockPos(mc.player.getPos());

        if (surroundInstanceDelay.passedMillis(timeToStart) && (mc.player.isOnGround() || !onlyOnGround.get())) {
            if (delay.get() != 0 && ticks++ % delay.get() != 0) return;
            if (!(Modules.get().get(Blink.class)).isActive() || !stayOnBlink.get()) {
                final AbstractClientPlayerEntity loc = mc.player;
                final BlockPos locRounded = PlayerUtils.roundBlockPos(loc.getPos());
                if (!lastPos.equals(loc.isOnGround() ? locRounded : loc.getBlockPos())) {
                    if (onlyOnGround.get() || loc.getPos().y > lastPos.getY() + 1.5 || ((Math.floor(loc.getPos().x) != lastPos.getX() || Math.floor(loc.getPos().z) != lastPos.getZ()) && loc.getPos().y > lastPos.getY() + 0.75) || (!mc.world.getBlockState(lastPos).getMaterial().isReplaceable() && loc.getBlockPos() != lastPos)) {
                        toggle();
                        return;
                    }

                    if (!onlyOnGround.get() && locRounded.getY() <= lastPos.getY()) lastPos = locRounded;
                }
            }

            int obbyIndex = findBlock();
            if (obbyIndex == -1) return;
            int prevSlot = mc.player.getInventory().selectedSlot;

            if (needsToPlace()) {
                for (BlockPos pos : getPositions()) {
                    if (mc.world.getBlockState(pos).getMaterial().isReplaceable()) mc.player.getInventory().selectedSlot = obbyIndex;
                    if (!mc.world.isOutOfHeightLimit(pos.getY()) && canPlace(pos)) renderBlocks.add(renderBlockPool.get().set(pos));
                    if (PlayerUtils.placeBlockMainHand(pos, rotate.get(), swing.get(), !onlyOnGround.get(), placeOnCrystal.get()) && delay.get() != 0) {
                        mc.player.getInventory().selectedSlot = prevSlot;
                        return;
                    }
                }

                mc.player.getInventory().selectedSlot = prevSlot;
            }
        }
    }

    private boolean canPlace(BlockPos pos) {
        BlockState state = mc.world.getBlockState(pos);
        if (state.isAir()) return true;
        else if (state.getMaterial().isLiquid()) return true;
        else return state.getMaterial().isReplaceable();
    }

    private List<BlockPos> getPositions() {
        final List<BlockPos> positions = new ArrayList<>();
        if (!onlyOnGround.get()) add(positions, lastPos.down());

        if ((verticalMode.get() == VerticalMode.Under || verticalMode.get() == VerticalMode.Both) || (underHeightKeyBind.get()).isPressed()) {
            add(positions, lastPos.north().down());
            add(positions, lastPos.east().down());
            add(positions, lastPos.south().down());
            add(positions, lastPos.west().down());
        }

        add(positions, lastPos.north());
        add(positions, lastPos.east());
        add(positions, lastPos.south());
        add(positions, lastPos.west());

        if ((verticalMode.get() == VerticalMode.Double || verticalMode.get() == VerticalMode.Both) || (doubleHeightKeyBind.get()).isPressed()) {
            add(positions, lastPos.north().up());
            add(positions, lastPos.east().up());
            add(positions, lastPos.south().up());
            add(positions, lastPos.west().up());
        }

        if (horizontalMode.get() != HorizontalMode.Normal || (bigKeyBind.get()).isPressed() || (bigKeyBind.get()).isPressed()) {
            if (mc.world.getBlockState(lastPos.north()).getBlock() != Blocks.BEDROCK) add(positions, lastPos.north(2));
            if (mc.world.getBlockState(lastPos.east()).getBlock() != Blocks.BEDROCK) add(positions, lastPos.east(2));
            if (mc.world.getBlockState(lastPos.south()).getBlock() != Blocks.BEDROCK) add(positions, lastPos.south(2));
            if (mc.world.getBlockState(lastPos.west()).getBlock() != Blocks.BEDROCK) add(positions, lastPos.west(2));
        }

        if (horizontalMode.get() == HorizontalMode.Giant || (giantKeyBind.get()).isPressed()) {
            if (mc.world.getBlockState(lastPos.north()).getBlock() != Blocks.BEDROCK || mc.world.getBlockState(lastPos.east()).getBlock() != Blocks.BEDROCK) add(positions, lastPos.north().east());
            if (mc.world.getBlockState(lastPos.east()).getBlock() != Blocks.BEDROCK || mc.world.getBlockState(lastPos.south()).getBlock() != Blocks.BEDROCK) add(positions, lastPos.east().south());
            if (mc.world.getBlockState(lastPos.south()).getBlock() != Blocks.BEDROCK || mc.world.getBlockState(lastPos.west()).getBlock() != Blocks.BEDROCK) add(positions, lastPos.south().west());
            if (mc.world.getBlockState(lastPos.west()).getBlock() != Blocks.BEDROCK || mc.world.getBlockState(lastPos.north()).getBlock() != Blocks.BEDROCK) add(positions, lastPos.west().north());
        }

        return positions;
    }

    private boolean needsToPlace() {
        return anyAir(lastPos.down(), lastPos.north(), lastPos.east(), lastPos.south(), lastPos.west(), lastPos.north().up(), lastPos.east().up(), lastPos.south().up(), lastPos.west().up(), lastPos.north(2), lastPos.east(2), lastPos.south(2), lastPos.west(2), lastPos.north().east(), lastPos.east().south(), lastPos.south().west(), lastPos.west().north());
    }

    private void add(final List<BlockPos> list, final BlockPos pos) {
        if (mc.world.getBlockState(pos).isAir() && allAir(pos.north(), pos.east(), pos.south(), pos.west(), pos.up(), pos.down()) && onlyOnGround.get()) list.add(pos.down());
        list.add(pos);
    }

    private boolean allAir(final BlockPos... pos) {
        return Arrays.stream(pos).allMatch(blockPos -> mc.world.getBlockState(blockPos).isAir());
    }

    private boolean anyAir(final BlockPos... pos) {
        return Arrays.stream(pos).anyMatch(blockPos -> mc.world.getBlockState(blockPos).isAir());
    }

    private Block primaryBlock() {
        Block index = null;
        if (primary.get() == Primary.Obsidian) index = Blocks.OBSIDIAN;
        else if (primary.get() == Primary.Ender_Chest) index = Blocks.ENDER_CHEST;
        else if (primary.get() == Primary.Crying_Obsidian) index = Blocks.CRYING_OBSIDIAN;
        else if (primary.get() == Primary.Netherite_Block) index = Blocks.NETHERITE_BLOCK;
        else if (primary.get() == Primary.Ancient_Debris) index = Blocks.ANCIENT_DEBRIS;
        else if (primary.get() == Primary.Respawn_Anchor) index = Blocks.RESPAWN_ANCHOR;
        else if (primary.get() == Primary.Anvil) index = Blocks.ANVIL;
        return index;
    }

    private int findBlock() {
        int index = InvUtils.findBlockInHotbar(primaryBlock());
        if (index == -1 && allBlocks.get()) {
            index = InvUtils.findBlockInHotbar(Blocks.OBSIDIAN);
            if (index == -1) index = InvUtils.findBlockInHotbar(Blocks.ENDER_CHEST);
            if (index == -1) index = InvUtils.findBlockInHotbar(Blocks.CRYING_OBSIDIAN);
            if (index == -1) index = InvUtils.findBlockInHotbar(Blocks.NETHERITE_BLOCK);
            if (index == -1) index = InvUtils.findBlockInHotbar(Blocks.ANCIENT_DEBRIS);
            if (index == -1) index = InvUtils.findBlockInHotbar(Blocks.RESPAWN_ANCHOR);
            if (index == -1) index = InvUtils.findBlockInHotbar(Blocks.ANVIL);
        }

        return index;
    }

    public enum HorizontalMode {
        Normal,
        Big,
        Giant
    }

    public enum VerticalMode {
        Under,
        Double,
        Both,
        None
    }

    public enum Primary {
        Obsidian,
        Ender_Chest,
        Crying_Obsidian,
        Netherite_Block,
        Ancient_Debris,
        Respawn_Anchor,
        Anvil;

        @Override
        public String toString() {
            return super.toString().replace("_", " ");
        }
    }

    // Rendering

    @EventHandler
    private void onRender3D(Render3DEvent event) {
        if (!render.get()) return;
        renderBlocks.sort(Comparator.comparingInt(o -> -o.ticks));
        renderBlocks.forEach(renderBlock -> renderBlock.render(event, sideColor.get(), lineColor.get(), shapeMode.get()));
    }

    public static class RenderBlock {
        public BlockPos.Mutable pos = new BlockPos.Mutable();
        public int ticks;

        public RenderBlock set(BlockPos blockPos) {
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
}
