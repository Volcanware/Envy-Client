package mathax.client.systems.modules.movement.speed;

import mathax.client.eventbus.EventHandler;
import mathax.client.events.entity.player.PlayerMoveEvent;
import mathax.client.events.packets.PacketEvent;
import mathax.client.events.world.TickEvent;
import mathax.client.settings.*;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import mathax.client.systems.modules.Modules;
import mathax.client.systems.modules.movement.speed.modes.Strafe;
import mathax.client.systems.modules.movement.speed.modes.Vanilla;
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

    public final Setting<Double> vanillaSpeed = sgGeneral.add(new DoubleSetting.Builder()
        .name("vanilla-speed")
        .description("The speed in blocks per second.")
        .defaultValue(5.6)
        .min(0)
        .sliderRange(0, 20)
        .visible(() -> speedMode.get() == SpeedModes.Vanilla)
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

    public final Setting<Double> timer = sgGeneral.add(new DoubleSetting.Builder()
        .name("timer")
        .description("Timer override.")
        .defaultValue(1)
        .min(0.01)
        .sliderRange(0.01, 10)
        .build()
    );

    public final Setting<Boolean> inLiquids = sgGeneral.add(new BoolSetting.Builder()
        .name("in-liquids")
        .description("Uses speed when in lava or water.")
        .defaultValue(true)
        .build()
    );

    public final Setting<Boolean> whenSneaking = sgGeneral.add(new BoolSetting.Builder()
        .name("when-sneaking")
        .description("Uses speed when sneaking.")
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

    public Speed() {
        super(Categories.Movement, Items.DIAMOND_BOOTS, "speed", "Modifies your movement speed when moving.");

        onSpeedModeChanged(speedMode.get());
    }

    @Override
    public void onActivate() {
        currentMode.onActivate();
    }

    @Override
    public void onDeactivate() {
        Modules.get().get(Timer.class).setOverride(Timer.OFF);
        currentMode.onDeactivate();
    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) {
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
    private void onPacketReceive(PacketEvent.Receive event) {
        if (event.packet instanceof PlayerPositionLookS2CPacket) currentMode.onRubberband();
    }

    private void onSpeedModeChanged(SpeedModes mode) {
        switch (mode) {
            case Vanilla -> currentMode = new Vanilla();
            case Strafe -> currentMode = new Strafe();
        }
    }

    @Override
    public String getInfoString() {
        return currentMode.getHudString();
    }
}
