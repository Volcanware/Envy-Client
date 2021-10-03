package mathax.legacy.client.systems.modules.combat;

import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.utils.entity.SortPriority;
import mathax.legacy.client.utils.entity.TargetUtils;
import mathax.legacy.client.utils.player.InvUtils;
import mathax.legacy.client.utils.world.BlockUtils;
import mathax.legacy.client.eventbus.EventHandler;
import mathax.legacy.client.settings.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;

public class AutoWeb extends Module {
    private PlayerEntity target = null;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> range = sgGeneral.add(new DoubleSetting.Builder()
        .name("target-range")
        .description("The maximum distance to target players.")
        .defaultValue(4)
        .min(0)
        .build()
    );

    private final Setting<SortPriority> priority = sgGeneral.add(new EnumSetting.Builder<SortPriority>()
        .name("target-priority")
        .description("How to select the player to target.")
        .defaultValue(SortPriority.LowestDistance)
        .build()
    );

    private final Setting<Boolean> doubles = sgGeneral.add(new BoolSetting.Builder()
        .name("doubles")
        .description("Places webs in the target's upper hitbox as well as the lower hitbox.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
        .name("rotate")
        .description("Rotates towards the webs when placing.")
        .defaultValue(true)
        .build()
    );

    public AutoWeb() {
        super(Categories.Combat, Items.COBWEB, "auto-web", "Automatically places webs on other players.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (TargetUtils.isBadTarget(target, range.get())) {
            target = TargetUtils.getPlayerTarget(range.get(), priority.get());
        }
        if (TargetUtils.isBadTarget(target, range.get())) return;

        BlockUtils.place(target.getBlockPos(), InvUtils.findInHotbar(Items.COBWEB), rotate.get(), 0, false);

        if (doubles.get()) {
            BlockUtils.place(target.getBlockPos().add(0, 1, 0), InvUtils.findInHotbar(Items.COBWEB), rotate.get(), 0, false);
        }
    }
}
