package mathax.client.systems.modules.experimental;

import mathax.client.eventbus.EventHandler;
import mathax.client.events.mathax.MouseButtonEvent;
import mathax.client.events.world.TickEvent;
import mathax.client.settings.*;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import static mathax.client.utils.chinaman.FloorUtil.ofFloored;

public class BoomPlus extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Modes> mode = sgGeneral.add(new EnumSetting.Builder<Modes>()
        .name("mode")
        .description("the mode")
        .defaultValue(Modes.Fireball)
        .build());

    private final Setting<Integer> power = sgGeneral.add(new IntSetting.Builder()
        .name("power")
        .description("how big explosion")
        .defaultValue(10)
        .min(1)
        .sliderMax(127)
        .visible(() -> mode.get() == Modes.Fireball || mode.get() == Modes.Creeper)
        .build());

    private final Setting<Integer> fuse = sgGeneral.add(new IntSetting.Builder()
        .name("Creeper/TNT fuse")
        .description("In ticks")
        .defaultValue(20)
        .sliderRange(0, 120)
        .visible(() -> mode.get() == Modes.TNT || mode.get() == Modes.Creeper)
        .build());
    public final Setting<Boolean> target = sgGeneral.add(new BoolSetting.Builder()
        .name("OnTarget")
        .description("spawns on target")
        .defaultValue(false)
        .visible(() -> !(mode.get() == Modes.Lightning))
        .build()
    );

    private final Setting<Double> speed = sgGeneral.add(new DoubleSetting.Builder()
        .name("speed")
        .description("fastness of thing")
        .defaultValue(5)
        .min(1)
        .sliderMax(10)
        .visible(() -> !target.get() && (mode.get() == Modes.Wither || mode.get() == Modes.Spit || mode.get() == Modes.ShulkerBullet || mode.get() == Modes.WitherSkull || mode.get() == Modes.TNT || mode.get() == Modes.Arrow || mode.get() == Modes.Creeper || mode.get() == Modes.Kitty || mode.get() == Modes.Fireball))
        .build());
    public final Setting<Boolean> auto = sgGeneral.add(new BoolSetting.Builder()
        .name("FULLAUTO")
        .description("FULL AUTO BABY!")
        .defaultValue(false)
        .build()
    );
    public final Setting<Integer> atickdelay = sgGeneral.add(new IntSetting.Builder()
        .name("FULLAUTOTickDelay")
        .description("Tick Delay for FULLAUTO option.")
        .defaultValue(2)
        .min(0)
        .sliderMax(20)
        .visible(() -> auto.get())
        .build()
    );

    public BoomPlus() {
        super(Categories.Experimental, Items.FIRE_CHARGE, "boom+", "shoots something where you click");
    }
    private int aticks=0;

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player.getAbilities().creativeMode) {}
        else {
            error("You need to be in creative mode.");
            toggle();
        }
        if (auto.get() && mc.options.attackKey.isPressed() && mc.currentScreen == null && mc.player.getAbilities().creativeMode) {
            if (aticks<=atickdelay.get()){
                aticks++;
            } else if (aticks>atickdelay.get()) {
                NbtList motion = new NbtList();
                NbtCompound tag = new NbtCompound();
                NbtList Pos = new NbtList();
                HitResult hr = mc.cameraEntity.raycast(900, 0, true);
                Vec3d owo = hr.getPos();
                BlockPos pos = ofFloored(owo);
                ItemStack rst = mc.player.getMainHandStack();
                Vec3d sex = mc.player.getRotationVector().multiply(speed.get());
                BlockHitResult bhr = new BlockHitResult(mc.player.getEyePos(), Direction.DOWN, ofFloored(mc.player.getEyePos()), false);
                switch (mode.get()) {
                    case Fireball -> {
                        ItemStack Motion = new ItemStack(Items.SALMON_SPAWN_EGG);
                        if (target.get()) {
                            Pos.add(NbtDouble.of(pos.getX()));
                            Pos.add(NbtDouble.of(pos.getY()));
                            Pos.add(NbtDouble.of(pos.getZ()));
                            tag.put("Pos", Pos);
                        } else {
                            motion.add(NbtDouble.of(sex.x));
                            motion.add(NbtDouble.of(sex.y));
                            motion.add(NbtDouble.of(sex.z));
                            tag.put("Motion", motion);
                        }
                        tag.putInt("ExplosionPower", power.get());
                        tag.putString("id", "minecraft:fireball");
                        Motion.setSubNbt("EntityTag", tag);
                        mc.interactionManager.clickCreativeStack(Motion, 36 + mc.player.getInventory().selectedSlot);
                        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                        mc.interactionManager.clickCreativeStack(rst, 36 + mc.player.getInventory().selectedSlot);
                    }
                    case Lightning -> {
                        ItemStack Lightning = new ItemStack(Items.SALMON_SPAWN_EGG);
                        Pos.add(NbtDouble.of(pos.getX()));
                        Pos.add(NbtDouble.of(pos.getY()));
                        Pos.add(NbtDouble.of(pos.getZ()));
                        tag.put("Pos", Pos);
                        tag.putString("id", "minecraft:lightning_bolt");
                        Lightning.setSubNbt("EntityTag", tag);
                        mc.interactionManager.clickCreativeStack(Lightning, 36 + mc.player.getInventory().selectedSlot);
                        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                        mc.interactionManager.clickCreativeStack(rst, 36 + mc.player.getInventory().selectedSlot);
                    }
                    case Kitty -> {
                        ItemStack Kitty = new ItemStack(Items.CAT_SPAWN_EGG);
                        if (target.get()) {
                            Pos.add(NbtDouble.of(pos.getX()));
                            Pos.add(NbtDouble.of(pos.getY()));
                            Pos.add(NbtDouble.of(pos.getZ()));
                            tag.put("Pos", Pos);
                        } else {
                            motion.add(NbtDouble.of(sex.x));
                            motion.add(NbtDouble.of(sex.y));
                            motion.add(NbtDouble.of(sex.z));
                            tag.put("Motion", motion);
                        }
                        Kitty.setSubNbt("EntityTag", tag);
                        mc.interactionManager.clickCreativeStack(Kitty, 36 + mc.player.getInventory().selectedSlot);
                        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                        mc.interactionManager.clickCreativeStack(rst, 36 + mc.player.getInventory().selectedSlot);
                    }
                    case Wither -> {
                        ItemStack Kitty = new ItemStack(Items.CAT_SPAWN_EGG);
                        if (target.get()) {
                            Pos.add(NbtDouble.of(pos.getX()));
                            Pos.add(NbtDouble.of(pos.getY()));
                            Pos.add(NbtDouble.of(pos.getZ()));
                            tag.put("Pos", Pos);
                        } else {
                            motion.add(NbtDouble.of(sex.x));
                            motion.add(NbtDouble.of(sex.y));
                            motion.add(NbtDouble.of(sex.z));
                            tag.put("Motion", motion);
                        }
                        tag.putString("id", "minecraft:wither");
                        Kitty.setSubNbt("EntityTag", tag);
                        mc.interactionManager.clickCreativeStack(Kitty, 36 + mc.player.getInventory().selectedSlot);
                        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                        mc.interactionManager.clickCreativeStack(rst, 36 + mc.player.getInventory().selectedSlot);
                    }
                    case TNT -> {
                        ItemStack TNT = new ItemStack(Items.CAT_SPAWN_EGG);
                        if (target.get()) {
                            Pos.add(NbtDouble.of(pos.getX()));
                            Pos.add(NbtDouble.of(pos.getY()));
                            Pos.add(NbtDouble.of(pos.getZ()));
                            tag.put("Pos", Pos);
                        } else {
                            motion.add(NbtDouble.of(sex.x));
                            motion.add(NbtDouble.of(sex.y));
                            motion.add(NbtDouble.of(sex.z));
                            tag.put("Motion", motion);
                        }
                        tag.putString("id", "minecraft:tnt");
                        tag.putInt("Fuse", (fuse.get()));
                        TNT.setSubNbt("EntityTag", tag);
                        mc.interactionManager.clickCreativeStack(TNT, 36 + mc.player.getInventory().selectedSlot);
                        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                        mc.interactionManager.clickCreativeStack(rst, 36 + mc.player.getInventory().selectedSlot);
                    }
                    case WitherSkull -> {
                        ItemStack WitherSkull = new ItemStack(Items.CAT_SPAWN_EGG);
                        if (target.get()) {
                            Pos.add(NbtDouble.of(pos.getX()));
                            Pos.add(NbtDouble.of(pos.getY()));
                            Pos.add(NbtDouble.of(pos.getZ()));
                            tag.put("Pos", Pos);
                        } else {
                            motion.add(NbtDouble.of(sex.x));
                            motion.add(NbtDouble.of(sex.y));
                            motion.add(NbtDouble.of(sex.z));
                            tag.put("Motion", motion);
                        }
                        tag.putString("id", "minecraft:wither_skull");
                        WitherSkull.setSubNbt("EntityTag", tag);
                        mc.interactionManager.clickCreativeStack(WitherSkull, 36 + mc.player.getInventory().selectedSlot);
                        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                        mc.interactionManager.clickCreativeStack(rst, 36 + mc.player.getInventory().selectedSlot);
                    }
                    case Spit -> {
                        ItemStack Spit = new ItemStack(Items.CAT_SPAWN_EGG);
                        if (target.get()) {
                            Pos.add(NbtDouble.of(pos.getX()));
                            Pos.add(NbtDouble.of(pos.getY()));
                            Pos.add(NbtDouble.of(pos.getZ()));
                            tag.put("Pos", Pos);
                        } else {
                            motion.add(NbtDouble.of(sex.x));
                            motion.add(NbtDouble.of(sex.y));
                            motion.add(NbtDouble.of(sex.z));
                            tag.put("Motion", motion);
                        }
                        tag.putString("id", "minecraft:llama_spit");
                        Spit.setSubNbt("EntityTag", tag);
                        mc.interactionManager.clickCreativeStack(Spit, 36 + mc.player.getInventory().selectedSlot);
                        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                        mc.interactionManager.clickCreativeStack(rst, 36 + mc.player.getInventory().selectedSlot);
                    }
                    case ShulkerBullet -> {
                        ItemStack ShulkerBullet = new ItemStack(Items.CAT_SPAWN_EGG);
                        if (target.get()) {
                            Pos.add(NbtDouble.of(pos.getX()));
                            Pos.add(NbtDouble.of(pos.getY()));
                            Pos.add(NbtDouble.of(pos.getZ()));
                            tag.put("Pos", Pos);
                        } else {
                            motion.add(NbtDouble.of(sex.x));
                            motion.add(NbtDouble.of(sex.y));
                            motion.add(NbtDouble.of(sex.z));
                            tag.put("Motion", motion);
                        }
                        tag.putString("id", "minecraft:shulker_bullet");
                        ShulkerBullet.setSubNbt("EntityTag", tag);
                        mc.interactionManager.clickCreativeStack(ShulkerBullet, 36 + mc.player.getInventory().selectedSlot);
                        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                        mc.interactionManager.clickCreativeStack(rst, 36 + mc.player.getInventory().selectedSlot);
                    }
                    case Creeper -> {
                        ItemStack Creeper = new ItemStack(Items.CREEPER_SPAWN_EGG);
                        if (target.get()) {
                            Pos.add(NbtDouble.of(pos.getX()));
                            Pos.add(NbtDouble.of(pos.getY()));
                            Pos.add(NbtDouble.of(pos.getZ()));
                            tag.put("Pos", Pos);
                        } else {
                            motion.add(NbtDouble.of(sex.x));
                            motion.add(NbtDouble.of(sex.y));
                            motion.add(NbtDouble.of(sex.z));
                            tag.put("Motion", motion);
                        }
                        tag.putInt("ignited", (1));
                        tag.putInt("Invulnerable", (1));
                        tag.putInt("Fuse", (fuse.get()));
                        tag.putInt("NoGravity", (1));
                        tag.putInt("ExplosionRadius", power.get());
                        Creeper.setSubNbt("EntityTag", tag);
                        mc.interactionManager.clickCreativeStack(Creeper, 36 + mc.player.getInventory().selectedSlot);
                        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                        mc.interactionManager.clickCreativeStack(rst, 36 + mc.player.getInventory().selectedSlot);
                    }
                    case Arrow -> {
                        ItemStack Arrow = new ItemStack(Items.SALMON_SPAWN_EGG);
                        if (target.get()) {
                            Pos.add(NbtDouble.of(pos.getX()));
                            Pos.add(NbtDouble.of(pos.getY()));
                            Pos.add(NbtDouble.of(pos.getZ()));
                            tag.put("Pos", Pos);
                        } else {
                            motion.add(NbtDouble.of(sex.x));
                            motion.add(NbtDouble.of(sex.y));
                            motion.add(NbtDouble.of(sex.z));
                            tag.put("Motion", motion);
                        }
                        tag.putString("id", "minecraft:arrow");
                        Arrow.setSubNbt("EntityTag", tag);
                        mc.interactionManager.clickCreativeStack(Arrow, 36 + mc.player.getInventory().selectedSlot);
                        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                        mc.interactionManager.clickCreativeStack(rst, 36 + mc.player.getInventory().selectedSlot);
                    }
                }
                aticks=0;
            }
        }
    }

    @EventHandler
    private void onMouseButton(MouseButtonEvent event) {
        if (mc.options.attackKey.isPressed() && mc.currentScreen == null && mc.player.getAbilities().creativeMode) {
            NbtList motion = new NbtList();
            NbtCompound tag = new NbtCompound();
            NbtList Pos = new NbtList();
            HitResult hr = mc.cameraEntity.raycast(900, 0, true);
            Vec3d owo = hr.getPos();
            BlockPos pos = ofFloored(owo);
            ItemStack rst = mc.player.getMainHandStack();
            Vec3d sex = mc.player.getRotationVector().multiply(speed.get());
            BlockHitResult bhr = new BlockHitResult(mc.player.getEyePos(), Direction.DOWN, ofFloored(mc.player.getEyePos()), false);
            switch (mode.get()) {
                case Fireball -> {
                    ItemStack Motion = new ItemStack(Items.SALMON_SPAWN_EGG);
                    if (target.get()) {
                        Pos.add(NbtDouble.of(pos.getX()));
                        Pos.add(NbtDouble.of(pos.getY()));
                        Pos.add(NbtDouble.of(pos.getZ()));
                        tag.put("Pos", Pos);
                    } else {
                        motion.add(NbtDouble.of(sex.x));
                        motion.add(NbtDouble.of(sex.y));
                        motion.add(NbtDouble.of(sex.z));
                        tag.put("Motion", motion);
                    }
                    tag.putInt("ExplosionPower", power.get());
                    tag.putString("id", "minecraft:fireball");
                    Motion.setSubNbt("EntityTag", tag);
                    mc.interactionManager.clickCreativeStack(Motion, 36 + mc.player.getInventory().selectedSlot);
                    mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                    mc.interactionManager.clickCreativeStack(rst, 36 + mc.player.getInventory().selectedSlot);
                }
                case Lightning -> {
                    ItemStack Lightning = new ItemStack(Items.SALMON_SPAWN_EGG);
                    Pos.add(NbtDouble.of(pos.getX()));
                    Pos.add(NbtDouble.of(pos.getY()));
                    Pos.add(NbtDouble.of(pos.getZ()));
                    tag.put("Pos", Pos);
                    tag.putString("id", "minecraft:lightning_bolt");
                    Lightning.setSubNbt("EntityTag", tag);
                    mc.interactionManager.clickCreativeStack(Lightning, 36 + mc.player.getInventory().selectedSlot);
                    mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                    mc.interactionManager.clickCreativeStack(rst, 36 + mc.player.getInventory().selectedSlot);
                }
                case Kitty -> {
                    ItemStack Kitty = new ItemStack(Items.CAT_SPAWN_EGG);
                    if (target.get()) {
                        Pos.add(NbtDouble.of(pos.getX()));
                        Pos.add(NbtDouble.of(pos.getY()));
                        Pos.add(NbtDouble.of(pos.getZ()));
                        tag.put("Pos", Pos);
                    } else {
                        motion.add(NbtDouble.of(sex.x));
                        motion.add(NbtDouble.of(sex.y));
                        motion.add(NbtDouble.of(sex.z));
                        tag.put("Motion", motion);
                    }
                    Kitty.setSubNbt("EntityTag", tag);
                    mc.interactionManager.clickCreativeStack(Kitty, 36 + mc.player.getInventory().selectedSlot);
                    mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                    mc.interactionManager.clickCreativeStack(rst, 36 + mc.player.getInventory().selectedSlot);
                }
                case Wither -> {
                    ItemStack Kitty = new ItemStack(Items.CAT_SPAWN_EGG);
                    if (target.get()) {
                        Pos.add(NbtDouble.of(pos.getX()));
                        Pos.add(NbtDouble.of(pos.getY()));
                        Pos.add(NbtDouble.of(pos.getZ()));
                        tag.put("Pos", Pos);
                    } else {
                        motion.add(NbtDouble.of(sex.x));
                        motion.add(NbtDouble.of(sex.y));
                        motion.add(NbtDouble.of(sex.z));
                        tag.put("Motion", motion);
                    }
                    tag.putString("id", "minecraft:wither");
                    Kitty.setSubNbt("EntityTag", tag);
                    mc.interactionManager.clickCreativeStack(Kitty, 36 + mc.player.getInventory().selectedSlot);
                    mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                    mc.interactionManager.clickCreativeStack(rst, 36 + mc.player.getInventory().selectedSlot);
                }
                case TNT -> {
                    ItemStack TNT = new ItemStack(Items.CAT_SPAWN_EGG);
                    if (target.get()) {
                        Pos.add(NbtDouble.of(pos.getX()));
                        Pos.add(NbtDouble.of(pos.getY()));
                        Pos.add(NbtDouble.of(pos.getZ()));
                        tag.put("Pos", Pos);
                    } else {
                        motion.add(NbtDouble.of(sex.x));
                        motion.add(NbtDouble.of(sex.y));
                        motion.add(NbtDouble.of(sex.z));
                        tag.put("Motion", motion);
                    }
                    tag.putString("id", "minecraft:tnt");
                    tag.putInt("Fuse", (fuse.get()));
                    TNT.setSubNbt("EntityTag", tag);
                    mc.interactionManager.clickCreativeStack(TNT, 36 + mc.player.getInventory().selectedSlot);
                    mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                    mc.interactionManager.clickCreativeStack(rst, 36 + mc.player.getInventory().selectedSlot);
                }
                case WitherSkull -> {
                    ItemStack WitherSkull = new ItemStack(Items.CAT_SPAWN_EGG);
                    if (target.get()) {
                        Pos.add(NbtDouble.of(pos.getX()));
                        Pos.add(NbtDouble.of(pos.getY()));
                        Pos.add(NbtDouble.of(pos.getZ()));
                        tag.put("Pos", Pos);
                    } else {
                        motion.add(NbtDouble.of(sex.x));
                        motion.add(NbtDouble.of(sex.y));
                        motion.add(NbtDouble.of(sex.z));
                        tag.put("Motion", motion);
                    }
                    tag.putString("id", "minecraft:wither_skull");
                    WitherSkull.setSubNbt("EntityTag", tag);
                    mc.interactionManager.clickCreativeStack(WitherSkull, 36 + mc.player.getInventory().selectedSlot);
                    mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                    mc.interactionManager.clickCreativeStack(rst, 36 + mc.player.getInventory().selectedSlot);
                }
                case Spit -> {
                    ItemStack Spit = new ItemStack(Items.CAT_SPAWN_EGG);
                    if (target.get()) {
                        Pos.add(NbtDouble.of(pos.getX()));
                        Pos.add(NbtDouble.of(pos.getY()));
                        Pos.add(NbtDouble.of(pos.getZ()));
                        tag.put("Pos", Pos);
                    } else {
                        motion.add(NbtDouble.of(sex.x));
                        motion.add(NbtDouble.of(sex.y));
                        motion.add(NbtDouble.of(sex.z));
                        tag.put("Motion", motion);
                    }
                    tag.putString("id", "minecraft:llama_spit");
                    Spit.setSubNbt("EntityTag", tag);
                    mc.interactionManager.clickCreativeStack(Spit, 36 + mc.player.getInventory().selectedSlot);
                    mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                    mc.interactionManager.clickCreativeStack(rst, 36 + mc.player.getInventory().selectedSlot);
                }
                case ShulkerBullet -> {
                    ItemStack ShulkerBullet = new ItemStack(Items.CAT_SPAWN_EGG);
                    if (target.get()) {
                        Pos.add(NbtDouble.of(pos.getX()));
                        Pos.add(NbtDouble.of(pos.getY()));
                        Pos.add(NbtDouble.of(pos.getZ()));
                        tag.put("Pos", Pos);
                    } else {
                        motion.add(NbtDouble.of(sex.x));
                        motion.add(NbtDouble.of(sex.y));
                        motion.add(NbtDouble.of(sex.z));
                        tag.put("Motion", motion);
                    }
                    tag.putString("id", "minecraft:shulker_bullet");
                    ShulkerBullet.setSubNbt("EntityTag", tag);
                    mc.interactionManager.clickCreativeStack(ShulkerBullet, 36 + mc.player.getInventory().selectedSlot);
                    mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                    mc.interactionManager.clickCreativeStack(rst, 36 + mc.player.getInventory().selectedSlot);
                }
                case Creeper -> {
                    ItemStack Creeper = new ItemStack(Items.CREEPER_SPAWN_EGG);
                    if (target.get()) {
                        Pos.add(NbtDouble.of(pos.getX()));
                        Pos.add(NbtDouble.of(pos.getY()));
                        Pos.add(NbtDouble.of(pos.getZ()));
                        tag.put("Pos", Pos);
                    } else {
                        motion.add(NbtDouble.of(sex.x));
                        motion.add(NbtDouble.of(sex.y));
                        motion.add(NbtDouble.of(sex.z));
                        tag.put("Motion", motion);
                    }
                    tag.putInt("ignited", (1));
                    tag.putInt("Invulnerable", (1));
                    tag.putInt("Fuse", (fuse.get()));
                    tag.putInt("NoGravity", (1));
                    tag.putInt("ExplosionRadius", power.get());
                    Creeper.setSubNbt("EntityTag", tag);
                    mc.interactionManager.clickCreativeStack(Creeper, 36 + mc.player.getInventory().selectedSlot);
                    mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                    mc.interactionManager.clickCreativeStack(rst, 36 + mc.player.getInventory().selectedSlot);
                }
                case Arrow -> {
                    ItemStack Arrow = new ItemStack(Items.SALMON_SPAWN_EGG);
                    if (target.get()) {
                        Pos.add(NbtDouble.of(pos.getX()));
                        Pos.add(NbtDouble.of(pos.getY()));
                        Pos.add(NbtDouble.of(pos.getZ()));
                        tag.put("Pos", Pos);
                    } else {
                        motion.add(NbtDouble.of(sex.x));
                        motion.add(NbtDouble.of(sex.y));
                        motion.add(NbtDouble.of(sex.z));
                        tag.put("Motion", motion);
                    }
                    tag.putString("id", "minecraft:arrow");
                    Arrow.setSubNbt("EntityTag", tag);
                    mc.interactionManager.clickCreativeStack(Arrow, 36 + mc.player.getInventory().selectedSlot);
                    mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                    mc.interactionManager.clickCreativeStack(rst, 36 + mc.player.getInventory().selectedSlot);
                }
            }
        }
    }
    public enum Modes {
        Fireball, Lightning, Kitty, Creeper, Arrow, TNT, WitherSkull, Spit, ShulkerBullet, Wither
    }
}
