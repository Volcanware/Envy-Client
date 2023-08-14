package mathax.client.systems.modules.world;

import mathax.client.eventbus.EventHandler;
import mathax.client.events.game.GameLeftEvent;
import mathax.client.events.game.OpenScreenEvent;
import mathax.client.events.world.TickEvent;
import mathax.client.settings.*;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Random;
import static mathax.client.MatHax.mc;

public class AirstrikePlus extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Boolean> disconnectdisable = sgGeneral.add(new BoolSetting.Builder()
        .name("Disable on Disconnect")
        .description("Disables module on disconnecting")
        .defaultValue(false)
        .build());
    private final Setting<Modes> mode = sgGeneral.add(new EnumSetting.Builder<Modes>()
        .name("mode")
        .description("the mode")
        .defaultValue(Modes.Fireball)
        .build());
    private final Setting<Integer> radius = sgGeneral.add(new IntSetting.Builder()
        .name("radius")
        .description("radius they spawn from the player")
        .defaultValue(30)
        .sliderRange(1, 100)
        .min(1)
        .build());

    private final Setting<Integer> power = sgGeneral.add(new IntSetting.Builder()
        .name("fireball/CreeperPower")
        .description("power of explosions")
        .defaultValue(10)
        .sliderRange(1, 127)
        .min(1)
        .max(127)
        .visible(() -> mode.get() == Modes.Fireball || mode.get() == Modes.Creeper)
        .build());

    private final Setting<Integer> fuse = sgGeneral.add(new IntSetting.Builder()
        .name("Creeper/TNT fuse")
        .description("In ticks")
        .defaultValue(40)
        .sliderRange(0, 120)
        .min(0)
        .visible(() -> mode.get() == Modes.TNT || mode.get() == Modes.Creeper)
        .build());


    private final Setting<Integer> height = sgGeneral.add(new IntSetting.Builder()
        .name("HeightAboveHead")
        .description("How far from your Characters Y level to spawn at.")
        .defaultValue(20)
        .sliderRange(-63, 319)
        .build());

    private final Setting<Integer> speed = sgGeneral.add(new IntSetting.Builder()
        .name("speed")
        .description("speed of entities")
        .defaultValue(5)
        .sliderRange(1, 10)
        .min(1)
        .max(10)
        .visible(() -> !(mode.get() == Modes.Wither | mode.get() == Modes.Lightning))
        .build());

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("its in ticks")
        .defaultValue(2)
        .sliderRange(0, 20)
        .min(0)
        .build());


    public AirstrikePlus() {
        super(Categories.World, Items.TNT_MINECART, "Airstrike+", "Rains fireballs from the sky, and other things");
    }

    final Random r = new Random();
    Vec3d origin = null;
    int i = 0;
    int catdog=0;

    private Vec3d pickRandomPos() {
        double x = r.nextDouble(radius.get() * 2) - radius.get() + origin.x;
        double y = mc.player.getY()+height.get();
        double z = r.nextDouble(radius.get() * 2) - radius.get() + origin.z;
        return new Vec3d(x, y, z);
    }
    @EventHandler
    private void onScreenOpen(OpenScreenEvent event) {
        if (disconnectdisable.get() && event.screen instanceof DisconnectedScreen) {
            toggle();
        }
    }
    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (disconnectdisable.get())toggle();
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        origin = mc.player.getPos();
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        ItemStack bomb = new ItemStack(Items.SALMON_SPAWN_EGG);
        ItemStack bfr = mc.player.getMainHandStack();
        BlockHitResult bhr = new BlockHitResult(mc.player.getPos(), Direction.DOWN, new BlockPos(mc.player.getBlockPos()), false);
        Vec3d cpos = pickRandomPos();
        NbtCompound tag = new NbtCompound();
        NbtList speedlist = new NbtList();
        NbtList pos = new NbtList();
        i++;
        if (mc.player.getAbilities().creativeMode) {
            if (i >= delay.get()) {
                switch (mode.get()) {
                    case Fireball -> {
                        speedlist.add(NbtDouble.of(0));
                        speedlist.add(NbtDouble.of(-speed.get()));
                        speedlist.add(NbtDouble.of(0));
                        pos.add(NbtDouble.of(cpos.x));
                        pos.add(NbtDouble.of(mc.player.getY()+height.get()));
                        pos.add(NbtDouble.of(cpos.z));
                        tag.put("ExplosionPower", NbtDouble.of(power.get()));
                        tag.put("power", speedlist);
                        tag.put("Pos", pos);
                        tag.putString("id", "minecraft:fireball");
                        bomb.setSubNbt("EntityTag", tag);
                        mc.interactionManager.clickCreativeStack(bomb, 36 + mc.player.getInventory().selectedSlot);
                        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                        mc.interactionManager.clickCreativeStack(bfr, 36 + mc.player.getInventory().selectedSlot);
                        i = 0;
                    }
                    case Creeper -> {
                        speedlist.add(NbtDouble.of(0));
                        speedlist.add(NbtDouble.of(-speed.get()));
                        speedlist.add(NbtDouble.of(0));
                        pos.add(NbtDouble.of(cpos.x));
                        pos.add(NbtDouble.of(mc.player.getY()+height.get()));
                        pos.add(NbtDouble.of(cpos.z));
                        tag.putInt("ignited", (1));
                        tag.putInt("Invulnerable", (1));
                        tag.put("ExplosionRadius", NbtDouble.of(power.get()));
                        tag.put("Motion", speedlist);
                        tag.put("Pos", pos);
                        tag.putInt("Fuse", (fuse.get()));
                        tag.putString("id", "minecraft:creeper");
                        bomb.setSubNbt("EntityTag", tag);
                        mc.interactionManager.clickCreativeStack(bomb, 36 + mc.player.getInventory().selectedSlot);
                        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                        mc.interactionManager.clickCreativeStack(bfr, 36 + mc.player.getInventory().selectedSlot);
                        i = 0;
                    }
                    case Lightning -> {
                        pos.add(NbtDouble.of(cpos.x));
                        pos.add(NbtDouble.of(mc.player.getY()+height.get()));
                        pos.add(NbtDouble.of(cpos.z));
                        tag.put("Pos", pos);
                        tag.putString("id", "minecraft:lightning_bolt");
                        bomb.setSubNbt("EntityTag", tag);
                        mc.interactionManager.clickCreativeStack(bomb, 36 + mc.player.getInventory().selectedSlot);
                        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                        mc.interactionManager.clickCreativeStack(bfr, 36 + mc.player.getInventory().selectedSlot);
                        i = 0;
                    }
                    case Kitty -> {
                        speedlist.add(NbtDouble.of(0));
                        speedlist.add(NbtDouble.of(-speed.get()));
                        speedlist.add(NbtDouble.of(0));
                        pos.add(NbtDouble.of(cpos.x));
                        pos.add(NbtDouble.of(mc.player.getY()+height.get()));
                        pos.add(NbtDouble.of(cpos.z));
                        tag.putInt("Invulnerable", (1));
                        tag.put("Motion", speedlist);
                        tag.put("Pos", pos);
                        tag.putString("id", "minecraft:cat");
                        bomb.setSubNbt("EntityTag", tag);
                        mc.interactionManager.clickCreativeStack(bomb, 36 + mc.player.getInventory().selectedSlot);
                        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                        mc.interactionManager.clickCreativeStack(bfr, 36 + mc.player.getInventory().selectedSlot);
                        i = 0;
                    }
                    case Wither -> {
                        pos.add(NbtDouble.of(cpos.x));
                        pos.add(NbtDouble.of(mc.player.getY()+height.get()));
                        pos.add(NbtDouble.of(cpos.z));
                        tag.put("Pos", pos);
                        tag.putString("id", "minecraft:wither");
                        bomb.setSubNbt("EntityTag", tag);
                        mc.interactionManager.clickCreativeStack(bomb, 36 + mc.player.getInventory().selectedSlot);
                        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                        mc.interactionManager.clickCreativeStack(bfr, 36 + mc.player.getInventory().selectedSlot);
                        i = 0;
                    }
                    case TNT -> {
                        speedlist.add(NbtDouble.of(0));
                        speedlist.add(NbtDouble.of(-speed.get()));
                        speedlist.add(NbtDouble.of(0));
                        pos.add(NbtDouble.of(cpos.x));
                        pos.add(NbtDouble.of(mc.player.getY()+height.get()));
                        pos.add(NbtDouble.of(cpos.z));
                        tag.put("Motion", speedlist);
                        tag.put("Pos", pos);
                        tag.putString("id", "minecraft:tnt");
                        tag.putInt("Fuse", (fuse.get()));
                        bomb.setSubNbt("EntityTag", tag);
                        mc.interactionManager.clickCreativeStack(bomb, 36 + mc.player.getInventory().selectedSlot);
                        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                        mc.interactionManager.clickCreativeStack(bfr, 36 + mc.player.getInventory().selectedSlot);
                        i = 0;
                    }
                    case Spit -> {
                        speedlist.add(NbtDouble.of(0));
                        speedlist.add(NbtDouble.of(-speed.get()));
                        speedlist.add(NbtDouble.of(0));
                        pos.add(NbtDouble.of(cpos.x));
                        pos.add(NbtDouble.of(mc.player.getY()+height.get()));
                        pos.add(NbtDouble.of(cpos.z));
                        tag.put("Motion", speedlist);
                        tag.put("Pos", pos);
                        tag.putString("id", "minecraft:llama_spit");
                        bomb.setSubNbt("EntityTag", tag);
                        mc.interactionManager.clickCreativeStack(bomb, 36 + mc.player.getInventory().selectedSlot);
                        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                        mc.interactionManager.clickCreativeStack(bfr, 36 + mc.player.getInventory().selectedSlot);
                        i = 0;
                    }

                    case ShulkerBullet -> {
                        speedlist.add(NbtDouble.of(0));
                        speedlist.add(NbtDouble.of(-speed.get()));
                        speedlist.add(NbtDouble.of(0));
                        pos.add(NbtDouble.of(cpos.x));
                        pos.add(NbtDouble.of(mc.player.getY()+height.get()));
                        pos.add(NbtDouble.of(cpos.z));
                        tag.put("Motion", speedlist);
                        tag.put("Pos", pos);
                        tag.putString("id", "minecraft:shulker_bullet");
                        bomb.setSubNbt("EntityTag", tag);
                        mc.interactionManager.clickCreativeStack(bomb, 36 + mc.player.getInventory().selectedSlot);
                        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                        mc.interactionManager.clickCreativeStack(bfr, 36 + mc.player.getInventory().selectedSlot);
                        i = 0;
                    }
                    case Arrow -> {
                        speedlist.add(NbtDouble.of(0));
                        speedlist.add(NbtDouble.of(-speed.get()));
                        speedlist.add(NbtDouble.of(0));
                        pos.add(NbtDouble.of(cpos.x));
                        pos.add(NbtDouble.of(mc.player.getY()+height.get()));
                        pos.add(NbtDouble.of(cpos.z));
                        tag.put("Motion", speedlist);
                        tag.put("Pos", pos);
                        tag.putString("id", "minecraft:arrow");
                        bomb.setSubNbt("EntityTag", tag);
                        mc.interactionManager.clickCreativeStack(bomb, 36 + mc.player.getInventory().selectedSlot);
                        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                        mc.interactionManager.clickCreativeStack(bfr, 36 + mc.player.getInventory().selectedSlot);
                        i = 0;
                    }
                    case CatsAndDogs -> {
                        catdog++;
                        if (catdog<=1) {
                            speedlist.add(NbtDouble.of(0));
                            speedlist.add(NbtDouble.of(-speed.get()));
                            speedlist.add(NbtDouble.of(0));
                            pos.add(NbtDouble.of(cpos.x));
                            pos.add(NbtDouble.of(mc.player.getY()+height.get()));
                            pos.add(NbtDouble.of(cpos.z));
                            tag.putInt("Invulnerable", (1));
                            tag.put("Motion", speedlist);
                            tag.put("Pos", pos);
                            tag.putString("id", "minecraft:cat");
                            bomb.setSubNbt("EntityTag", tag);
                            mc.interactionManager.clickCreativeStack(bomb, 36 + mc.player.getInventory().selectedSlot);
                            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                            mc.interactionManager.clickCreativeStack(bfr, 36 + mc.player.getInventory().selectedSlot);
                            i = 0;
                        } else if (catdog>=2){
                            speedlist.add(NbtDouble.of(0));
                            speedlist.add(NbtDouble.of(-speed.get()));
                            speedlist.add(NbtDouble.of(0));
                            pos.add(NbtDouble.of(cpos.x));
                            pos.add(NbtDouble.of(mc.player.getY()+height.get()));
                            pos.add(NbtDouble.of(cpos.z));
                            tag.putInt("Invulnerable", (1));
                            tag.put("Motion", speedlist);
                            tag.put("Pos", pos);
                            tag.putString("id", "minecraft:wolf");
                            bomb.setSubNbt("EntityTag", tag);
                            mc.interactionManager.clickCreativeStack(bomb, 36 + mc.player.getInventory().selectedSlot);
                            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                            mc.interactionManager.clickCreativeStack(bfr, 36 + mc.player.getInventory().selectedSlot);
                            i = 0;
                            catdog=0;
                        }
                    }
                }
            }
        } else {
            error("You need to be in creative mode.");
            toggle();
        }
    }
    public enum Modes {
        Fireball, Creeper, Lightning, Kitty, Arrow, TNT, Spit, ShulkerBullet, Wither, CatsAndDogs
    }
}
