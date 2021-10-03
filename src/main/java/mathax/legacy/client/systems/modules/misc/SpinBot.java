package mathax.legacy.client.systems.modules.misc;

import mathax.legacy.client.bus.EventHandler;
import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.settings.IntSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.systems.modules.Modules;
import mathax.legacy.client.systems.modules.player.EXPThrower;
import mathax.legacy.client.utils.player.Rotations;
import net.minecraft.item.BowItem;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.ExperienceBottleItem;
import net.minecraft.item.Items;

public class SpinBot extends Module {
    private short count;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Integer> speed = sgGeneral.add(new IntSetting.Builder()
        .name("spin-speed")
        .description("The speed at which you spin.")
        .defaultValue(25)
        .min(0)
        .sliderMax(100)
        .build()
    );

    public SpinBot() {
        super(Categories.Misc, Items.GUNPOWDER, "spin-bot", "Makes you spin like Spin bot in CS:GO.");
        count = 0;
    }

    @EventHandler
    public void onTick(TickEvent.Post post) {
        if (Modules.get().isActive(EXPThrower.class) || mc.player.getMainHandStack().getItem() instanceof ExperienceBottleItem || mc.player.getMainHandStack().getItem() instanceof ExperienceBottleItem || mc.player.getMainHandStack().getItem() instanceof EnderPearlItem || mc.player.getMainHandStack().getItem() instanceof EnderPearlItem || mc.player.getMainHandStack().getItem() instanceof ExperienceBottleItem || mc.player.getMainHandStack().getItem() instanceof ExperienceBottleItem || mc.player.getMainHandStack().getItem() instanceof BowItem || mc.player.getMainHandStack().getItem() instanceof BowItem || mc.player.getInventory().getArmorStack(2).getItem() == Items.ELYTRA) {
            return;
        }
        count = (short)(count + speed.get());
        if (count > 180) {
            count = (short)-180;
        }
        Rotations.rotate(count, 0.0);
    }
}
