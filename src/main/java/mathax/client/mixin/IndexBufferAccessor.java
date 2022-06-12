package mathax.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderSystem.IndexBuffer.class)
public interface IndexBufferAccessor {
    @Accessor("id")
    int getId();
}
