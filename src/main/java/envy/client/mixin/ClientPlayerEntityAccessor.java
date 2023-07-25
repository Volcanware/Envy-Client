package envy.client.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.message.ChatMessageSigner;
import net.minecraft.network.message.MessageSignature;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ClientPlayerEntity.class)
public interface ClientPlayerEntityAccessor {
    @Accessor("mountJumpStrength")
    void setMountJumpStrength(float strength);

    @Invoker("signChatMessage")
    MessageSignature _signChatMessage(ChatMessageSigner signer, Text message);
}
