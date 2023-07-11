package mathax.client.systems.modules.movement;

import mathax.client.MatHax;
import mathax.client.eventbus.EventHandler;
import mathax.client.events.entity.player.PlayerMoveEvent;
import mathax.client.events.world.TickEvent;
import mathax.client.settings.DoubleSetting;
import mathax.client.settings.Setting;
import mathax.client.settings.SettingGroup;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import mathax.client.systems.modules.movement.elytrafly.ElytraFlightModes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;

//When I started this only me and god knew how it worked, now only god knows how it works
public class ElytraFlyRecoded extends Module {
    protected double velX, velY, velZ; //This is dumb
    protected Vec3d forward, right; //This is also dumb, why I have no fucking clue

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    //TODO: Baritone integration
    public ElytraFlyRecoded() {
        super(Categories.Movement, Items.ELYTRA, "ElytraFlyRecoded", "Gives you more control over your elytra. | BETA");
    }

    public final Setting<Double> horizontalSpeed = sgGeneral.add(new DoubleSetting.Builder()
        .name("horizontal-speed")
        .description("How fast you go forward and backward.")
        .defaultValue(1)
        .min(0)
        .sliderRange(0, 3)
        .build()
    );

    @EventHandler
    private void onTick(TickEvent.Post event) {
        boolean a = false;
        boolean b = false;

        if (!(mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() instanceof ElytraItem))
            return; //Checks if the player is wearing an elytra, if this somehow breaks then what the fuck did you do
        if (mc.player.isFallFlying()) { //This is how we know we are using the elytra || Experiment with this

            if (MatHax.mc.options.forwardKey.isPressed()) { //This crashes, why, it just does, don't question it
                velX += forward.x * horizontalSpeed.get() * 10;
                velZ += forward.z * horizontalSpeed.get() * 10;
                a = true;
            }
        }
    }
}
