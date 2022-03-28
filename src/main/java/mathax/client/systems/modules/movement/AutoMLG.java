package mathax.client.systems.modules.movement;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

import baritone.api.utils.Rotation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.block.SeagrassBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import mathax.client.eventbus.EventHandler;
import mathax.client.eventbus.EventPriority;
import mathax.client.events.world.TickEvent;
import mathax.client.settings.BoolSetting;
import mathax.client.settings.DoubleSetting;
import mathax.client.settings.Setting;
import mathax.client.settings.SettingGroup;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import mathax.client.systems.modules.Modules;
import mathax.client.systems.modules.movement.AntiAFK.SpinMode;
import mathax.client.systems.modules.movement.AutoWalk.Mode;
import mathax.client.utils.world.BlockUtils;
import mathax.client.utils.player.PlayerUtils;
//other mods
import mathax.client.systems.modules.movement.Flight;

public class AutoMLG extends Module {
	
	private final SettingGroup sgGeneral = settings.getDefaultGroup();
	private final SettingGroup sgIgnore = settings.createGroup("Ignoring");
	
	//MC client
	private MinecraftClient IMC;
	
	private final Setting<Double> minfall = sgGeneral.add(new DoubleSetting.Builder()
	    	.name("min-fall")
	    	.description("At which minimum height the bot should perform the mlgbucket?")
	    	.defaultValue(4.0)
	    	.min(1)
	    	.sliderRange(1, 20.0)
	    	.build()
		);
	
	/*
	private final Setting<Boolean> allowInv = sgGeneral.add(new BoolSetting.Builder()
	        .name("allow-inventory")
	        .description("Takes buckets from your inventory.")
	        .defaultValue(false)
	        .build()
	    );
	    */
	
	private final Setting<Boolean> snowprio = sgGeneral.add(new BoolSetting.Builder()
	        .name("powder-snow-priority")
	        .description("Will prioritize powdered snow if both mlg items are avaiable.")
	        .defaultValue(false)
	        .build()
	    );
	
	private final Setting<Boolean> possnap = sgGeneral.add(new BoolSetting.Builder()
	        .name("snap")
	        .description("Will clamp player's X and Z positions.")
	        .defaultValue(false)
	        .build()
	    );
	
	private final Setting<Boolean> snowInWarm = sgGeneral.add(new BoolSetting.Builder()
	        .name("use-powder-snow-in-nether")
	        .description("Uses powdersnow bucket instead of water bucket in nether biome.") //even if priority is set to water bucket
	        .defaultValue(true)
	        .build()
	    );
	
	private final Setting<Boolean> inCreative = sgIgnore.add(new BoolSetting.Builder()
	        .name("in-creative")
	        .description("Tries to mlg bucket in creative mode.")
	        .defaultValue(true)
	        .build()
	    );
	
	 private final Setting<Boolean> ignFly = sgIgnore.add(new BoolSetting.Builder()
		        .name("ignore-flight")
		        .description("Tries to mlg bucket even if fly module is enabled.")
		        .defaultValue(false)
		        .build()
		    );
	 
	 private final Setting<Boolean> ignElytra = sgIgnore.add(new BoolSetting.Builder()
		        .name("ignore-flight")
		        .description("Tries to mlg bucket even if elytrafly module is enabled.")
		        .defaultValue(false)
		        .build()
		    );
	 
	 private final Setting<Boolean> ignSlowFall = sgIgnore.add(new BoolSetting.Builder()
		        .name("ignore-slowfall")
		        .description("Tries to mlg bucket even if player has slow falling effect.")
		        .defaultValue(false)
		        .build()
		    );
	
	 private boolean placedWater = false;

	    private boolean isWaterBucket = false; //is the item used for mlg a weter bucket?
	    private boolean isOffHand = false; //is the mlg item held in offhand?
	    private Integer pickupRetry = 0;
	    private BlockPos waterPlaceBlock = null;
	    private Vec2f lastRot = null;
	    private Vec3d lastPos = null;
	    
	    private static final List<ItemStack> BUCKETS = Arrays.asList(
	        new ItemStack(Items.WATER_BUCKET), //0
	        new ItemStack(Items.POWDER_SNOW_BUCKET) //1
	    );
	
	public AutoMLG() {
        super(Categories.Movement, Items.WATER_BUCKET, "auto-mlg", "Places water.");
    }
	
	@EventHandler
    private void onTick(TickEvent.Pre event) {
		if (mc.player.getAbilities().creativeMode && inCreative.get() == false) return; //checks if the player is in creative mode
		//ignoring part
		if(Modules.get().get("flight").isActive() && ignFly.get() == false) return;
		if(Modules.get().get("elytra-fly").isActive() && ignElytra.get() == false) return;
		if(mc.player.hasStatusEffect(StatusEffects.SLOW_FALLING) && ignSlowFall.get() == false) return;
		
	    if (!placedWater) {
	        float minfallDist = minfall.get().floatValue();
	        if (mc.player.fallDistance > minfall.get() - 2.0f && !mc.player.hasStatusEffect(StatusEffects.SLOW_FALLING) && !(mc.player.isFallFlying())) {
	            Vec3d playerPos = mc.player.getPos();
	            if (lastPos == null) lastPos = playerPos;
	            if (playerPos.y - lastPos.y < 0.0D) {
	                
	                BlockHitResult result = mc.world.raycast(new RaycastContext(mc.player.getPos(), playerPos.subtract(0, 5, 0), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, mc.player));
	                BlockPos bp = result.getBlockPos();
	                if (result != null && result.getType() == BlockHitResult.Type.BLOCK
	                        && Math.max(0.0, (float)playerPos.y - (float)(bp.getY())) - 1.3f + mc.player.fallDistance > minfall.get().floatValue()
	                        && causeFallDamage(BlockUtils.getState(result.getBlockPos()))
	                        && causeFallDamage(BlockUtils.getState(result.getBlockPos().up()))
	                ) {
	                    for (ItemStack bucket : (snowprio.get() ? Lists.reverse(BUCKETS) : BUCKETS)) {
	                        if (mc.world.getDimension().isUltrawarm() && bucket.getItem().equals(Items.WATER_BUCKET))
	                            continue;
	                        int location = switchToStack(bucket);
	                        if (location == 0) continue;
	                        isOffHand = location < 0;
	                        isWaterBucket = bucket.getItem().equals(Items.WATER_BUCKET);
	                        
	                        if(mc.world.getDimension().isUltrawarm() && snowInWarm.get() == true)
	                        {
	                        	isWaterBucket = false;
	                        }
	                        
	                        if (possnap.get()) {
	                            double x = MathHelper.floor(mc.player.getX()) + 0.5;
	                            double z = MathHelper.floor(mc.player.getZ()) + 0.5;
	                            if ((Math.abs(mc.player.getX() - x) > 1e-5) || (Math.abs(mc.player.getZ() - z) > 1e-5)) {
	                                mc.player.setPosition(x, mc.player.getY(), z);
	                                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.isOnGround()));
	                            }
	                            mc.player.setVelocity(0, mc.player.getVelocity().y, 0);
	                        }
	                        if (lastRot == null) lastRot = mc.player.getRotationClient();
	                        if (rightClickBlock(bp, false)) {
	                            placedWater = true;
	                            waterPlaceBlock = bp;
	                            pickupRetry = 0;
	                        }
	                        break;
	                    }
	                }
	            }
	            lastPos = playerPos;
	        }
	        else {
	            lastRot = null;
	        }
	    } else {
	        if (mc.player.isOnGround() || mc.player.isTouchingWater() && mc.player.getItemCooldownManager().getCooldownProgress(mc.player.getInventory().getMainHandStack().getItem(), 0) < 1) {
	            int location = switchToStack(new ItemStack(Items.BUCKET));
	            if (location != 0) {
	                isOffHand = location < 0;
	                rightClickBlock(waterPlaceBlock, true);
	                if ((isOffHand ? mc.player.getOffHandStack() : mc.player.getInventory().getMainHandStack()).getItem().equals(Items.BUCKET)) {
	                    pickupRetry++;
	                    if (pickupRetry <= 10) {
	                        return;
	                    }
	                }
	            }
	            restoreRotation();
	            lastPos = null;
	            placedWater = false;
	        }
	    }
    }
	
	private void restoreRotation() {
        if (lastRot == null) return;
        mc.player.setPitch(lastRot.x);
        mc.player.setYaw(lastRot.y);
        lastRot = null;
    }
	
	private boolean causeFallDamage(BlockState bs) {
        if (bs.isAir()) return true;
        Block block = bs.getBlock();
        if (block instanceof FluidBlock || block instanceof SeaPickleBlock || block instanceof SeagrassBlock) return false;
        return true;
    }
	
	private int switchToStack(ItemStack stack) {
        PlayerInventory playerInv = mc.player.getInventory();
        int slot = playerInv.getSlotWithStack(stack);
        if (slot >= 0 && slot <= 8) {
            // bucket in hotbar
            playerInv.selectedSlot = slot;
            return 1;
        }
        else if (slot >= 9 && slot <= 35) {
            // bucket in inventory
        	/*
            if (allowInv.get()) {
                PlayerUtils.windowClick_SWAP(slot, playerInv.selectedSlot);
                return 1;
            }
            */ //this code crashes if the buckets are in the inventory, i am currently working on a fix
        	//theres no inv setting for now
        }
        else {
            // no bucket in inventory
            if (mc.player.getOffHandStack().isItemEqual(stack)) {
                // use bucket in offhand
                return -1;
            }
        }
        return 0;
    }
	
	private boolean rightClickBlock(BlockPos pos, boolean pickup) {
        Vec3d hitVec = Vec3d.ofCenter(pos).add(Vec3d.of(Direction.UP.getVector()).multiply(0.5));

        Rotation rotation = PlayerUtils.getNeededRotations(hitVec);
        mc.player.setYaw(rotation.getYaw());
        mc.player.setPitch(rotation.getPitch());
        PlayerMoveC2SPacket.LookAndOnGround packet = new PlayerMoveC2SPacket.LookAndOnGround(rotation.getYaw(),
            rotation.getPitch(), mc.player.isOnGround());
        mc.player.networkHandler.sendPacket(packet);

        if (!placedWater && !isWaterBucket) {
            mc.interactionManager.interactBlock(mc.player, mc.world, isOffHand ? Hand.OFF_HAND : Hand.MAIN_HAND,
                new BlockHitResult(new Vec3d(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5), Direction.UP, pos, false));
        }
        else {
            Vec3d eyesPos = new Vec3d(mc.player.getX(), mc.player.getY() + mc.player.getEyeHeight(
                mc.player.getPose()), mc.player.getZ());
            // check if hitVec is within range (4.25 blocks)
            if (eyesPos.squaredDistanceTo(hitVec) > 18.0625)
                return false;
            mc.interactionManager.interactItem(mc.player, mc.world, isOffHand ? Hand.OFF_HAND : Hand.MAIN_HAND);
        }
        return true;
    }
}
