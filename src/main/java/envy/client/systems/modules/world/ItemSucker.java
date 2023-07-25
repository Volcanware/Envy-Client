package envy.client.systems.modules.world;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.pathing.goals.GoalGetToBlock;
import envy.client.eventbus.EventHandler;
import envy.client.events.entity.player.PlayerMoveEvent;
import envy.client.events.game.GameLeftEvent;
import envy.client.events.world.TickEvent;
import envy.client.mixininterface.IVec3d;
import envy.client.settings.BoolSetting;
import envy.client.settings.DoubleSetting;
import envy.client.settings.Setting;
import envy.client.settings.SettingGroup;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import envy.client.utils.player.PlayerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public class ItemSucker extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> range = sgGeneral.add(new DoubleSetting.Builder()
        .name("range")
        .description("Range, in which items will be sucked.")
        .defaultValue(3.5)
        .range(1, 128)
        .sliderRange(1, 10)
        .build()
    );

    private final Setting<Boolean> boolSpeed = sgGeneral.add(new BoolSetting.Builder()
        .name("change-speed")
        .description("Change player moving speed.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Double> speed = sgGeneral.add(new DoubleSetting.Builder()
        .name("speed")
        .description("Player moving speed.")
        .defaultValue(20)
        .range(1, 128)
        .sliderRange(1, 30)
        .visible(boolSpeed::get)
        .build()
    );

    private final Setting<Boolean> goBack = sgGeneral.add(new BoolSetting.Builder()
        .name("go-back")
        .description("When all items is picked up, you will return to start position.")
        .defaultValue(true)
        .build()
    );
    //SUCCCCCCC
    boolean changeSpeed = false;
    BlockPos pos = null;

    public ItemSucker() {
        super(Categories.World, Items.ACACIA_FENCE, "item-sucker", "Sucks up all items on the ground.");
    }

    @Override
    public boolean onActivate() {pos = null;
        return false;
    }
    @EventHandler
    private void onGameLeft(GameLeftEvent event) {pos = null;}

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (changeSpeed) {
            Vec3d vel = PlayerUtils.getHorizontalVelocity(speed.get());
            double velX = vel.getX();
            double velZ = vel.getZ();
            ((IVec3d) event.movement).set(velX, event.movement.y, velZ);
        }
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        IBaritone baritone = BaritoneAPI.getProvider().getPrimaryBaritone();
        if (boolSpeed.get() && baritone.getPathingBehavior().isPathing()) changeSpeed = true;
        else if (boolSpeed.get() && !baritone.getPathingBehavior().isPathing()) changeSpeed = false;
        if (goBack.get() && pos != null) {
            baritone.getCustomGoalProcess().setGoalAndPath(new GoalGetToBlock(pos.add(0, -1, 0)));
            if (mc.player.getBlockPos().getX() == pos.getX() && mc.player.getBlockPos().getZ() == pos.getZ()) pos = null;
        }
        for (Entity entity : mc.world.getEntities()) {
            if (Objects.equals(entity.getType().toString(), "entity.minecraft.item") && mc.player.distanceTo(entity) <= range.get()) {
                if (goBack.get() && pos == null) pos = mc.player.getBlockPos();
                if (mc.player == null || mc.world == null) return;
                baritone.getCustomGoalProcess().setGoalAndPath(new GoalGetToBlock(entity.getBlockPos().add(0, -1, 0)));
            }
        }
    }
}
