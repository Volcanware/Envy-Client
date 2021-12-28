package mathax.legacy.client.systems.modules.combat;

import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.settings.BoolSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.utils.player.InvUtils;
import mathax.legacy.client.utils.world.BlockUtils;
import mathax.legacy.client.eventbus.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.item.Items;

public class AntiAnchor extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
        .name("rotate")
        .description("Makes you rotate when placing.")
        .defaultValue(true)
        .build()
    );

    // Render

    private final Setting<Boolean> swing = sgRender.add(new BoolSetting.Builder()
        .name("swing")
        .description("Swings your hand client-side when placing or interacting.")
        .defaultValue(true)
        .build()
    );

    public AntiAnchor() {
        super(Categories.Combat, Items.RESPAWN_ANCHOR, "anti-anchor", "Automatically prevents Anchor Aura by placing a slab on your head.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.world.getBlockState(mc.player.getBlockPos().up(2)).getBlock() == Blocks.RESPAWN_ANCHOR
            && mc.world.getBlockState(mc.player.getBlockPos().up()).getBlock() == Blocks.AIR) {

            BlockUtils.place(
                mc.player.getBlockPos().add(0, 1, 0),
                InvUtils.findInHotbar(itemStack -> Block.getBlockFromItem(itemStack.getItem()) instanceof SlabBlock),
                rotate.get(),
                15,
                swing.get(),
                false,
                true
            );
        }
    }
}
