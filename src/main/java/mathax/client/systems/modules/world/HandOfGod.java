package mathax.client.systems.modules.world;

import mathax.client.eventbus.EventHandler;
import mathax.client.events.mathax.MouseButtonEvent;
import mathax.client.events.world.TickEvent;
import mathax.client.settings.*;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import mathax.client.systems.modules.Modules;
import mathax.client.systems.modules.movement.Flight;
import mathax.client.utils.Jebus.ChatUtils;
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

public class HandOfGod extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgClick = settings.createGroup("Click Options");
    private final SettingGroup sgPcentered = settings.createGroup("Player-Centered Options");
    public final Setting<Boolean> autosave = sgGeneral.add(new BoolSetting.Builder()
        .name("AutoSave and CTRL+S shortcut")
        .description("For saving your progress incase of server shutdown.")
        .defaultValue(true)
        .build()
    );
    private final Setting<Integer> autosavedelay = sgGeneral.add(new IntSetting.Builder()
        .name("AutoSave Delay (Seconds)")
        .description("How many seconds between saving the world.")
        .defaultValue(20)
        .min(1)
        .sliderMax(60)
        .build());

    private final Setting<String> block = sgClick.add(new StringSetting.Builder()
        .name("ClickBlock")
        .description("What is created when clicking")
        .defaultValue("air")
        .build());

    private final Setting<Integer> cwidth = sgClick.add(new IntSetting.Builder()
        .name("ClickWidth")
        .description("The width of the click fill")
        .defaultValue(10)
        .min(1)
        .sliderMax(30)
        .build());

    private final Setting<Integer> cheight = sgClick.add(new IntSetting.Builder()
        .name("ClickHeight")
        .description("The height of the click fill")
        .defaultValue(10)
        .min(1)
        .sliderMax(30)
        .build());
    private final Setting<Integer> cdepth = sgClick.add(new IntSetting.Builder()
        .name("ClickDepth")
        .description("The depth of the click fill")
        .defaultValue(10)
        .min(1)
        .sliderMax(30)
        .build());
    public final Setting<Boolean> lightning = sgClick.add(new BoolSetting.Builder()
        .name("Lightning")
        .description("Lightning on/off")
        .defaultValue(true)
        .build()
    );
    public final Setting<Boolean> auto = sgClick.add(new BoolSetting.Builder()
        .name("FULLAUTO")
        .description("FULL AUTO BABY!")
        .defaultValue(false)
        .build()
    );
    public final Setting<Integer> atickdelay = sgClick.add(new IntSetting.Builder()
        .name("FULLAUTOTickDelay")
        .description("Tick Delay for FULLAUTO option.")
        .defaultValue(2)
        .min(0)
        .sliderMax(20)
        .visible(() -> auto.get())
        .build()
    );
    public final Setting<Boolean> fluids = sgClick.add(new BoolSetting.Builder()
        .name("IncludeFluids")
        .description("Includes fluids when targeting, or not.")
        .defaultValue(true)
        .build()
    );
    public final Setting<Boolean> SwpAway = sgClick.add(new BoolSetting.Builder()
        .name("Sweep Away")
        .description("Right Click to sweep the whole world away")
        .defaultValue(false)
        .build()
    );
    private final Setting<String> sweepblock = sgClick.add(new StringSetting.Builder()
        .name("SweepBlock")
        .description("What is created when sweeping")
        .defaultValue("air")
        .visible(() -> SwpAway.get())
        .build());
    private final Setting<Integer> sweepradius = sgClick.add(new IntSetting.Builder()
        .name("SweepAwayRadius")
        .description("radius")
        .defaultValue(45)
        .sliderRange(1, 90)
        .visible(() -> SwpAway.get())
        .build());
    private final Setting<Integer> sweepstart = sgClick.add(new IntSetting.Builder()
        .name("SweepAwayStartingDistance")
        .description("Starting distance from character for the sweeper.")
        .defaultValue(3)
        .sliderRange(1, 30)
        .visible(() -> SwpAway.get())
        .build());
    public final Setting<Boolean> rndplyr = sgPcentered.add(new BoolSetting.Builder()
        .name("NukeAroundPlayer")
        .description("Runs /fill air around you every tick.")
        .defaultValue(true)
        .build()
    );
    public final Setting<Integer> pwidth = sgPcentered.add(new IntSetting.Builder()
        .name("PlayerWidth")
        .description("Width removed around player")
        .defaultValue(10)
        .min(1)
        .sliderMax(30)
        .visible(() -> rndplyr.get())
        .build()
    );
    public final Setting<Integer> pheight = sgPcentered.add(new IntSetting.Builder()
        .name("PlayerHeight")
        .description("Height removed around player")
        .defaultValue(10)
        .min(1)
        .sliderMax(30)
        .visible(() -> rndplyr.get())
        .build()
    );
    public final Setting<Integer> pdepth = sgPcentered.add(new IntSetting.Builder()
        .name("PlayerDepth")
        .description("Depth removed around player")
        .defaultValue(10)
        .min(1)
        .sliderMax(30)
        .visible(() -> rndplyr.get())
        .build()
    );
    public final Setting<Integer> tickdelay = sgPcentered.add(new IntSetting.Builder()
        .name("TickDelayAroundPlayer")
        .description("Tick Delay for running /fill around the player.")
        .defaultValue(0)
        .min(0)
        .sliderMax(100)
        .visible(() -> rndplyr.get())
        .build()
    );
    public final Setting<Boolean> mgcersr = sgPcentered.add(new BoolSetting.Builder()
        .name("MagicEraser")
        .description("FLY SLOW FOR IT TO WORK CORRECTLY. Runs /fill air in the shape of a wall infront of you every tick.")
        .defaultValue(false)
        .build()
    );
    private final Setting<Integer> mgcradius = sgPcentered.add(new IntSetting.Builder()
        .name("MagicEraserRadius")
        .description("radius")
        .defaultValue(30)
        .sliderRange(1, 90)
        .visible(() -> mgcersr.get())
        .build());
    private final Setting<Integer> mgcdist = sgPcentered.add(new IntSetting.Builder()
        .name("MagicEraserDistance")
        .description("Distance from player which the layer is /fill'ed")
        .defaultValue(5)
        .sliderRange(1, 30)
        .visible(() -> mgcersr.get())
        .build());

    public final Setting<Boolean> voider = sgPcentered.add(new BoolSetting.Builder()
        .name("VoiderAura")
        .description("Runs /fill on a single layer to your specified radius in a range from above your head to beneath your feet.")
        .defaultValue(false)
        .build()
    );
    private final Setting<Integer> radius = sgPcentered.add(new IntSetting.Builder()
        .name("radius")
        .description("radius")
        .defaultValue(45)
        .sliderRange(1, 90)
        .visible(() -> voider.get())
        .build());
    private final Setting<Integer> vrange = sgPcentered.add(new IntSetting.Builder()
        .name("VerticalRange")
        .description("How Far vertically from player to void.")
        .defaultValue(5)
        .sliderRange(1, 20)
        .visible(() -> voider.get())
        .build());
    public final Setting<Boolean> roofer = sgPcentered.add(new BoolSetting.Builder()
        .name("Roofer")
        .description("Runs /fill on the world at a set height")
        .defaultValue(false)
        .build()
    );
    private final Setting<String> roofblock = sgPcentered.add(new StringSetting.Builder()
        .name("RooferBlock")
        .description("What is created.")
        .defaultValue("obsidian")
        .visible(() -> roofer.get())
        .build());
    private final Setting<Integer> roofradius = sgPcentered.add(new IntSetting.Builder()
        .name("radius")
        .description("radius")
        .defaultValue(45)
        .sliderRange(1, 90)
        .visible(() -> roofer.get())
        .build());
    private final Setting<Integer> roofheight = sgPcentered.add(new IntSetting.Builder()
        .name("height")
        .description("height")
        .defaultValue(255)
        .sliderRange(64, 319)
        .visible(() -> roofer.get())
        .build());
    public final Setting<Integer> rooftickdelay = sgPcentered.add(new IntSetting.Builder()
        .name("TickDelay")
        .description("Tick Delay for running /fill.")
        .defaultValue(20)
        .min(0)
        .sliderMax(100)
        .visible(() -> roofer.get())
        .build()
    );
    public HandOfGod() {
        super(Categories.World, Items.GOLD_BLOCK, "HandOfGod", "Changes the world as you fly around, and replaces blocks with whatever you please when you click. (Must have OP status)");
    }

    private int ticks=0;
    private int swpr=0;
    private boolean sweep=false;
    private int asaveticks=0;
    private int aticks=0;
    private int errticks=0;
    private int roofticks=0;
    private int pX;
    private int pY;
    private int pZ;
    private int sX;
    private int sY;
    private int sZ;
    int i;

    @Override
    public boolean onActivate() {
        roofticks=0;
        if (roofer.get()){
            pX=mc.player.getBlockPos().getX();
            pZ=mc.player.getBlockPos().getZ();
            ChatUtils.sendPlayerMsg("/fill " + (pX - roofradius.get()) + " " + roofheight.get() +" "+ (pZ - roofradius.get()) +" "+ (pX + roofradius.get()) + " " + roofheight.get() +" "+ (pZ + roofradius.get()) + " "+roofblock);
        }
        aticks=0;
        ticks=0;
        if (voider.get()){
            i=mc.player.getBlockPos().getY();
        }
        return false;
    }

    @EventHandler
    private void onMouseButton(MouseButtonEvent event) {
        if (mc.options.attackKey.isPressed() && mc.currentScreen == null) {
            HitResult hr = mc.cameraEntity.raycast(900, 0, fluids.get());
            Vec3d god = hr.getPos();
            BlockPos pos = ofFloored(god);
            if (lightning.get()) {
                ItemStack rst = mc.player.getMainHandStack();
                BlockHitResult bhr = new BlockHitResult(mc.player.getEyePos(), Direction.DOWN, ofFloored(mc.player.getEyePos()), false);
                ItemStack Lightning = new ItemStack(Items.SALMON_SPAWN_EGG);
                NbtCompound tag = new NbtCompound();
                NbtList Pos = new NbtList();
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
            switch (mc.player.getHorizontalFacing()){
                case NORTH, SOUTH -> {
                    int x1 = Math.round(pos.getX()) + cwidth.get();
                    int y1 = Math.round(pos.getY()) + cheight.get();
                    int z1 = Math.round(pos.getZ()) + cdepth.get();
                    int x2 = Math.round(pos.getX()) - cwidth.get();
                    int y2 = Math.round(pos.getY()) - cheight.get();
                    int z2 = Math.round(pos.getZ()) - cdepth.get();
                    ChatUtils.sendPlayerMsg("/fill " + x1 + " " + y1 + " " + z1 + " " + x2 + " " + y2 + " " + z2 + " " + block);
                }
                case EAST, WEST -> {
                    int x1 = Math.round(pos.getX()) + cdepth.get();
                    int y1 = Math.round(pos.getY()) + cheight.get();
                    int z1 = Math.round(pos.getZ()) + cwidth.get();
                    int x2 = Math.round(pos.getX()) - cdepth.get();
                    int y2 = Math.round(pos.getY()) - cheight.get();
                    int z2 = Math.round(pos.getZ()) - cwidth.get();
                    ChatUtils.sendPlayerMsg("/fill " + x1 + " " + y1 + " " + z1 + " " + x2 + " " + y2 + " " + z2 + " " + block);
                }
            }
        }
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        pX=mc.player.getBlockPos().getX();
        pY=mc.player.getBlockPos().getY();
        pZ=mc.player.getBlockPos().getZ();
        if (autosave.get()){
            asaveticks++;
            if (asaveticks>=autosavedelay.get()*20){
                ChatUtils.sendPlayerMsg("/save-all");
                asaveticks=0;
            }
            if (mc.options.sneakKey.isPressed() && mc.options.backKey.isPressed()){
                ChatUtils.sendPlayerMsg("/save-all");
                asaveticks=0;
            }
        }
        if (auto.get() && mc.options.attackKey.isPressed() && mc.currentScreen == null) {
            if (aticks<=atickdelay.get()){
                aticks++;
            } else if (aticks>atickdelay.get()){
                HitResult hr = mc.cameraEntity.raycast(900, 0, fluids.get());
                Vec3d god = hr.getPos();
                BlockPos pos = ofFloored(god);
                if (lightning.get()) {
                    ItemStack rst = mc.player.getMainHandStack();
                    BlockHitResult bhr = new BlockHitResult(mc.player.getEyePos(), Direction.DOWN, ofFloored(mc.player.getEyePos()), false);
                    ItemStack Lightning = new ItemStack(Items.SALMON_SPAWN_EGG);
                    NbtCompound tag = new NbtCompound();
                    NbtList Pos = new NbtList();
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
                switch (mc.player.getHorizontalFacing()){
                    case NORTH, SOUTH -> {
                        int x1 = Math.round(pos.getX()) + cwidth.get();
                        int y1 = Math.round(pos.getY()) + cheight.get();
                        int z1 = Math.round(pos.getZ()) + cdepth.get();
                        int x2 = Math.round(pos.getX()) - cwidth.get();
                        int y2 = Math.round(pos.getY()) - cheight.get();
                        int z2 = Math.round(pos.getZ()) - cdepth.get();
                        ChatUtils.sendPlayerMsg("/fill " + x1 + " " + y1 + " " + z1 + " " + x2 + " " + y2 + " " + z2 + " " + block);
                    }
                    case EAST, WEST -> {
                        int x1 = Math.round(pos.getX()) + cdepth.get();
                        int y1 = Math.round(pos.getY()) + cheight.get();
                        int z1 = Math.round(pos.getZ()) + cwidth.get();
                        int x2 = Math.round(pos.getX()) - cdepth.get();
                        int y2 = Math.round(pos.getY()) - cheight.get();
                        int z2 = Math.round(pos.getZ()) - cwidth.get();
                        ChatUtils.sendPlayerMsg("/fill " + x1 + " " + y1 + " " + z1 + " " + x2 + " " + y2 + " " + z2 + " " + block);
                    }
                }
                aticks=0;
            }
        }
        if (SwpAway.get()){
            if (mc.options.useKey.isPressed()){
                sweep=true;
            }else if (mc.options.useKey.isPressed()==false) sweep=false;
            if (sweep==false){
                sX=mc.player.getBlockX();
                sY=mc.player.getBlockY();
                sZ=mc.player.getBlockZ();
            }
            if (sweep==true){
                switch (mc.player.getHorizontalFacing()){
                    case NORTH -> {
                        ChatUtils.sendPlayerMsg("/fill " + (sX - sweepradius.get()) + " " + (sY-sweepradius.get()) + " "+ (sZ - (sweepstart.get()+swpr)) +" "+ (sX + sweepradius.get()) + " " + (sY +sweepradius.get()) +" "+ (sZ - (sweepstart.get()+swpr)) +" "+sweepblock);
                    }
                    case WEST -> {
                        ChatUtils.sendPlayerMsg("/fill " + (sX - (sweepstart.get()+swpr)) + " " + (sY-sweepradius.get()) + " "+ (sZ - sweepradius.get()) +" "+ (sX - (sweepstart.get()+swpr)) + " " + (sY+sweepradius.get()) +" "+ (sZ + sweepradius.get()) +" "+sweepblock);
                    }
                    case SOUTH -> {
                        ChatUtils.sendPlayerMsg("/fill " + (sX - sweepradius.get()) + " " + (sY-sweepradius.get()) + " "+ (sZ + (sweepstart.get()+swpr)) +" "+ (sX + sweepradius.get()) + " " + (sY+sweepradius.get()) +" "+ (sZ + (sweepstart.get()+swpr)) +" "+sweepblock);
                    }
                    case EAST -> {
                        ChatUtils.sendPlayerMsg("/fill " + (sX  + (sweepstart.get()+swpr)) + " " + (sY-sweepradius.get()) + " "+ (sZ - sweepradius.get()) +" "+ (sX + (sweepstart.get()+swpr)) + " " + (sY+sweepradius.get()) +" "+ (sZ + sweepradius.get()) +" "+sweepblock);
                    }
                }
                swpr++;
            } else swpr=0;
        }
        if (rndplyr.get()){
            if (ticks<=tickdelay.get()){
                ticks++;
            } else if (ticks>tickdelay.get()){
                switch (mc.player.getHorizontalFacing()){
                    case NORTH, SOUTH -> {
                        ChatUtils.sendPlayerMsg("/execute at @p run fill ~"+pwidth.get()+" ~"+pheight.get()+" ~"+pdepth.get()+" ~-"+pwidth.get()+" ~-"+pheight.get()+" ~-"+pdepth.get()+" air");

                    }
                    case EAST, WEST -> {
                        ChatUtils.sendPlayerMsg("/execute at @p run fill ~"+pdepth.get()+" ~"+pheight.get()+" ~"+pwidth.get()+" ~-"+pdepth.get()+" ~-"+pheight.get()+" ~-"+pwidth.get()+" air");

                    }
                }
                ticks=0;
            }
        }
        if (voider.get()){
            if (i>= mc.player.getBlockPos().getY()-vrange.get()){
                ChatUtils.sendPlayerMsg("/fill " + (pX - radius.get()) + " " + i +" "+ (pZ - radius.get()) +" "+ (pX + radius.get()) + " " + i +" "+ (pZ + radius.get()) +" air");
                i--;
            }else if (i<= mc.player.getBlockPos().getY()-vrange.get()){
                i=pY+vrange.get();
            }


        }
        if (mgcersr.get()){
            if (Modules.get().isActive(Flight.class)){
                if (errticks<3){
                    errticks++;}
                if (errticks==2){
                    error("Fly Slow. Set Flight speed to 0.1 or less. :D");
                }
            }
            if (mc.options.jumpKey.isPressed()){
                ChatUtils.sendPlayerMsg("/fill " + (pX - mgcradius.get()) + " " + (pY+mgcdist.get()) + " "+ (pZ - mgcradius.get()) +" "+ (pX + mgcradius.get()) + " " + (pY+mgcdist.get()) +" "+ (pZ + mgcradius.get()) +" air");
            }
            if (mc.options.sneakKey.isPressed()){
                ChatUtils.sendPlayerMsg("/fill " + (pX - mgcradius.get()) + " " + (pY-mgcdist.get()) + " "+ (pZ - mgcradius.get()) +" "+ (pX + mgcradius.get()) + " " + (pY-mgcdist.get()) +" "+ (pZ + mgcradius.get()) +" air");
            }
            if (mc.options.forwardKey.isPressed()){
                switch (mc.player.getHorizontalFacing()){
                    case NORTH -> {
                        ChatUtils.sendPlayerMsg("/fill " + (pX - mgcradius.get()) + " " + (pY-mgcradius.get()) + " "+ (pZ - mgcdist.get()) +" "+ (pX + mgcradius.get()) + " " + (pY+mgcradius.get()) +" "+ (pZ - mgcdist.get()) +" air");
                    }
                    case WEST -> {
                        ChatUtils.sendPlayerMsg("/fill " + (pX - mgcdist.get()) + " " + (pY-mgcradius.get()) + " "+ (pZ - mgcradius.get()) +" "+ (pX - mgcdist.get()) + " " + (pY+mgcradius.get()) +" "+ (pZ + mgcradius.get()) +" air");
                    }
                    case SOUTH -> {
                        ChatUtils.sendPlayerMsg("/fill " + (pX - mgcradius.get()) + " " + (pY-mgcradius.get()) + " "+ (pZ + mgcdist.get()) +" "+ (pX + mgcradius.get()) + " " + (pY+mgcradius.get()) +" "+ (pZ + mgcdist.get()) +" air");
                    }
                    case EAST -> {
                        ChatUtils.sendPlayerMsg("/fill " + (pX + mgcdist.get()) + " " + (pY-mgcradius.get()) + " "+ (pZ - mgcradius.get()) +" "+ (pX + mgcdist.get()) + " " + (pY+mgcradius.get()) +" "+ (pZ + mgcradius.get()) +" air");
                    }
                }
            }
            if (mc.options.backKey.isPressed()){
                switch (mc.player.getHorizontalFacing()){
                    case NORTH -> {
                        ChatUtils.sendPlayerMsg("/fill " + (pX - mgcradius.get()) + " " + (pY-mgcradius.get()) + " "+ (pZ + mgcdist.get()) +" "+ (pX + mgcradius.get()) + " " + (pY+mgcradius.get()) +" "+ (pZ + mgcdist.get()) +" air");
                    }
                    case WEST -> {
                        ChatUtils.sendPlayerMsg("/fill " + (pX + mgcdist.get()) + " " + (pY-mgcradius.get()) + " "+ (pZ - mgcradius.get()) +" "+ (pX + mgcdist.get()) + " " + (pY+mgcradius.get()) +" "+ (pZ + mgcradius.get()) +" air");
                    }
                    case SOUTH -> {
                        ChatUtils.sendPlayerMsg("/fill " + (pX - mgcradius.get()) + " " + (pY-mgcradius.get()) + " "+ (pZ - mgcdist.get()) +" "+ (pX + mgcradius.get()) + " " + (pY+mgcradius.get()) +" "+ (pZ - mgcdist.get()) +" air");
                    }
                    case EAST -> {
                        ChatUtils.sendPlayerMsg("/fill " + (pX - mgcdist.get()) + " " + (pY-mgcradius.get()) + " "+ (pZ - mgcradius.get()) +" "+ (pX - mgcdist.get()) + " " + (pY+mgcradius.get()) +" "+ (pZ + mgcradius.get()) +" air");
                    }
                }
            }
            if (mc.options.rightKey.isPressed()){
                switch (mc.player.getHorizontalFacing()){
                    case NORTH -> {
                        ChatUtils.sendPlayerMsg("/fill " + (pX + mgcdist.get()) + " " + (pY-mgcradius.get()) + " "+ (pZ - mgcradius.get()) +" "+ (pX + mgcdist.get()) + " " + (pY+mgcradius.get()) +" "+ (pZ + mgcradius.get()) +" air");
                    }
                    case WEST -> {
                        ChatUtils.sendPlayerMsg("/fill " + (pX - mgcradius.get()) + " " + (pY-mgcradius.get()) + " "+ (pZ - mgcdist.get()) +" "+ (pX + mgcradius.get()) + " " + (pY+mgcradius.get()) +" "+ (pZ - mgcdist.get()) +" air");
                    }
                    case SOUTH -> {
                        ChatUtils.sendPlayerMsg("/fill " + (pX - mgcdist.get()) + " " + (pY-mgcradius.get()) + " "+ (pZ - mgcradius.get()) +" "+ (pX - mgcdist.get()) + " " + (pY+mgcradius.get()) +" "+ (pZ + mgcradius.get()) +" air");
                    }
                    case EAST -> {
                        ChatUtils.sendPlayerMsg("/fill " + (pX - mgcradius.get()) + " " + (pY-mgcradius.get()) + " "+ (pZ + mgcdist.get()) +" "+ (pX + mgcradius.get()) + " " + (pY+mgcradius.get()) +" "+ (pZ + mgcdist.get()) +" air");
                    }
                }
            }
            if (mc.options.leftKey.isPressed()){
                switch (mc.player.getHorizontalFacing()){
                    case NORTH -> {
                        ChatUtils.sendPlayerMsg("/fill " + (pX - mgcdist.get()) + " " + (pY-mgcradius.get()) + " "+ (pZ - mgcradius.get()) +" "+ (pX - mgcdist.get()) + " " + (pY+mgcradius.get()) +" "+ (pZ + mgcradius.get()) +" air");
                    }
                    case WEST -> {
                        ChatUtils.sendPlayerMsg("/fill " + (pX - mgcradius.get()) + " " + (pY-mgcradius.get()) + " "+ (pZ + mgcdist.get()) +" "+ (pX + mgcradius.get()) + " " + (pY+mgcradius.get()) +" "+ (pZ + mgcdist.get()) +" air");
                    }
                    case SOUTH -> {
                        ChatUtils.sendPlayerMsg("/fill " + (pX + mgcdist.get()) + " " + (pY-mgcradius.get()) + " "+ (pZ - mgcradius.get()) +" "+ (pX + mgcdist.get()) + " " + (pY+mgcradius.get()) +" "+ (pZ + mgcradius.get()) +" air");
                    }
                    case EAST -> {
                        ChatUtils.sendPlayerMsg("/fill " + (pX - mgcradius.get()) + " " + (pY-mgcradius.get()) + " "+ (pZ - mgcdist.get()) +" "+ (pX + mgcradius.get()) + " " + (pY+mgcradius.get()) +" "+ (pZ - mgcdist.get()) +" air");
                    }
                }
            }

        }
        if (roofer.get()){
            if (roofticks<=rooftickdelay.get()){
                roofticks++;
            } else if (roofticks>rooftickdelay.get()) {
                ChatUtils.sendPlayerMsg("/fill " + (pX - roofradius.get()) + " " + roofheight.get() +" "+ (pZ - roofradius.get()) +" "+ (pX + roofradius.get()) + " " + roofheight.get() +" "+ (pZ + roofradius.get()) + " "+roofblock);
                roofticks=0;
            }
        }
    }
}



