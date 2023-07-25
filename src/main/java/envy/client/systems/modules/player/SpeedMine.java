package envy.client.systems.modules.player;

import envy.client.eventbus.EventHandler;
import envy.client.events.world.TickEvent;
import envy.client.mixin.StatusEffectInstanceAccessor;
import envy.client.settings.DoubleSetting;
import envy.client.settings.EnumSetting;
import envy.client.settings.Setting;
import envy.client.settings.SettingGroup;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Items;

import static net.minecraft.entity.effect.StatusEffects.HASTE;

public class SpeedMine extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    public final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("Determines how to modify the speed.")
        .defaultValue(Mode.Normal)
        .build()
    );
    public final Setting<Double> modifier = sgGeneral.add(new DoubleSetting.Builder()
        .name("modifier")
        .description("Mining speed modifier. An additional value of 0.2 is equivalent to one haste level (1.2 = haste 1).")
        .defaultValue(1.4)
        .min(0)
        .build()
    );

    public SpeedMine() {
        super(Categories.Player, Items.GOLDEN_PICKAXE, "fast-mine", "Allows you to quickly mine blocks.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {

        if (mode.get() == Mode.Normal) return;

        int amplifier = mode.get() == Mode.Haste2 ? 1 : 0;

        if (!mc.player.hasStatusEffect(HASTE)) mc.player.addStatusEffect(new StatusEffectInstance(HASTE, 255, amplifier, false, false, false));

        StatusEffectInstance effect = mc.player.getStatusEffect(HASTE);
        ((StatusEffectInstanceAccessor) effect).setAmplifier(amplifier);
        if (effect.getDuration() < 20) ((StatusEffectInstanceAccessor) effect).setDuration(20);
    }

    public enum Mode {
        Normal("Normal"),
        Haste1("Haste I"),
        Haste2("Haste II");

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
