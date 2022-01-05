package mathax.client.systems.modules.misc;

import mathax.client.eventbus.EventHandler;
import mathax.client.events.world.TickEvent;
import mathax.client.settings.IntSetting;
import mathax.client.settings.Setting;
import mathax.client.settings.SettingGroup;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import mathax.client.systems.modules.Modules;
import mathax.client.systems.modules.render.Freecam;
import net.minecraft.item.Items;

public class Twerk extends Module {
    private boolean hasTwerked = false;

    private int timer;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Integer> speed = sgGeneral.add(new IntSetting.Builder()
        .name("speed")
        .description("The speed of twerking.")
        .defaultValue(1)
        .min(1)
        .sliderRange(1, 100)
        .build()
    );

    public Twerk() {
        super(Categories.Misc, Items.DRIED_KELP, "twerk", "Makes you twerk like Miley Cyrus.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        timer++;
        if (timer < 10 - speed.get()) return;

        hasTwerked = !hasTwerked;

        timer = -1;
    }

    public boolean doVanilla() {
        return hasTwerked && !Modules.get().isActive(Freecam.class);
    }

    @Override
    public void onActivate() {
        timer = 0;
    }

    @Override
    public void onDeactivate() {
        hasTwerked = false;
    }
}
