package mathax.client.systems.modules.experimental;

import mathax.client.eventbus.EventHandler;
import mathax.client.events.game.GameLeftEvent;
import mathax.client.events.game.OpenScreenEvent;
import mathax.client.events.mathax.MouseButtonEvent;
import mathax.client.events.world.TickEvent;
import mathax.client.settings.BoolSetting;
import mathax.client.settings.IntSetting;
import mathax.client.settings.Setting;
import mathax.client.settings.SettingGroup;
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
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import static mathax.client.MatHax.mc;
import static mathax.client.utils.chinaman.FloorUtil.ofFloored;

public class ExplosionAura extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    public final Setting<Integer> tickdelay = sgGeneral.add(new IntSetting.Builder()
        .name("ExplosionTickDelayAroundPlayer")
        .description("Tick Delay for exploding around the player.")
        .defaultValue(5)
        .min(0)
        .sliderMax(100)
        .build()
    );
    private final Setting<Integer> power = sgGeneral.add(new IntSetting.Builder()
        .name("ExplosionPower")
        .description("Explosion Power at the character position.")
        .defaultValue(10)
        .min(1)
        .sliderMax(127)
        .build());
    public final Setting<Boolean> click = sgGeneral.add(new BoolSetting.Builder()
        .name("ClickExplosion")
        .description("spawns on target")
        .defaultValue(false)
        .build()
    );
    private final Setting<Integer> cpower = sgGeneral.add(new IntSetting.Builder()
        .name("ClickPower")
        .description("how big explosion")
        .defaultValue(10)
        .min(1)
        .sliderMax(127)
        .visible(() -> click.get())
        .build());
    public final Setting<Boolean> auto = sgGeneral.add(new BoolSetting.Builder()
        .name("FULLAUTO")
        .description("FULL AUTO BABY!")
        .defaultValue(false)
        .visible(() -> click.get())
        .build()
    );
    public final Setting<Integer> atickdelay = sgGeneral.add(new IntSetting.Builder()
        .name("FULLAUTOTickDelay")
        .description("Tick Delay for FULLAUTO option.")
        .defaultValue(2)
        .min(0)
        .sliderMax(20)
        .visible(() -> auto.get() && click.get())
        .build()
    );

    public ExplosionAura() {
        super(Categories.Experimental, Items.OBSIDIAN, "ExplosionAura", "You explode as you move. Must be in creative.");
    }

    private int ticks=0;
    private int aticks=0;

    @EventHandler
    private void onScreenOpen(OpenScreenEvent event) {
        if (event.screen instanceof DisconnectedScreen) {
            toggle();
        }
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        toggle();
    }

    @EventHandler
    private void onMouseButton(MouseButtonEvent event) {
        if (mc.options.attackKey.isPressed() && mc.currentScreen == null && mc.player.getAbilities().creativeMode) {
            if (click.get()) {
                HitResult hr = mc.cameraEntity.raycast(600, 0, true);
                Vec3d owo = hr.getPos();
                BlockPos pos = ofFloored(owo);
                ItemStack rst = mc.player.getMainHandStack();
                BlockHitResult bhr = new BlockHitResult(mc.player.getEyePos(), Direction.DOWN, ofFloored(mc.player.getEyePos()), false);
                ItemStack Creeper = new ItemStack(Items.CREEPER_SPAWN_EGG);
                NbtCompound tag = new NbtCompound();
                NbtList Pos = new NbtList();
                Pos.add(NbtDouble.of(pos.getX()));
                Pos.add(NbtDouble.of(pos.getY()));
                Pos.add(NbtDouble.of(pos.getZ()));
                tag.put("Pos", Pos);
                tag.putInt("ignited", (1));
                tag.putInt("Invulnerable", (1));
                tag.putInt("Fuse", (0));
                tag.putInt("NoGravity", (1));
                tag.putInt("ExplosionRadius", cpower.get());
                Creeper.setSubNbt("EntityTag", tag);
                mc.interactionManager.clickCreativeStack(Creeper, 36 + mc.player.getInventory().selectedSlot);
                mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                mc.interactionManager.clickCreativeStack(rst, 36 + mc.player.getInventory().selectedSlot);
            }
        }
    }
    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player.getAbilities().creativeMode) {
            if (auto.get() && mc.options.attackKey.isPressed() && mc.currentScreen == null && mc.player.getAbilities().creativeMode) {
                if (click.get()) {
                    if (aticks<=atickdelay.get()){
                        aticks++;
                    } else if (aticks>atickdelay.get()) {
                        HitResult hr = mc.cameraEntity.raycast(600, 0, true);
                        Vec3d owo = hr.getPos();
                        BlockPos pos = ofFloored(owo);
                        ItemStack rst = mc.player.getMainHandStack();
                        BlockHitResult bhr = new BlockHitResult(mc.player.getEyePos(), Direction.DOWN, ofFloored(mc.player.getEyePos()), false);
                        ItemStack Creeper = new ItemStack(Items.CREEPER_SPAWN_EGG);
                        NbtCompound tag = new NbtCompound();
                        NbtList Pos = new NbtList();
                        Pos.add(NbtDouble.of(pos.getX()));
                        Pos.add(NbtDouble.of(pos.getY()));
                        Pos.add(NbtDouble.of(pos.getZ()));
                        tag.put("Pos", Pos);
                        tag.putInt("ignited", (1));
                        tag.putInt("Invulnerable", (1));
                        tag.putInt("Fuse", (0));
                        tag.putInt("NoGravity", (1));
                        tag.putInt("ExplosionRadius", cpower.get());
                        Creeper.setSubNbt("EntityTag", tag);
                        mc.interactionManager.clickCreativeStack(Creeper, 36 + mc.player.getInventory().selectedSlot);
                        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                        mc.interactionManager.clickCreativeStack(rst, 36 + mc.player.getInventory().selectedSlot);
                        aticks=0;
                    }
                }
            }
            if (mc.options.forwardKey.isPressed()||mc.options.jumpKey.isPressed()||mc.options.sneakKey.isPressed()||mc.options.leftKey.isPressed()||mc.options.rightKey.isPressed()||mc.options.backKey.isPressed()) {
                if (ticks<=tickdelay.get()){
                    ticks++;
                } else if (ticks>tickdelay.get()){
                    ItemStack rst = mc.player.getMainHandStack();
                    BlockHitResult bhr = new BlockHitResult(mc.player.getPos(), Direction.DOWN, ofFloored(mc.player.getPos()), false);
                    ItemStack Creeper = new ItemStack(Items.CREEPER_SPAWN_EGG);
                    NbtCompound tag = new NbtCompound();
                    tag.putInt("ignited", (1));
                    tag.putInt("Fuse", (0));
                    tag.putInt("Invulnerable", (1));
                    tag.putInt("NoGravity", (1));
                    tag.putInt("ExplosionRadius", power.get());
                    Creeper.setSubNbt("EntityTag", tag);
                    mc.interactionManager.clickCreativeStack(Creeper, 36 + mc.player.getInventory().selectedSlot);
                    mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                    mc.interactionManager.clickCreativeStack(rst, 36 + mc.player.getInventory().selectedSlot);
                    ticks=0;
                }
            }
        } else {
            error("You need to be in creative mode.");
            toggle();
        }
    }
}
