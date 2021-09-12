package mathax.legacy.client.systems.modules.player;

import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.settings.DoubleSetting;
import mathax.legacy.client.settings.EnumSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.mixin.StatusEffectInstanceAccessor;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.bus.EventHandler;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Items;

import static net.minecraft.entity.effect.StatusEffects.HASTE;

public class SpeedMine extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .defaultValue(Mode.Normal)
        .build()
    );
    public final Setting<Double> modifier = sgGeneral.add(new DoubleSetting.Builder()
        .name("modifier")
        .description("Mining speed modifier. An additional value of 0.2 is equivalent to one haste level (1.2 = haste 1).")
        .defaultValue(1.4D)
        .min(0D)
        .sliderMin(1D)
        .sliderMax(10D)
        .build()
    );

    public SpeedMine() {
        super(Categories.Player, Items.GOLDEN_PICKAXE, "speed-mine");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        Mode mode = this.mode.get();

        if (mode == Mode.Haste1 || mode == Mode.Haste2) {
            int amplifier = mode == Mode.Haste2 ? 1 : 0;
            if (mc.player.hasStatusEffect(HASTE)) {
                StatusEffectInstance effect = mc.player.getStatusEffect(HASTE);
                ((StatusEffectInstanceAccessor) effect).setAmplifier(amplifier);
                if (effect.getDuration() < 20) {
                    ((StatusEffectInstanceAccessor) effect).setDuration(20);
                }
            } else {
                mc.player.addStatusEffect(new StatusEffectInstance(HASTE, 20, amplifier, false, false, false));
            }
        }
    }

    public enum Mode {
        Normal,
        Haste1,
        Haste2
    }
}
