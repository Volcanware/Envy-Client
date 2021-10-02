package mathax.legacy.client.systems.modules.misc;

import mathax.legacy.client.bus.EventHandler;
import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.settings.IntSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.systems.modules.Modules;
import mathax.legacy.client.systems.modules.render.Freecam;
import mathax.legacy.client.utils.misc.EnhancedTimer;
import net.minecraft.item.Items;

public class Twerk extends Module {
    private boolean hasTwerked = false;

    private final EnhancedTimer onTwerk = new EnhancedTimer();

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Integer> twerkDelay = sgGeneral.add(new IntSetting.Builder()
        .name("Delay")
        .description("Delay between twerks in ticks.")
        .defaultValue(5)
        .sliderMin(1)
        .sliderMax(10)
        .build()
    );

    public Twerk() {
        super(Categories.Misc, Items.DRIED_KELP, "Twerk", "Makes you twerk like Miley Cyrus");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (!hasTwerked && !mc.player.isSneaking()) {
            onTwerk.reset();
            hasTwerked = true;
        }

        if (onTwerk.passedTicks(twerkDelay.get()) && hasTwerked) {
            hasTwerked = false;
        }
    }

    public boolean doVanilla() {
        return hasTwerked && !Modules.get().isActive(Freecam.class);
    }

    @Override
    public void onDeactivate() {
        hasTwerked = false;
        onTwerk.reset();
    }
}
