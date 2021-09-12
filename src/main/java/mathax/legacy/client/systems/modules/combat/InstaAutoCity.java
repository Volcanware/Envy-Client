package mathax.legacy.client.systems.modules.combat;

import mathax.legacy.client.MatHaxLegacy;
import mathax.legacy.client.bus.EventHandler;
import mathax.legacy.client.events.render.Render3DEvent;
import mathax.legacy.client.renderer.ShapeMode;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.settings.BoolSetting;
import mathax.legacy.client.settings.ColorSetting;
import mathax.legacy.client.settings.DoubleSetting;
import mathax.legacy.client.settings.EnumSetting;
import mathax.legacy.client.settings.IntSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.utils.player.InvUtils;
import mathax.legacy.client.utils.player.Rotations;
import mathax.legacy.client.utils.render.color.SettingColor;
import mathax.legacy.client.utils.world.BlockUtils;
import mathax.legacy.client.utils.world.EnhancedBlockUtils;
import mathax.legacy.client.utils.world.CityUtils;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;

public class InstaAutoCity extends Module {
    private int delayLeft;
    private boolean mining;
    private BlockPos mineTarget;
    private PlayerEntity target;
    static final boolean assertionsDisabled = !InstaAutoCity.class.desiredAssertionStatus();
    private Direction direction;
    private int count;
    private BlockPos targetBlockPos;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");

    // General

    private final Setting<Double> range = sgGeneral.add(new DoubleSetting.Builder()
        .name("range")
        .description("The maximum range a city-able block will be found.")
        .defaultValue(5.0)
        .min(0.0)
        .sliderMax(20.0)
        .build()
    );

    private final Setting<Boolean> support = sgGeneral.add(new BoolSetting.Builder()
        .name("support")
        .description("If there is no block below a city block it will place one before mining.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> chatInfo = sgGeneral.add(new BoolSetting.Builder()
        .name("chat-info")
        .description("Sends a client-side message if you city a player.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("Delay between mining blocks in ticks.")
        .defaultValue(1)
        .min(0)
        .sliderMax(10)
        .build()
    );

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
        .name("rotate")
        .description("Sends rotation packets to the server when mining.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> swing = sgGeneral.add(new BoolSetting.Builder()
        .name("swing")
        .description("Only server side rotations.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> ironPickaxe = sgGeneral.add(new BoolSetting.Builder()
        .name("swing")
        .description("Only server side rotations.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> autoToggle = sgGeneral.add(new BoolSetting.Builder()
        .name("auto-toggle")
        .description("Makes the module toggle itself off.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Integer> toggle = sgGeneral.add(new IntSetting.Builder()
        .name("auto-toggle-delay")
        .description("Amount of ticks the block has to be air to auto toggle off.")
        .defaultValue(20)
        .min(0)
        .sliderMax(40)
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

    public InstaAutoCity() {
        super(Categories.Combat, Items.DIAMOND_PICKAXE, "insta-auto-city");
    }

    @Override
    public void onDeactivate() {
        if (mineTarget != null) {
            mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, mineTarget, direction));
        }
        mineTarget = null;
        target = null;
    }

    @Override
    public void onActivate() {
        count = 0;
        mining = false;
        target = CityUtils.getPlayerTarget(range.get() + 1.0);
        if (target != null && CityUtils.getTargetBlock(target) != null) {
            Vec3d vec3d = new Vec3d(mc.player.getPos().x, mc.player.getPos().y + 1.0, mc.player.getPos().z);
            Vec3d vec3d1 = new Vec3d(CityUtils.getTargetBlock(target).getX(), CityUtils.getTargetBlock(target).getY(), CityUtils.getTargetBlock(target).getZ());
            if (vec3d.distanceTo(vec3d1) <= range.get()) {
                mineTarget = CityUtils.getTargetBlock(target);
            }
        }
        if (mineTarget != null && target != null) {
            if (mc.player.squaredDistanceTo(mineTarget.getX(), mineTarget.getY(), mineTarget.getZ()) > range.get()) {
                if (autoToggle.get()) {
                    if (chatInfo.get()) {
                        info("Target block out of reach, disabling...");
                    }
                    toggle();
                    return;
                }
            }
            if (chatInfo.get()) {
                info("Attempting to city " + Formatting.WHITE + target.getGameProfile().getName() + Formatting.GRAY + "...");
            }
            targetBlockPos = target.getBlockPos();
            int n = InvUtils.findInHotbar(Items.BARRIER).getSlot();
            if (ironPickaxe.get() && n == -1) {
                n = InvUtils.findInHotbar(Items.IRON_PICKAXE).getSlot();
            }
            if (n == -1) {
                n = InvUtils.findInHotbar(Items.NETHERITE_PICKAXE).getSlot();
            }
            if (n == -1) {
                n = InvUtils.findInHotbar(Items.DIAMOND_PICKAXE).getSlot();
            }
            if (mc.player.getAbilities().creativeMode) {
                n = mc.player.getInventory().selectedSlot;
            }
            if (n == -1) {
                if (chatInfo.get()) {
                    info("No pickaxe found, disabling...");
                }
                toggle();
                return;
            }
            if (support.get()) {
                int n2 = InvUtils.findInHotbar((Item[]) new Item[]{Items.OBSIDIAN}).getSlot();
                BlockPos blockPos = mineTarget.down(1);
                if (!BlockUtils.canPlace(blockPos) && mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN && mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && chatInfo.get()) {
                    info("Couldn't place support block, mining anyway.");
                } else if (n2 == -1) {
                    if (chatInfo.get()) {
                        info("No obsidian found for support, mining anyway.");
                    }
                } else {
                    EnhancedBlockUtils.place(blockPos, Hand.MAIN_HAND, n2, rotate.get(), 0, true);
                }
            }
            mc.player.getInventory().selectedSlot = n;
        } else {
            mineTarget = null;
            target = null;
            if (autoToggle.get()) {
                if (chatInfo.get()) {
                    info("No target block found, disabling...");
                }
                toggle();
            }
        }
    }

    private void doMine() {
        --delayLeft;
        if (!mining) {
            if (rotate.get()) {
                Rotations.rotate(Rotations.getYaw(mineTarget), Rotations.getPitch(mineTarget));
            }
            if (swing.get()) {
                mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
            } else {
                mc.player.swingHand(Hand.MAIN_HAND);
            }
            mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, mineTarget, direction));
            mining = true;
        }
        if (delayLeft <= 0) {
            if (rotate.get()) {
                Rotations.rotate(Rotations.getYaw(mineTarget), Rotations.getPitch(mineTarget));
            }
            mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
            mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, mineTarget, direction));
            delayLeft = delay.get();
        }
    }

    public BlockPos getMineTarget() {
        return mineTarget != null ? mineTarget : null;
    }

    @EventHandler
    private void onTick(TickEvent.Pre pre) {
        if (!assertionsDisabled && mc.world == null) {
            throw new AssertionError();
        }
        if (autoToggle.get()) {
            direction = EnhancedBlockUtils.rayTraceCheck(mineTarget, true);
            if (!mc.world.isAir(mineTarget)) {
                doMine();
            } else {
                ++count;
            }
            if (target == null || !target.isAlive() || count >= toggle.get() || !mineTarget.isWithinDistance(mc.player.getPos(), range.get()) || target.getBlockPos() != targetBlockPos) {
                toggle();
            }
        } else {
            if (target == null) return;
            direction = EnhancedBlockUtils.rayTraceCheck(mineTarget, true);
            if (!mc.world.isAir(mineTarget)) {
                doMine();
            }
            if (target == null || !target.isAlive() || !mineTarget.isWithinDistance(mc.player.getPos(), range.get()) || target.getBlockPos() != targetBlockPos) {
                toggle();
            }
        }
    }

    @Override
    public String getInfoString() {
        return target != null ? target.getEntityName() : null;
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (!render.get()) return;
        if (mineTarget == null) return;
        event.renderer.box(mineTarget, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
    }
}
