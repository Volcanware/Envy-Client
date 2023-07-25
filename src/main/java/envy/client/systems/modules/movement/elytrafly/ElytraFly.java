package envy.client.systems.modules.movement.elytrafly;

import envy.client.Envy;
import envy.client.eventbus.EventHandler;
import envy.client.events.entity.player.PlayerMoveEvent;
import envy.client.events.packets.PacketEvent;
import envy.client.events.world.TickEvent;
import envy.client.mixininterface.IVec3d;
import envy.client.settings.*;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import envy.client.systems.modules.Modules;
import envy.client.systems.modules.movement.elytrafly.modes.Packet;
import envy.client.systems.modules.movement.elytrafly.modes.Pitch40;
import envy.client.systems.modules.movement.elytrafly.modes.Vanilla;
import envy.client.systems.modules.player.ChestSwap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class ElytraFly extends Module {
    private ElytraFlightMode currentMode = new Vanilla();

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgInventory = settings.createGroup("Inventory");
    private final SettingGroup sgAutoPilot = settings.createGroup("Auto Pilot");

    // General

    public final Setting<ElytraFlightModes> flightMode = sgGeneral.add(new EnumSetting.Builder<ElytraFlightModes>()
        .name("mode")
        .description("The mode of flying.")
        .defaultValue(ElytraFlightModes.Vanilla)
        .onModuleActivated(flightModesSetting -> onModeChanged(flightModesSetting.get()))
        .onChanged(this::onModeChanged)
        .build()
    );

    public final Setting<Boolean> autoTakeOff = sgGeneral.add(new BoolSetting.Builder()
        .name("auto-take-off")
        .description("Automatically takes off when you hold jump without needing to double jump.")
        .defaultValue(false)
        .visible(() -> flightMode.get() != ElytraFlightModes.Pitch40)
        .build()
    );

    public final Setting<Double> fallMultiplier = sgGeneral.add(new DoubleSetting.Builder()
        .name("fall-multiplier")
        .description("Controls how fast will you go down naturally.")
        .defaultValue(0.01)
        .min(0)
        .sliderRange(0, 1)
        .visible(() -> flightMode.get() != ElytraFlightModes.Pitch40)
        .build()
    );

    public final Setting<Double> horizontalSpeed = sgGeneral.add(new DoubleSetting.Builder()
        .name("horizontal-speed")
        .description("How fast you go forward and backward.")
        .defaultValue(1)
        .min(0)
        .sliderRange(0, 3)
        .visible(() -> flightMode.get() != ElytraFlightModes.Pitch40)
        .build()
    );

    public final Setting<Double> verticalSpeed = sgGeneral.add(new DoubleSetting.Builder()
        .name("vertical-speed")
        .description("How fast you go up and down.")
        .defaultValue(1)
        .min(0)
        .sliderRange(0, 3)
        .visible(() -> flightMode.get() != ElytraFlightModes.Pitch40)
        .build()
    );

    public final Setting<Boolean> stopInWater = sgGeneral.add(new BoolSetting.Builder()
        .name("stop-in-water")
        .description("Stops flying in water.")
        .defaultValue(true)
        .build()
    );

    public final Setting<Boolean> dontGoIntoUnloadedChunks = sgGeneral.add(new BoolSetting.Builder()
        .name("no-unloaded-chunks")
        .description("Stops you from going into unloaded chunks.")
        .defaultValue(true)
        .build()
    );

    public final Setting<Boolean> noCrash = sgGeneral.add(new BoolSetting.Builder()
        .name("no-crash")
        .description("Stops you from going into walls.")
        .defaultValue(false)
        .build()
    );

    public final Setting<Integer> crashLookAhead = sgGeneral.add(new IntSetting.Builder()
        .name("crash-look-ahead")
        .description("Distance to look ahead when flying.")
        .defaultValue(5)
        .range(1, 15)
        .sliderRange(1, 15)
        .visible(noCrash::get)
        .build()
    );

    private final Setting<Boolean> instaDrop = sgGeneral.add(new BoolSetting.Builder()
        .name("insta-drop")
        .description("Makes you drop out of flight instantly.")
        .defaultValue(false)
        .build()
    );

    public final Setting<Double> pitch40lowerBounds = sgGeneral.add(new DoubleSetting.Builder()
        .name("pitch40-lower-bounds")
        .description("The bottom height boundary for pitch40.")
        .defaultValue(80)
        .min(0)
        .sliderRange(0, 260)
        .visible(() -> flightMode.get() == ElytraFlightModes.Pitch40)
        .build()
    );

    public final Setting<Double> pitch40upperBounds = sgGeneral.add(new DoubleSetting.Builder()
        .name("pitch40-upper-bounds")
        .description("The upper height boundary for pitch40.")
        .defaultValue(120)
        .min(0)
        .sliderRange(0, 260)
        .visible(() -> flightMode.get() == ElytraFlightModes.Pitch40)
        .build()
    );

    public final Setting<Double> pitch40rotationSpeed = sgGeneral.add(new DoubleSetting.Builder()
        .name("pitch40-rotate-speed")
        .description("The speed for pitch rotation (degrees/tick)")
        .defaultValue(4)
        .min(0)
        .sliderRange(0, 6)
        .visible(() -> flightMode.get() == ElytraFlightModes.Pitch40)
        .build()
    );

    // Inventory

    public final Setting<Boolean> replace = sgInventory.add(new BoolSetting.Builder()
        .name("elytra-replace")
        .description("Replaces broken elytra with a new elytra.")
        .defaultValue(false)
        .build()
    );

    public final Setting<Integer> replaceDurability = sgInventory.add(new IntSetting.Builder()
        .name("replace-durability")
        .description("The durability threshold your elytra will be replaced at.")
        .defaultValue(2)
        .range(1, Items.ELYTRA.getMaxDamage() - 1)
        .sliderRange(1, Items.ELYTRA.getMaxDamage() - 1)
        .visible(replace::get)
        .build()
    );

    public final Setting<ChestSwapMode> chestSwap = sgInventory.add(new EnumSetting.Builder<ChestSwapMode>()
        .name("chest-swap")
        .description("Enables ChestSwap when toggling this module.")
        .defaultValue(ChestSwapMode.Never)
        .build()
    );

    public final Setting<Boolean> autoReplenish = sgInventory.add(new BoolSetting.Builder()
        .name("replenish-fireworks")
        .description("Moves fireworks into a selected hotbar slot.")
        .defaultValue(false)
        .build()
    );

    public final Setting<Integer> replenishSlot = sgInventory.add(new IntSetting.Builder()
        .name("replenish-slot")
        .description("The slot auto move moves fireworks to.")
        .defaultValue(9)
        .range(1, 9)
        .sliderRange(1, 9)
        .visible(autoReplenish::get)
        .build()
    );

    // Auto Pilot

    public final Setting<Boolean> autoPilot = sgAutoPilot.add(new BoolSetting.Builder()
        .name("auto-pilot")
        .description("Moves forward while elytra flying.")
        .defaultValue(false)
        .visible(() -> flightMode.get() != ElytraFlightModes.Pitch40)
        .build()
    );

    public final Setting<Boolean> useFireworks = sgAutoPilot.add(new BoolSetting.Builder()
        .name("use-fireworks")
        .description("Uses firework rockets every second of your choice.")
        .defaultValue(false)
        .visible(autoPilot::get)
        .build()
    );

    public final Setting<Double> autoPilotFireworkDelay = sgAutoPilot.add(new DoubleSetting.Builder()
        .name("firework-delay")
        .description("The delay in seconds in between using fireworks if \"Use Fireworks\" is enabled.")
        .defaultValue(8)
        .min(1)
        .sliderRange(1, 20)
        .visible(useFireworks::get)
        .build()
    );

    public final Setting<Double> autoPilotMinimumHeight = sgAutoPilot.add(new DoubleSetting.Builder()
        .name("minimum-height")
        .description("The minimum height for autopilot.")
        .defaultValue(120)
        .min(0)
        .sliderRange(0, 260)
        .visible(autoPilot::get)
        .build()
    );

    public ElytraFly() {
        super(Categories.Movement, Items.ELYTRA, "elytra-fly", "Gives you more control over your elytra.");
    }

    @Override
    public boolean onActivate() {
        currentMode.onActivate();
        if ((chestSwap.get() == ChestSwapMode.Always || chestSwap.get() == ChestSwapMode.Wait_for_Ground) && mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() != Items.ELYTRA) Modules.get().get(ChestSwap.class).swap();
        return false;
    }

    @Override
    public void onDeactivate() {
        if (autoPilot.get()) mc.options.forwardKey.setPressed(false);

        if (chestSwap.get() == ChestSwapMode.Always && mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA) Modules.get().get(ChestSwap.class).swap();
        else if (chestSwap.get() == ChestSwapMode.Wait_for_Ground) enableGroundListener();

        if (mc.player.isFallFlying() && instaDrop.get()) enableInstaDropListener();

        currentMode.onDeactivate();
    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) {
        if (!(mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() instanceof ElytraItem)) return;

        currentMode.autoTakeoff();

        if (mc.player.isFallFlying()) {
            currentMode.velX = 0;
            currentMode.velY = event.movement.y;
            currentMode.velZ = 0;
            currentMode.forward = Vec3d.fromPolar(0, mc.player.getYaw()).multiply(0.1);
            currentMode.right = Vec3d.fromPolar(0, mc.player.getYaw() + 90).multiply(0.1);

            if (mc.player.isTouchingWater() && stopInWater.get()) {
                mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
                return;
            }

            currentMode.handleFallMultiplier();
            currentMode.handleAutopilot();

            currentMode.handleHorizontalSpeed(event);
            currentMode.handleVerticalSpeed(event);

            int chunkX = (int) ((mc.player.getX() + currentMode.velX) / 16);
            int chunkZ = (int) ((mc.player.getZ() + currentMode.velZ) / 16);
            if (dontGoIntoUnloadedChunks.get()) {
                if (mc.world.getChunkManager().isChunkLoaded(chunkX, chunkZ)) ((IVec3d) event.movement).set(currentMode.velX, currentMode.velY, currentMode.velZ);
                else ((IVec3d) event.movement).set(0, currentMode.velY, 0);
            } else ((IVec3d) event.movement).set(currentMode.velX, currentMode.velY, currentMode.velZ);

            currentMode.onPlayerMove();
        } else {
            if (currentMode.lastForwardPressed) {
                mc.options.forwardKey.setPressed(false);
                currentMode.lastForwardPressed = false;
            }
        }

        if (noCrash.get() && mc.player.isFallFlying()) {
            Vec3d lookAheadPos = mc.player.getPos().add(mc.player.getVelocity().normalize().multiply(crashLookAhead.get()));
            RaycastContext raycastContext = new RaycastContext(mc.player.getPos(), new Vec3d(lookAheadPos.getX(), mc.player.getY(), lookAheadPos.getZ()), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player);
            BlockHitResult hitResult = mc.world.raycast(raycastContext);
            if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) ((IVec3d) event.movement).set(0, currentMode.velY, 0);
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        currentMode.onTick();
    }

    @EventHandler
    private void onPacketSend(PacketEvent.Send event) {
        currentMode.onPacketSend(event);
    }

    private void onModeChanged(ElytraFlightModes mode) {
        switch (mode) {
            case Vanilla -> currentMode = new Vanilla();
            case Packet -> currentMode = new Packet();
            case Pitch40 -> {
                currentMode = new Pitch40();
                autoPilot.set(false);
            }
        }
    }

    //Ground
    private class StaticGroundListener {
        @EventHandler
        private void chestSwapGroundListener(PlayerMoveEvent event) {
            if (mc.player != null && mc.player.isOnGround()) {
                if (mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA) {
                    Modules.get().get(ChestSwap.class).swap();
                    disableGroundListener();
                }
            }
        }
    }

    private final StaticGroundListener staticGroundListener = new StaticGroundListener();

    protected void enableGroundListener() {
        Envy.EVENT_BUS.subscribe(staticGroundListener);
    }

    protected void disableGroundListener() {
        Envy.EVENT_BUS.unsubscribe(staticGroundListener);
    }

    private class StaticInstaDropListener {
        @EventHandler
        private void onInstaDropTick(TickEvent.Post event) {
            if (mc.player != null && mc.player.isFallFlying()) {
                mc.player.setVelocity(0, 0, 0);
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
            } else disableInstaDropListener();
        }
    }

    private final StaticInstaDropListener staticInstadropListener = new StaticInstaDropListener();

    protected void enableInstaDropListener() {
        Envy.EVENT_BUS.subscribe(staticInstadropListener);
    }

    protected void disableInstaDropListener() {
        Envy.EVENT_BUS.unsubscribe(staticInstadropListener);
    }

    @Override
    public String getInfoString() {
        return currentMode.getHudString();
    }

    public enum ChestSwapMode {
        Always("Always"),
        Never("Never"),
        Wait_for_Ground("Wait for ground");

        private final String title;

        ChestSwapMode(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    public enum AutoPilotMode {
        Vanilla("Vanilla"),
        Pitch40("Pitch 40");

        private final String title;

        AutoPilotMode(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
