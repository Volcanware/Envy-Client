package mathax.client.systems.modules.movement;

import mathax.client.eventbus.EventHandler;
import mathax.client.events.world.TickEvent;
import mathax.client.settings.EnumSetting;
import mathax.client.settings.Setting;
import mathax.client.settings.SettingGroup;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import mathax.client.systems.modules.Modules;
import net.minecraft.item.Items;

import static mathax.client.systems.modules.movement.FastFall.Mode.Matrix;
import static mathax.client.systems.modules.movement.FastFall.Mode.Vanilla;


public class FastFall extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public FastFall() {
        super(Categories.Movement, Items.ARMOR_STAND, "fast-fall", "Allows you to fall faster.");
    }

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("How to cancel the fall damage.")
        .defaultValue(Mode.Vanilla)
        .build()
    );


    @EventHandler
    public void onTick(TickEvent.Post event) {

        if (mode.get() == Vanilla) {
            if (mc.player.fallDistance > 0.4) {
                mc.player.setVelocity(mc.player.getVelocity().x, -1.6, mc.player.getVelocity().z);
            }
        }
        if (mode.get() == Matrix) {
            if (mc.player.fallDistance > 0.8) {
                mc.player.setVelocity(0, -0.54, 0);
            }
        }
    }

    public enum Mode {
        Vanilla("Vanilla"),
        Matrix("Matrix");

        private final String title;

        Mode(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
