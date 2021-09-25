package mathax.legacy.client.systems.modules.chat;

import mathax.legacy.client.bus.EventHandler;
import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.settings.DoubleSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import net.minecraft.item.Items;

public class StayHydrated extends Module {
    private boolean menuCounting, notifyOnJoin, count;
    private int ticks = 0;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    //General

    private final Setting<Double> delay = sgGeneral.add(new DoubleSetting.Builder()
        .name("delay")
        .description("Delay between drinking notifications in minutes.")
        .defaultValue(120)
        .min(1)
        .sliderMin(5)
        .sliderMax(180)
        .build()
    );

    public StayHydrated() {
        super(Categories.Chat, Items.WATER_BUCKET, "stay-hydrated", "Notifies you when its time to drink #StayHydrated");

        runInMainMenu = true;
    }

    @EventHandler
    public void onActivate() {
        ticks = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (count) {
            ticks++;
        }

        if (mc.world != null) count = true;

        if (notifyOnJoin && mc.world != null) {
            notifyOnJoin = false;
            ticks = 0;
            postNotification();
            return;
        }

        if (mc.world == null) {
            menuCounting = true;
            if (ticks == (delay.get() * 20) * 60) {
                notifyOnJoin = true;
                count = false;
            }

            return;
        }

        if (ticks > (delay.get() * 20) * 60) {
            postNotification();
            ticks = 0;
        }
    }

    private void postNotification() {
        info("Its time to drink! #StayHydrated");
    }
}
