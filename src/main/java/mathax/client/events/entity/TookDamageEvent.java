package mathax.client.events.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

public class TookDamageEvent {
    private static final TookDamageEvent INSTANCE = new TookDamageEvent();

    public LivingEntity entity;
    public DamageSource source;

    public static TookDamageEvent get(LivingEntity entity, DamageSource source) {
        INSTANCE.entity = entity;
        INSTANCE.source = source;
        return INSTANCE;
    }
}
