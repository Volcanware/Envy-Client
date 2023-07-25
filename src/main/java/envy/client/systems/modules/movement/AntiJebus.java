package envy.client.systems.modules.movement;

import envy.client.eventbus.EventHandler;
import envy.client.events.world.TickEvent;
import envy.client.settings.DoubleSetting;
import envy.client.settings.EnumSetting;
import envy.client.settings.Setting;
import envy.client.settings.SettingGroup;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;

import static envy.client.systems.modules.movement.AntiJebus.Mode.NCP;
import static envy.client.systems.modules.movement.AntiJebus.Mode.Vanilla;

public class AntiJebus extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();


    public AntiJebus() {
        super(Categories.Movement, Items.IRON_BOOTS, "AntiJebus", "Stops you from leaving the water, now with NCP bypass.");
    }

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("How to Jebus")
        .defaultValue(AntiJebus.Mode.Vanilla)
        .build()
    );
    private final Setting<Double> Jebusv2 = sgGeneral.add(new DoubleSetting.Builder()
        .name("jebus sped")
        .description(" ")
        .defaultValue(1)
        .min(0.1)
        .sliderMax(10)
        .visible(() -> mode.get() == Vanilla)
        .build()
    );



    @EventHandler
    private void onTick(TickEvent.Post event) {

        if (mode.get() == NCP) {

            Block antijebus = mc.world.getBlockState(mc.player.getBlockPos().up()).getBlock();

            if (antijebus == Blocks.WATER) {
                mc.player.setVelocity(mc.player.getVelocity().x, -0.2, mc.player.getVelocity().z);
            }
            if (antijebus == Blocks.BUBBLE_COLUMN) {
                mc.player.setVelocity(mc.player.getVelocity().x, -0.2, mc.player.getVelocity().z);
            }
        }

        if (mode.get() == Vanilla) {

            Block antijebusup = mc.world.getBlockState(mc.player.getBlockPos().up()).getBlock();
            Block antijebusdown = mc.world.getBlockState(mc.player.getBlockPos().down()).getBlock();

            if (antijebusup == Blocks.WATER || antijebusdown == Blocks.WATER) {
                mc.player.setVelocity(mc.player.getVelocity().x, -Jebusv2.get(), mc.player.getVelocity().z);
            }

            if (antijebusup == Blocks.BUBBLE_COLUMN || antijebusdown == Blocks.BUBBLE_COLUMN) {
                mc.player.setVelocity(mc.player.getVelocity().x, -Jebusv2.get(), mc.player.getVelocity().z);

            }
        }
    }

    public enum Mode {
        Vanilla("Vanilla"),
        NCP("NCP");

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



