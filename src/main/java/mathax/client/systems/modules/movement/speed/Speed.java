package mathax.client.systems.modules.movement.speed;

import mathax.client.eventbus.EventHandler;
import mathax.client.events.entity.player.PlayerMoveEvent;
import mathax.client.events.packets.PacketEvent;
import mathax.client.events.world.TickEvent;
import mathax.client.settings.*;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import mathax.client.systems.modules.Modules;
import mathax.client.systems.modules.movement.speed.modes.*;
import mathax.client.systems.modules.movement.speed.modes.MineBerry;
import mathax.client.systems.modules.movement.speed.modes.VelocityHop;
import mathax.client.utils.player.PlayerUtils;
import mathax.client.systems.modules.world.Timer;
import net.minecraft.entity.MovementType;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;



public class Speed extends Module {
    private SpeedMode currentMode;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<SpeedModes> speedMode = sgGeneral.add(new EnumSetting.Builder<SpeedModes>()
        .name("mode")
        .description("The method of applying speed.")
        .defaultValue(SpeedModes.Strafe)
        .onModuleActivated(speedModesSetting -> onSpeedModeChanged(speedModesSetting.get()))
        .onChanged(this::onSpeedModeChanged)
        .build()
    );

    public final Setting<Boolean> autojump = sgGeneral.add(new BoolSetting.Builder()
        .name("AutoJump")
        .description("Jumps when your on the ground")
        .defaultValue(true)
        .visible(() -> speedMode.get() == SpeedModes.Custom)
        .build()
    );

    public final Setting<Double> lowhealthdisable = sgGeneral.add(new DoubleSetting.Builder()
        .name("LowHealthDisable")
        .description("Disables the module when your health is below this value.")
        .defaultValue(4)
        .min(0.5)
        .sliderMax(20)
        .visible(() -> speedMode.get() == SpeedModes.Custom)
        .build()
    );

    public final Setting<Boolean> autoSprint = sgGeneral.add(new BoolSetting.Builder()
        .name("AutoSprint")
        .description("Keeps Sprint On")
        .defaultValue(true)
        .visible(() -> speedMode.get() == SpeedModes.Custom)
        .build()
    );

    public final Setting<Double> airstrafe= sgGeneral.add(new DoubleSetting.Builder()
        .name("AirStrafe")
        .description("The speed in the air")
        .defaultValue(1.1)
        .min(0)
        .sliderRange(0, 1.5)
        .visible(() -> speedMode.get() == SpeedModes.Custom)
        .build()
    );

    public final Setting<Double> groundStrafe= sgGeneral.add(new DoubleSetting.Builder()
        .name("GroundStrafe")
        .description("The speed on the ground")
        .defaultValue(1.1)
        .min(0)
        .sliderRange(0, 1.5)
        .visible(() -> speedMode.get() == SpeedModes.Custom)
        .build()
    );

    public final Setting<Double> onfire= sgGeneral.add(new DoubleSetting.Builder()
        .name("OnFire Speed")
        .description("The speed while on fire")
        .defaultValue(1.1)
        .min(0)
        .sliderRange(0, 2)
        .visible(() -> speedMode.get() == SpeedModes.Custom)
        .build()
    );

    public final Setting<Double> floating= sgGeneral.add(new DoubleSetting.Builder()
        .name("Float")
        .description("how much to float")
        .defaultValue(1)
        .min(1)
        .sliderRange(1, 10)
        .visible(() -> speedMode.get() == SpeedModes.Custom)
        .build()
    );

    public final Setting<Double> vanillaSpeed = sgGeneral.add(new DoubleSetting.Builder()
        .name("vanilla-speed")
        .description("The speed in blocks per second.")
        .defaultValue(5.6)
        .min(0)
        .sliderRange(0, 35)
        .visible(() -> speedMode.get() == SpeedModes.Vanilla)
        .build()
    );

    public final Setting<Double> SpeedTest2 = sgGeneral.add(new DoubleSetting.Builder()
        .name("Speed")
        .description("Speed Value")
        .defaultValue(1)
        .min(0)
        .sliderRange(0, 12)
        .visible(() -> speedMode.get() == SpeedModes.Vanilla)
        .build()
    );

    public final Setting<Double> VelocityHop = sgGeneral.add(new DoubleSetting.Builder()
        .name("VelocityHop-speed")
        .description("The speed in blocks per second.")
        .defaultValue(6)
        .min(0)
        .sliderRange(0, 20)
        .visible(() -> speedMode.get() == SpeedModes.VelocityHop)
        .build()
    );

    public final Setting<Double> EnvyAnarchy = sgGeneral.add(new DoubleSetting.Builder()
        .name("Envy-Speed")
        .description("The speed in blocks per second.")
        .defaultValue(8)
        .min(1)
        .sliderRange(1, 12)
        .visible(() -> speedMode.get() == SpeedModes.EnvyAnarchy)
        .build()
    );

    public final Setting<Double> ncpSpeed = sgGeneral.add(new DoubleSetting.Builder()
        .name("strafe-speed")
        .description("The speed.")
        .visible(() -> speedMode.get() == SpeedModes.Strafe)
        .defaultValue(1.6)
        .min(0)
        .sliderRange(0, 3)
        .build()
    );

    public final Setting<Boolean> ncpSpeedLimit = sgGeneral.add(new BoolSetting.Builder()
        .name("speed-limit")
        .description("Limits your speed on servers with very strict anticheats.")
        .visible(() -> speedMode.get() == SpeedModes.Strafe)
        .defaultValue(false)
        .build()
    );

    public final Setting<Double> bowspeed = sgGeneral.add(new DoubleSetting.Builder()
        .name("bow-speed")
        .description("The speed when holding a bow")
        .visible(() -> speedMode.get() == SpeedModes.Custom)
        .defaultValue(1)
        .min(0)
        .sliderRange(0, 2)
        .build()
    );

    public final Setting<Double> swordspeed = sgGeneral.add(new DoubleSetting.Builder()
        .name("sword-speed")
        .description("The speed when holding a sword")
        .visible(() -> speedMode.get() == SpeedModes.Custom)
        .defaultValue(1)
        .min(0)
        .sliderRange(0, 2)
        .build()
    );


    public final Setting<Boolean> groundspoof = sgGeneral.add(new BoolSetting.Builder()
        .name("groundspoof")
        .description("Spoofs your ground status.")
        .visible(() -> speedMode.get() == SpeedModes.Custom)
        .defaultValue(false)
        .build()
    );

    public final Setting<Boolean> rubberband = sgGeneral.add(new BoolSetting.Builder()
        .name("rubberband")
        .description("Disables Speed When you Rubberband / Teleport")
        .visible(() -> speedMode.get() == SpeedModes.Custom)
        .defaultValue(false)
        .build()
    );

    public final Setting<Double> timer = sgGeneral.add(new DoubleSetting.Builder()
        .name("timer")
        .description("Timer override.")
        .defaultValue(1)
        .visible(() -> speedMode.get() == SpeedModes.Vanilla || speedMode.get() == SpeedModes.Strafe || speedMode.get() == SpeedModes.Custom)
        .build()
    );

    public final Setting<Boolean> inLiquids = sgGeneral.add(new BoolSetting.Builder()
        .name("in-liquids")
        .description("Uses speed when in lava or water.")
        .defaultValue(true)
        .visible(() -> speedMode.get() == SpeedModes.Vanilla || speedMode.get() == SpeedModes.Strafe)
        .build()
    );
    public final Setting<Boolean> whenSneaking = sgGeneral.add(new BoolSetting.Builder()
        .name("when-sneaking")
        .description("Uses speed when sneaking.")
        .visible(() -> speedMode.get() == SpeedModes.Vanilla || speedMode.get() == SpeedModes.Strafe)
        .defaultValue(true)
        .build()
    );

    public final Setting<Boolean> vanillaOnGround = sgGeneral.add(new BoolSetting.Builder()
        .name("only-on-ground")
        .description("Uses speed only when standing on a block.")
        .visible(() -> speedMode.get() == SpeedModes.Vanilla)
        .defaultValue(false)
        .build()
    );

    public final Setting<Double> dumbspeed = sgGeneral.add(new DoubleSetting.Builder()
        .name("dumbspeed")
        .description("Speed, of which is dumb.")
        .defaultValue(1)
        .visible(() -> speedMode.get() == SpeedModes.dumbspeed)
        .build()
    );

    public Speed() {
        super(Categories.Movement, Items.DIAMOND_BOOTS, "speed", "Modifies your movement speed when moving.");

        onSpeedModeChanged(speedMode.get());
    }

    @Override
    public boolean onActivate() {
        currentMode.onActivate();
        return false;
    }

    @Override
    public void onDeactivate() {
        Modules.get().get(Timer.class).setOverride(Timer.OFF);
        currentMode.onDeactivate();
    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) throws InterruptedException {
        if (event.type != MovementType.SELF || mc.player.isFallFlying() || mc.player.isClimbing() || mc.player.getVehicle() != null) return;
        if (!whenSneaking.get() && mc.player.isSneaking()) return;
        if (vanillaOnGround.get() && !mc.player.isOnGround() && speedMode.get() == SpeedModes.Vanilla) return;
        if (!inLiquids.get() && (mc.player.isTouchingWater() || mc.player.isInLava())) return;

        Modules.get().get(Timer.class).setOverride(PlayerUtils.isMoving() ? timer.get() : Timer.OFF);

        currentMode.onMove(event);
    }

    @EventHandler
    private void onPreTick(TickEvent.Pre event) {
        if (mc.player.isFallFlying() || mc.player.isClimbing() || mc.player.getVehicle() != null) return;
        if (!whenSneaking.get() && mc.player.isSneaking()) return;
        if (vanillaOnGround.get() && !mc.player.isOnGround() && speedMode.get() == SpeedModes.Vanilla) return;
        if (!inLiquids.get() && (mc.player.isTouchingWater() || mc.player.isInLava())) return;

        currentMode.onTick();
    }

    @EventHandler
    private void onPreTick2(TickEvent.Pre event) {
        currentMode.onTickEventPre(event);
    }

    @EventHandler
    private void onPacketReceive(PacketEvent.Receive event) {
        if (event.packet instanceof PlayerPositionLookS2CPacket) currentMode.onRubberband();
    }

    private void onSpeedModeChanged(SpeedModes mode) {
        switch (mode) {
            case Vanilla -> currentMode = new Vanilla();
            case Strafe -> currentMode = new Strafe();
            case MineBerry -> currentMode = new MineBerry();
            case VelocityHop -> currentMode = new VelocityHop();
            case Weird -> currentMode = new Weird();
            case LBL_SlowHop -> currentMode = new LBL_SlowHop();
            case Vulcan -> currentMode = new Vulcan();
            case OnGround -> currentMode = new OnGround();
            case LegitHop -> currentMode = new LegitHop();
            case EnvyAnarchy -> currentMode = new EnvyAnarchy();
            case Test3 -> currentMode = new SpeedTest3();
            case TimerHop -> currentMode = new TimerHop();
            case dumbspeed -> currentMode = new dumbspeed();
            case NONONOFUCK -> currentMode = new NONONOFUCK();
            case Chinese -> currentMode = new Chinese();
            case EnvyHop -> currentMode = new EnvyHop();
            case Custom -> currentMode = new Custom();
        }
    }

    @Override
    public String getInfoString() {
        return currentMode.getHudString();
    }
}
