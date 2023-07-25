package envy.client.mixin;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(ChatHud.class)
public interface ChatHudAccessor {
    @Invoker("addMessage")
    void add(Text message, int messageId);

    @Accessor("visibleMessages")
    List<ChatHudLine<OrderedText>> getVisibleMessages();

    @Accessor("messages")
    List<ChatHudLine<Text>> getMessages();
}
