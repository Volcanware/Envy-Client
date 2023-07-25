package envy.client.systems.modules.movement;

import envy.client.eventbus.EventHandler;
import envy.client.events.world.TickEvent;
import envy.client.mixininterface.IVec3d;
import envy.client.settings.DoubleSetting;
import envy.client.settings.Setting;
import envy.client.settings.SettingGroup;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;

public class Gravity extends Module {
    public Gravity() {
        super(Categories.Movement, Items.FEATHER, "gravity", "Changes gravity to moon gravity.");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> Gravity = sgGeneral.add(new DoubleSetting.Builder()
        .name("Strength")
        .description("Gravity strength.")
        .defaultValue(0.0568000030517578)
        .min(0.0000001)
        .sliderRange(0, 1)
        .build()
    );

    @EventHandler
    private void onTick(final TickEvent.Post event) {
        if (mc.options.sneakKey.isPressed()) return;
        Vec3d velocity = mc.player.getVelocity();
        ((IVec3d) velocity).set(velocity.x, velocity.y + Gravity.get(), velocity.z);
    }
}
