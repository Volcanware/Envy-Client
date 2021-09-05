package mathax.client.legacy.systems.modules.player;

import mathax.client.legacy.settings.BoolSetting;
import mathax.client.legacy.settings.Setting;
import mathax.client.legacy.settings.SettingGroup;
import mathax.client.legacy.settings.StatusEffectListSetting;
import mathax.client.legacy.systems.modules.Categories;
import mathax.client.legacy.systems.modules.Module;
import mathax.client.legacy.utils.player.PlayerUtils;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Items;

import java.util.Arrays;
import java.util.List;

import static net.minecraft.entity.effect.StatusEffects.*;

public class PotionSaver extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<List<StatusEffect>> effects = sgGeneral.add(new StatusEffectListSetting.Builder()
        .name("effects")
        .description("The effects to preserve.")
        .defaultValue(Arrays.asList(STRENGTH, ABSORPTION, RESISTANCE, FIRE_RESISTANCE, SPEED, HASTE, REGENERATION, WATER_BREATHING, SATURATION, LUCK, SLOW_FALLING, DOLPHINS_GRACE, CONDUIT_POWER, HERO_OF_THE_VILLAGE))
        .build()
    );

    public final Setting<Boolean> onlyWhenStationary = sgGeneral.add(new BoolSetting.Builder()
        .name("only-when-stationary")
        .description("Only freezes effects when you aren't moving.")
        .defaultValue(false)
        .build()
    );

    public PotionSaver() {
        super(Categories.Player, Items.SPLASH_POTION, "potion-saver", "Stops potion effects ticking when you stand still.");
    }

    public boolean shouldFreeze(StatusEffect effect) {
        return isActive() && (!onlyWhenStationary.get() || !PlayerUtils.isMoving()) && !mc.player.getStatusEffects().isEmpty() && effects.get().contains(effect);
    }
}
