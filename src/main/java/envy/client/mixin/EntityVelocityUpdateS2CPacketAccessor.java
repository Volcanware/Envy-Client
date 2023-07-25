package envy.client.mixin;

import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityVelocityUpdateS2CPacket.class)
public interface EntityVelocityUpdateS2CPacketAccessor {
    @Mutable
    @Accessor("velocityX")
    void setX(int velocityX);

    @Mutable
    @Accessor("velocityY")
    void setY(int velocityY);

    @Mutable
    @Accessor("velocityZ")
    void setZ(int velocityZ);
}

