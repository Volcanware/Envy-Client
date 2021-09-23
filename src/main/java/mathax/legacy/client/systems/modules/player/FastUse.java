package mathax.legacy.client.systems.modules.player;

import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.settings.BoolSetting;
import mathax.legacy.client.settings.EnumSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.mixin.MinecraftClientAccessor;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.bus.EventHandler;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Items;

public class FastUse extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("Which items to fast use.")
        .defaultValue(Mode.All)
        .build()
    );

    private final Setting<Boolean> exp = sgGeneral.add(new BoolSetting.Builder()
        .name("XP")
        .description("Fast-throws XP bottles if the mode is \"Some\".")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> blocks = sgGeneral.add(new BoolSetting.Builder()
        .name("blocks")
        .description("Fast-places blocks if the mode is \"Some\".")
        .defaultValue(false)
        .build()
    );

    public FastUse() {
        super(Categories.Player, Items.PLAYER_HEAD, "fast-use", "Allows you to use items at very high speeds");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        switch (mode.get()) {
            case All:
                ((MinecraftClientAccessor) mc).setItemUseCooldown(0);
                break;
            case Some:
                if ((exp.get() && (mc.player.getMainHandStack().getItem() == Items.EXPERIENCE_BOTTLE || mc.player.getOffHandStack().getItem() == Items.EXPERIENCE_BOTTLE))
                        || (blocks.get() && (mc.player.getMainHandStack().getItem() instanceof BlockItem || mc.player.getOffHandStack().getItem() instanceof BlockItem)))
                    ((MinecraftClientAccessor) mc).setItemUseCooldown(0);
                break;
        }
    }

    public enum Mode {
        All,
        Some
    }
}
