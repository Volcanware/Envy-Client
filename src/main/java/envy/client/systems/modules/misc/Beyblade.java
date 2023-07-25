package envy.client.systems.modules.misc;

import envy.client.eventbus.EventHandler;
import envy.client.events.world.TickEvent;
import envy.client.settings.*;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import envy.client.systems.modules.Modules;
import envy.client.systems.modules.combat.BedAura;
import envy.client.systems.modules.player.EXPThrower;
import envy.client.utils.player.Rotations;
import net.minecraft.item.BowItem;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.ExperienceBottleItem;
import net.minecraft.item.Items;

public class Beyblade extends Module {
    private final SettingGroup sgDefault = settings.getDefaultGroup();

    private final Setting<Mode> antiDesync = sgDefault.add(new EnumSetting.Builder<Mode>()
        .name("anti-desync")
        .description("Stops spinning on some triggers.")
        .defaultValue(Mode.All)
        .build()
    );

    private final Setting<Boolean> yaw = sgDefault.add(new BoolSetting.Builder()
        .name("yaw")
        .description("Spin around.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Integer> ySpeed = sgDefault.add(new IntSetting.Builder()
        .name("yaw-speed")
        .description("The speed at which you rotate.")
        .defaultValue(5)
        .range(1, 100)
        .visible(yaw::get)
        .build()
    );

    private final Setting<Boolean> pitch = sgDefault.add(new BoolSetting.Builder()
        .name("pitch")
        .description("Spin around.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Integer> pSpeed = sgDefault.add(new IntSetting.Builder()
        .name("speed")
        .description("The speed at which you rotate.")
        .defaultValue(5)
        .range(1, 100)
        .visible(pitch::get)
        .build()
    );

    public Beyblade() {
        super(Categories.Misc,Items.BEDROCK, "Beyblade", "Tries to rotate you.");
    }

    private short count = 0;
    private short yCount = 0;
    private short pCount = 0;

    @Override
    public boolean onActivate() {
        count = 0;
        return false;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        assert mc.player != null;

        switch (antiDesync.get()) {
            case All -> {
                if (Modules.get().isActive(EXPThrower.class) ||
                    Modules.get().isActive(BedAura.class) ||
                    mc.player.getMainHandStack().getItem() instanceof ExperienceBottleItem ||
                    mc.player.getOffHandStack().getItem() instanceof ExperienceBottleItem ||
                    mc.player.getMainHandStack().getItem() instanceof EnderPearlItem ||
                    mc.player.getOffHandStack().getItem() instanceof EnderPearlItem ||
                    mc.player.getMainHandStack().getItem() instanceof ExperienceBottleItem ||
                    mc.player.getOffHandStack().getItem() instanceof ExperienceBottleItem ||
                    mc.player.getMainHandStack().getItem() instanceof BowItem ||
                    mc.player.getOffHandStack().getItem() instanceof BowItem ||
                    mc.player.getInventory().getArmorStack(2).getItem() == Items.ELYTRA) return;
            }
            case ExceptElytra -> {
                if (Modules.get().isActive(EXPThrower.class) ||
                    Modules.get().isActive(BedAura.class) ||
                    mc.player.getMainHandStack().getItem() instanceof ExperienceBottleItem ||
                    mc.player.getOffHandStack().getItem() instanceof ExperienceBottleItem ||
                    mc.player.getMainHandStack().getItem() instanceof EnderPearlItem ||
                    mc.player.getOffHandStack().getItem() instanceof EnderPearlItem ||
                    mc.player.getMainHandStack().getItem() instanceof ExperienceBottleItem ||
                    mc.player.getOffHandStack().getItem() instanceof ExperienceBottleItem ||
                    mc.player.getMainHandStack().getItem() instanceof BowItem ||
                    mc.player.getOffHandStack().getItem() instanceof BowItem) return;
            }
        }

        yCount += ySpeed.get();
        if (yCount > 180) yCount = -180;

        if (pitch.get()) {
            count++;

            if (count <= pSpeed.get()) pCount = 90;
            if (count > pSpeed.get()) pCount = -90;
            if (count >= pSpeed.get() + pSpeed.get()) count = 0;
        }

        Rotations.rotate(yaw.get() ? yCount : mc.player.getYaw(), yaw.get() ? pCount : mc.player.getPitch());
    }

    public enum Mode {
        All, ExceptElytra, None
    }
}
