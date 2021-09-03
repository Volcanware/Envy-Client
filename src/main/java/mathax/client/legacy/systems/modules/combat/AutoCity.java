package mathax.client.legacy.systems.modules.combat;

import mathax.client.legacy.events.render.Render3DEvent;
import mathax.client.legacy.events.world.TickEvent;
import mathax.client.legacy.renderer.ShapeMode;
import mathax.client.legacy.settings.*;
import mathax.client.legacy.systems.modules.Categories;
import mathax.client.legacy.systems.modules.Module;
import mathax.client.legacy.utils.entity.EntityUtils;
import mathax.client.legacy.utils.entity.SortPriority;
import mathax.client.legacy.utils.entity.TargetUtils;
import mathax.client.legacy.utils.player.*;
import mathax.client.legacy.utils.render.color.SettingColor;
import mathax.client.legacy.utils.world.BlockUtils;
import mathax.client.legacy.bus.EventHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class AutoCity extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");

    private final Setting<Double> targetRange = sgGeneral.add(new DoubleSetting.Builder()
        .name("target-range")
        .description("The radius in which players get targeted.")
        .defaultValue(4)
        .min(0)
        .sliderMax(5)
        .build()
    );

    private final Setting<Boolean> support = sgGeneral.add(new BoolSetting.Builder()
        .name("support")
        .description("If there is no block below a city block it will place one before mining.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
        .name("rotate")
        .description("Automatically rotates you towards the city block.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> selfToggle = sgGeneral.add(new BoolSetting.Builder()
        .name("self-toggle")
        .description("Automatically toggles off after activation.")
        .defaultValue(true)
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
        .defaultValue(new SettingColor(230, 75, 100, 50))
        .build()
    );

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
        .name("line-color")
        .description("The color of the lines of the blocks being rendered.")
        .defaultValue(new SettingColor(230, 75, 100, 255))
        .build()
    );

    private PlayerEntity target;
    private BlockPos mineTarget;
    private boolean sentMessage;

    public AutoCity() {
        super(Categories.Combat, "auto-city", "Automatically cities a target by mining the nearest obsidian next to them.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (TargetUtils.isBadTarget(target, targetRange.get())) {
            PlayerEntity search = TargetUtils.getPlayerTarget(targetRange.get(), SortPriority.LowestDistance);
            if (search != target) sentMessage = false;
            target = search;
        }

        if (TargetUtils.isBadTarget(target, targetRange.get())) {
            target = null;
            mineTarget = null;
            if (selfToggle.get()) toggle();
            return;
        }

        mineTarget = EntityUtils.getCityBlock(target);

        if (mineTarget == null) {
            if (selfToggle.get()) {
                ChatUtils.error("Auto City", "No target block found, disabling...");
                toggle();
            }
            target = null;
            return;
        }

        if (PlayerUtils.distanceTo(mineTarget) > mc.interactionManager.getReachDistance() && selfToggle.get()) {
            ChatUtils.error("Auto City", "Target block out of reach, disabling...");
            toggle();
            return;
        }

        if (!sentMessage) {
            info("Auto City", "Attempting to city %s.", target.getEntityName());
            sentMessage = true;
        }

        FindItemResult pickaxe = InvUtils.find(itemStack -> itemStack.getItem() == Items.DIAMOND_PICKAXE || itemStack.getItem() == Items.NETHERITE_PICKAXE);

        if (!pickaxe.isHotbar()) {
            if (selfToggle.get()) {
                ChatUtils.error("Auto City", "No pickaxe found, disabling...");
                toggle();
            }
            return;
        }

        if (support.get()) {
            BlockUtils.place(mineTarget.down(1), InvUtils.findInHotbar(Items.OBSIDIAN), rotate.get(), 0, true);
        }

        InvUtils.swap(pickaxe.getSlot(), false);

        if (rotate.get()) Rotations.rotate(Rotations.getYaw(mineTarget), Rotations.getPitch(mineTarget), () -> mine(mineTarget));
        else mine(mineTarget);

        if (selfToggle.get()) toggle();
    }

    private void mine(BlockPos blockPos) {
        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, blockPos, Direction.UP));
        mc.player.swingHand(Hand.MAIN_HAND);
        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, blockPos, Direction.UP));
    }

    @Override
    public String getInfoString() {
        return EntityUtils.getName(target);
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (!render.get() || mineTarget == null) return;
        event.renderer.box(mineTarget, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
    }
}
