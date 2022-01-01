package mathax.client.mixin;

import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.screen.HorseScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HorseScreenHandler.class)
public interface HorseScreenHandlerAccessor {
    @Accessor("entity")
    HorseBaseEntity getEntity();
}
