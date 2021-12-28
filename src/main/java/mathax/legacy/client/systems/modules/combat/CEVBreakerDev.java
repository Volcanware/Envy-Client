package mathax.legacy.client.systems.modules.combat;

import mathax.legacy.client.MatHaxLegacy;
import mathax.legacy.client.eventbus.EventHandler;
import mathax.legacy.client.events.render.Render3DEvent;
import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.renderer.ShapeMode;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.utils.Utils;
import mathax.legacy.client.utils.entity.EntityUtils;
import mathax.legacy.client.utils.entity.SortPriority;
import mathax.legacy.client.utils.entity.TargetUtils;
import mathax.legacy.client.utils.player.*;
import mathax.legacy.client.utils.render.color.SettingColor;
import mathax.legacy.client.utils.world.BlockUtils;
import mathax.legacy.client.settings.*;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

public class CEVBreakerDev extends Module {
    private final List<BlockPos> placePositions = new ArrayList<>();

    private BlockPos obsidianPosition;

    private int crystalTimer;
    private int timer;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgTrap = settings.createGroup("Trap");
    private final SettingGroup sgPause = settings.createGroup("Pause");
    private final SettingGroup sgRender = settings.createGroup("Render");

    // General

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("How many ticks to wait between block placements.")
        .defaultValue(4)
        .min(0)
        .sliderRange(0, 100)
        .build()
    );

    private final Setting<Integer> crystalDelay = sgGeneral.add(new IntSetting.Builder()
        .name("break-delay")
        .description("How many ticks to wait before breaking a crystal.")
        .defaultValue(4)
        .min(0)
        .min(0)
        .sliderRange(0, 100)
        .build()
    );

    public final Setting<Double> targetRange = sgGeneral.add(new DoubleSetting.Builder()
        .name("range")
        .description("The radius players can be in to be targeted.")
        .defaultValue(5)
        .sliderRange(0, 10)
        .build()
    );

    private final Setting<SortPriority> priority = sgGeneral.add(new EnumSetting.Builder<SortPriority>()
        .name("target-priority")
        .description("How to select the player to target.")
        .defaultValue(SortPriority.Lowest_Distance)
        .build()
    );

    private final Setting<Boolean> antiSuicide = sgGeneral.add(new BoolSetting.Builder()
        .name("anti-suicide")
        .description("Will not place and break crystals if they will kill you.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Double> maxDamage = sgGeneral.add(new DoubleSetting.Builder()
        .name("max-damage")
        .description("Maximum damage crystals can deal to yourself.")
        .defaultValue(8)
        .range(0, 36)
        .sliderMax(36)
        .visible(antiSuicide::get)
        .build()
    );

    private final Setting<Boolean> silentSwitch = sgGeneral.add(new BoolSetting.Builder()
        .name("silent-switch")
        .description("Changes slots only clientside.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> packetMine = sgGeneral.add(new BoolSetting.Builder()
        .name("packet-mine")
        .description("Will mine the blocks using packets.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> interfering = sgGeneral.add(new BoolSetting.Builder()
        .name("break-interfering")
        .description("Breaks blocks that prevent the target from taking damage.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> burrow = sgGeneral.add(new BoolSetting.Builder()
        .name("break-burrow")
        .description("Breaks the burrow block from the target.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
        .name("rotate")
        .description("Sends rotation packets to the server when placing.")
        .defaultValue(true)
        .build()
    );

    // Trap

    private final Setting<Boolean> trap = sgTrap.add(new BoolSetting.Builder()
        .name("trap")
        .description("Traps the target before cev-ing them.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> instaTrap = sgTrap.add(new BoolSetting.Builder()
        .name("insta-trap")
        .description("Instaplaces the trap-blocks around the target.")
        .defaultValue(false)
        .visible(trap::get)
        .build()
    );

    private final Setting<Mode> trapMode = sgTrap.add(new EnumSetting.Builder<Mode>()
        .name("trap-mode")
        .description("Which blocks to place around the target.")
        .defaultValue(Mode.Face)
        .visible(trap::get)
        .build()
    );

    // Pause

    private final Setting<Boolean> eatPause = sgPause.add(new BoolSetting.Builder()
        .name("pause-on-eat")
        .description("Pauses cev breaker when eating.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> drinkPause = sgPause.add(new BoolSetting.Builder()
        .name("pause-on-drink")
        .description("Pauses cev breaker when drinking.")
        .defaultValue(true)
        .build()
    );

    // Render

    private final Setting<Boolean> renderSwing = sgRender.add(new BoolSetting.Builder()
        .name("swing")
        .description("Renders a hand swing animation.")
        .defaultValue(true)
        .build()
    );

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
        .visible(render::get)
        .build()
    );

    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
        .name("side-color")
        .description("The color of the sides of the blocks being rendered.")
        .defaultValue(new SettingColor(MatHaxLegacy.INSTANCE.MATHAX_COLOR.r, MatHaxLegacy.INSTANCE.MATHAX_COLOR.g, MatHaxLegacy.INSTANCE.MATHAX_COLOR.b, 75))
        .visible(render::get)
        .build()
    );

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
        .name("line-color")
        .description("The color of the lines of the blocks being rendered.")
        .defaultValue(new SettingColor(MatHaxLegacy.INSTANCE.MATHAX_COLOR.r, MatHaxLegacy.INSTANCE.MATHAX_COLOR.g, MatHaxLegacy.INSTANCE.MATHAX_COLOR.b))
        .visible(render::get)
        .build()
    );

    public CEVBreakerDev() {
        super(Categories.Combat, Items.END_CRYSTAL, "cev-breaker", "Places and explodes crystals on top of the target.");
    }

    @Override
    public void onActivate() {
        obsidianPosition = null;
        crystalTimer = crystalDelay.get() + 1;
        timer = delay.get() + 1;
        placePositions.clear();
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        PlayerEntity target = TargetUtils.getPlayerTarget(targetRange.get(), priority.get());

        obsidianPosition = null;

        FindItemResult obby = InvUtils.findInHotbar(Items.OBSIDIAN);
        FindItemResult pick = InvUtils.findInHotbar(stack -> stack.getItem() == Items.DIAMOND_PICKAXE || stack.getItem() == Items.NETHERITE_PICKAXE);
        FindItemResult crystal = InvUtils.findInHotbar(Items.END_CRYSTAL);

        if (!obby.found()) {
            error("Can't find obsidian in your hotbar, disabling...");
            toggle();
            return;
        }

        if (!pick.found()) {
            error("Can't find a pickaxe in your hotbar, disabling...");
            toggle();
            return;
        }

        if (!crystal.found()) {
            error("Can't find crystals in your hotbar, disabling...");
            toggle();
            return;
        }

        if (PlayerUtils.shouldPause(false, eatPause.get(), drinkPause.get())) return;

        if (target != null) {
            BlockPos pos = target.getBlockPos().up(2);

            // Trap

            if (trap.get()) {
                fillPlaceArray(target);

                for (int i = 0; i < 3; i++) {
                    if (!placePositions.isEmpty()) {
                        if (instaTrap.get()) {
                            if (BlockUtils.place(placePositions.get(placePositions.size() - 1), obby, rotate.get(), 50, true)) placePositions.remove(placePositions.get(placePositions.size() - 1));
                        } else {
                            if (BlockUtils.place(placePositions.get(placePositions.size() - 1), obby, rotate.get(), 50, true)) {
                                placePositions.remove(placePositions.get(placePositions.size() - 1));
                                return;
                            }
                        }
                    }
                }
            }

            // Switch to pickaxe (fix)

            if (BlockUtils.getBlock(obsidianPosition) == Blocks.OBSIDIAN && getCrystal(pos) != null && mc.player.getInventory().selectedSlot != pick.getSlot()) {
                InvUtils.swap2(pick.getSlot());
                return;
            }

            // Interfering

            if (interfering.get()) {
                if (!BlockUtils.getBlockState(target.getBlockPos().up()).isAir()) {
                    mine(target.getBlockPos().up(), pick);
                    obsidianPosition = target.getBlockPos().up();
                    return;
                }

                if (!BlockUtils.getBlockState(pos.up()).isAir()) {
                    mine(pos.up(), pick);
                    obsidianPosition = pos.up();
                    return;
                }
            }

            if (burrow.get() && !BlockUtils.getBlockState(target.getBlockPos()).isAir()) {
                mine(target.getBlockPos(), pick);
                obsidianPosition = target.getBlockPos();
                return;
            }

            if (antiSuicide.get()) {
                double selfDamage = DamageUtils.crystalDamage(mc.player, Utils.vec3d(pos.up()), false, pos.up(), true);
                if (selfDamage > maxDamage.get() || (antiSuicide.get() && selfDamage >= EntityUtils.getTotalHealth(mc.player))) return;
            }

            if (BlockUtils.getBlockState(pos).isAir() && getCrystal(pos) == null) {
                if (delay.get() == 0 || timer > delay.get()) {
                    for (Entity entity : mc.world.getEntities()) {
                        if (entity instanceof EndCrystalEntity && BlockUtils.distanceBetweenXZ(pos, entity.getBlockPos()) <= 1.5) {
                            attack((EndCrystalEntity) entity);
                            return;
                        }
                    }

                    BlockUtils.place(pos, obby, rotate.get(), 50);
                    obsidianPosition = pos;
                    timer = delay.get() + 1;
                    crystalTimer = crystalDelay.get() + 1;
                } else timer++;
            }

            if (BlockUtils.getBlock(pos) == Blocks.OBSIDIAN && getCrystal(pos) != null) {
                mine(pos, pick);

                crystalTimer = crystalDelay.get() + 1;
                obsidianPosition = pos;
            }

            if (BlockUtils.getBlock(pos) == Blocks.OBSIDIAN && getCrystal(pos) == null) {
                if (crystalDelay.get() == 0 || crystalTimer > crystalDelay.get()) {
                    for (Entity entity : mc.world.getEntities()) {
                        if (entity instanceof EndCrystalEntity && BlockUtils.distance(pos, entity.getBlockPos()) <= 2.5) {
                            attack((EndCrystalEntity) entity);
                            crystalTimer = 0;
                            return;
                        }
                    }

                    interact(pos, crystal, Direction.UP);
                    crystalTimer = 0;
                } else crystalTimer++;
            }

            if (BlockUtils.getBlockState(pos).isAir() && getCrystal(pos) != null) {
                if (crystalDelay.get() == 0 || crystalTimer > crystalDelay.get()) {
                    attack(getCrystal(pos));
                    crystalTimer = 0;
                } else crystalTimer++;
            }
        }
    }

    // Utils

    private EndCrystalEntity getCrystal(BlockPos pos) {
        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof EndCrystalEntity && pos.up().equals(entity.getBlockPos())) return (EndCrystalEntity) entity;
        }

        return null;
    }

    private void mine(BlockPos pos, FindItemResult pick) {
        if (!packetMine.get()) BlockUtils.breakBlock(pos, renderSwing.get());
        else {
            if (mc.player.getInventory().selectedSlot != pick.getSlot()) InvUtils.swap2(pick.getSlot());

            mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, Direction.UP));

            if (renderSwing.get()) mc.player.swingHand(Hand.MAIN_HAND);
            else mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));

            mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, Direction.UP));
        }
    }

    private void attack(EndCrystalEntity entity) {
        mc.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(entity, mc.player.isSneaking()));

        if (renderSwing.get()) mc.player.swingHand(Hand.MAIN_HAND);
        else mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
    }

    private void interact(BlockPos pos, FindItemResult item, Direction direction) {
        if (item == null) return;
        if (item.getHand() == null || !silentSwitch.get()) InvUtils.swap2(item.getSlot());

        if (silentSwitch.get()) mc.interactionManager.interactBlock(mc.player, mc.world, item.getHand() == null ? Hand.MAIN_HAND : item.getHand(), new BlockHitResult(mc.player.getPos(), direction, pos, true));
        else mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, new BlockHitResult(mc.player.getPos(), direction, pos, true));
    }

    // Trap

    private void fillPlaceArray(PlayerEntity target) {
        placePositions.clear();
        BlockPos targetPos = target.getBlockPos();

        switch (trapMode.get()) {
            case Full -> {
                add(targetPos.add(1, 1, 0));
                add(targetPos.add(-1, 1, 0));
                add(targetPos.add(0, 1, 1));
                add(targetPos.add(0, 1, -1));

                add(targetPos.add(1, 0, 0));
                add(targetPos.add(-1, 0, 0));
                add(targetPos.add(0, 0, 1));
                add(targetPos.add(0, 0, -1));
            }
            case Face -> {
                add(targetPos.add(1, 1, 0));
                add(targetPos.add(-1, 1, 0));
                add(targetPos.add(0, 1, 1));
                add(targetPos.add(0, 1, -1));
            }
            case Feet -> {
                add(targetPos.add(1, 0, 0));
                add(targetPos.add(-1, 0, 0));
                add(targetPos.add(0, 0, 1));
                add(targetPos.add(0, 0, -1));
            }
        }
    }

    private void add(BlockPos blockPos) {
        if (!placePositions.contains(blockPos) && BlockUtils.canPlace(blockPos, true)) placePositions.add(blockPos);
    }

    @EventHandler
    public void onRender3D(Render3DEvent event) {
        if (render.get() && obsidianPosition != null) event.renderer.box(obsidianPosition, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
    }

    public enum Mode {
        Full,
        Face,
        Feet,
        None
    }
}
