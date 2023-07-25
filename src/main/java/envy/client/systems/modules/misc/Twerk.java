package envy.client.systems.modules.misc;

import envy.client.eventbus.EventHandler;
import envy.client.events.world.TickEvent;
import envy.client.settings.EnumSetting;
import envy.client.settings.IntSetting;
import envy.client.settings.Setting;
import envy.client.settings.SettingGroup;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import net.minecraft.item.Items;

public class Twerk extends Module {
    private boolean hasTwerked = false;

    private int timer;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("Which method to sneak.")
        .defaultValue(Mode.Vanilla)
        .build()
    );

    private final Setting<Integer> speed = sgGeneral.add(new IntSetting.Builder()
        .name("speed")
        .description("The speed of twerking.")
        .defaultValue(1)
        .min(1)
        .sliderRange(1, 100)
        .build()
    );

    public Twerk() {
        super(Categories.Fun, Items.DRIED_KELP, "twerk", "Makes you twerk like Miley Cyrus.");
    }

    @Override
    public boolean onActivate() {
        timer = 0;
        return false;
    }

    @Override
    public void onDeactivate() {
        hasTwerked = false;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        timer++;
        if (timer < 10 - speed.get()) return;
        hasTwerked = !hasTwerked;
        timer = -1;
    }

    public boolean doPacket() {
        return isActive() && hasTwerked && !mc.player.getAbilities().flying && mode.get() == Mode.Packet;
    }

    public boolean doVanilla() {
        return isActive() && hasTwerked && !mc.player.getAbilities().flying && mode.get() == Mode.Vanilla;
    }

    public enum Mode {
        Packet("Packet"),
        Vanilla("Vanilla");

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
