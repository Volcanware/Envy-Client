package mathax.legacy.client.systems.modules.combat;

import mathax.legacy.client.eventbus.EventHandler;
import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.settings.BlockListSetting;
import mathax.legacy.client.settings.BoolSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.utils.player.InvUtils;
import mathax.legacy.client.utils.player.PlayerUtils;
import mathax.legacy.client.utils.world.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

import java.util.Collections;
import java.util.List;

public class AntiCEVBreaker extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Boolean> placeThingsIn = sgGeneral.add(new BoolSetting.Builder()
        .name("place-things-in")
        .description("Places things in you.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> placeThingsTop = sgGeneral.add(new BoolSetting.Builder()
        .name("place-things-top")
        .description("Places things above you.")
        .defaultValue(false)
        .build());

    private final Setting<Boolean> placeThingsTop2 = sgGeneral.add(new BoolSetting.Builder()
        .name("place-things-2-top")
        .description("Places things 2 blocks on top.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> placeThingsTop3 = sgGeneral.add(new BoolSetting.Builder()
        .name("place-things-3-top")
        .description("Places things 3 blocks on top.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> onlyInHole = sgGeneral.add(new BoolSetting.Builder()
        .name("only-in-hole")
        .description("Only functions when you are standing in a hole.")
        .defaultValue(true)
        .build()
    );

    private final Setting<List<Block>> blocks = sgGeneral.add(new BlockListSetting.Builder()
        .name("block")
        .description("What blocks to use for Anti CEV Breaker.")
        .defaultValue(Collections.singletonList(Blocks.TRIPWIRE))
        .filter(this::blockFilter)
        .build()
    );


    public AntiCEVBreaker() {
        super(Categories.Combat, Items.OBSIDIAN, "anti-cev-breaker", "Places buttons,pressure plates, strings to prevent you getting memed on.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (onlyInHole.get() && !PlayerUtils.isInHole(true)) return;
        if (placeThingsIn.get()) place(mc.player.getBlockPos().up(1));
        if (placeThingsTop.get()) place(mc.player.getBlockPos().up(2));
        if (placeThingsTop2.get()) place(mc.player.getBlockPos().up(3));
        if (placeThingsTop3.get()) place(mc.player.getBlockPos().up(4));
    }

    private boolean blockFilter(Block block) {
        return block == Blocks.ACACIA_PRESSURE_PLATE || block == Blocks.BIRCH_PRESSURE_PLATE || block == Blocks.CRIMSON_PRESSURE_PLATE || block == Blocks.DARK_OAK_PRESSURE_PLATE || block == Blocks.JUNGLE_PRESSURE_PLATE || block == Blocks.OAK_PRESSURE_PLATE || block == Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE || block == Blocks.SPRUCE_PRESSURE_PLATE || block == Blocks.STONE_PRESSURE_PLATE || block == Blocks.WARPED_PRESSURE_PLATE || block == Blocks.ACACIA_BUTTON || block == Blocks.BIRCH_BUTTON || block == Blocks.CRIMSON_BUTTON || block == Blocks.DARK_OAK_BUTTON || block == Blocks.JUNGLE_BUTTON || block == Blocks.OAK_BUTTON || block == Blocks.POLISHED_BLACKSTONE_BUTTON || block == Blocks.SPRUCE_BUTTON || block == Blocks.STONE_BUTTON || block == Blocks.WARPED_BUTTON || block == Blocks.TRIPWIRE;
    }

    private void place(BlockPos blockPos) {
        BlockUtils.place(blockPos, InvUtils.findInHotbar(itemStack -> (blocks.get()).contains(Block.getBlockFromItem(itemStack.getItem()))), 50, false);
    }
}
