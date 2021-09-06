package mathax.client.legacy.systems.modules.combat;

import mathax.client.legacy.events.world.TickEvent;
import mathax.client.legacy.settings.BlockListSetting;
import mathax.client.legacy.settings.BoolSetting;
import mathax.client.legacy.settings.Setting;
import mathax.client.legacy.settings.SettingGroup;
import mathax.client.legacy.systems.modules.Categories;
import mathax.client.legacy.systems.modules.Module;
import mathax.client.legacy.utils.player.InvUtils;
import mathax.client.legacy.utils.player.PlayerUtils;
import mathax.client.legacy.utils.world.BlockUtils;
import mathax.client.legacy.bus.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

import java.util.Collections;
import java.util.List;

public class Surround extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> doubleHeight = sgGeneral.add(new BoolSetting.Builder()
        .name("double-height")
        .description("Places obsidian on top of the original surround blocks to prevent people from face-placing you.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> underHeight = sgGeneral.add(new BoolSetting.Builder()
        .name("under-height")
        .description("Places obsidian next to the block youre standing on and fixes surround bug.")
        .defaultValue(true)
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

    private final Setting<Boolean> chatInfo = sgGeneral.add(new BoolSetting.Builder()
        .name("chat-info")
        .description("Informs you in chat if toggled off.")
        .defaultValue(true)
        .visible(disableOnJump::get)
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

    // TODO: Make a render for Surround monkeys.
    private final BlockPos.Mutable blockPos = new BlockPos.Mutable();
    private boolean return_;

    public Surround() {
        super(Categories.Combat, Items.OBSIDIAN, "surround", "Surrounds you in blocks to prevent you from taking lots of damage.");
    }

    @Override
    public void onActivate() {
        if (center.get()) PlayerUtils.centerPlayer();
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if ((disableOnJump.get() && (mc.options.keyJump.isPressed() || mc.player.input.jumping)) || (disableOnYChange.get() && mc.player.prevY < mc.player.getY())) {
            if (chatInfo.get()) {
                info("You jumped, disabling...");
            }
            toggle();
            return;
        }

        if (onlyOnGround.get() && !mc.player.isOnGround()) return;
        if (onlyWhenSneaking.get() && !mc.options.keySneak.isPressed()) return;

        // Place
        return_ = false;

        // Bottom
        boolean p1 = place(0, -1, 0);
        if (return_) return;

        // Sides
        boolean underHeightPlaced = false;
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

        boolean p6 = place(1, 0, 0);
        if (return_) return;
        boolean p7 = place(-1, 0, 0);
        if (return_) return;
        boolean p8 = place(0, 0, 1);
        if (return_) return;
        boolean p9 = place(0, 0, -1);
        if (return_) return;

        // Sides up
        boolean doubleHeightPlaced = false;
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
        if (turnOff.get() && p1 && p6 && p7 && p8 && p7) {
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

        return false;
    }

    private void setBlockPos(int x, int y, int z) {
        blockPos.set(mc.player.getX() + x, mc.player.getY() + y, mc.player.getZ() + z);
    }
}
