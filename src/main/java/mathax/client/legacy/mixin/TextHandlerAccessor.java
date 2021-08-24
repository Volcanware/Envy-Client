package mathax.client.legacy.mixin;

import net.minecraft.client.font.TextHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TextHandler.class)
public interface TextHandlerAccessor {
    @Accessor("widthRetriever")
    TextHandler.WidthRetriever getWidthRetriever();
}
