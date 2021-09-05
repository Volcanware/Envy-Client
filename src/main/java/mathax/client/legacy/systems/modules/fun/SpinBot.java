package mathax.client.legacy.systems.modules.fun;

import mathax.client.legacy.bus.EventHandler;
import mathax.client.legacy.events.world.TickEvent;
import mathax.client.legacy.settings.IntSetting;
import mathax.client.legacy.settings.Setting;
import mathax.client.legacy.settings.SettingGroup;
import mathax.client.legacy.systems.modules.Categories;
import mathax.client.legacy.systems.modules.Module;
import mathax.client.legacy.systems.modules.Modules;
import mathax.client.legacy.systems.modules.player.EXPThrower;
import mathax.client.legacy.utils.player.Rotations;
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
        super(Categories.Fun, Items.GUNPOWDER, "spin-bot", "Makes you spin like Spin Bot in CS:GO.");
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
