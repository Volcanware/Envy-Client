package mathax.client.legacy.mixin;

import mathax.client.legacy.renderer.GL;
import mathax.client.legacy.renderer.Renderer2D;
import mathax.client.legacy.systems.config.Config;
import mathax.client.legacy.systems.modules.Modules;
import mathax.client.legacy.systems.modules.misc.BetterTab;
import mathax.client.legacy.utils.Utils;
import mathax.client.legacy.utils.render.color.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {
    private static final Identifier mathaxLogo = new Identifier("mathaxlegacy", "textures/logo/logo.png");

    private Color textureColor = new Color(255, 255, 255, 255);

    private int x = 0;
    private int y = 0;
    private PlayerListEntry playerListEntry = null;

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I", ordinal = 0), index = 1)
    private int modifyCount(int count) {
        BetterTab module = Modules.get().get(BetterTab.class);

        return module.isActive() ? module.tabSize.get() : count;
    }

    @Inject(method = "getPlayerName", at = @At("HEAD"), cancellable = true)
    public void getPlayerName(PlayerListEntry playerListEntry, CallbackInfoReturnable<Text> info) {
        BetterTab betterTab = Modules.get().get(BetterTab.class);

        if (betterTab.isActive()) info.setReturnValue(betterTab.getPlayerName(playerListEntry));
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I", ordinal = 1), index = 0)
    private int modifyWidth(int width) {
        BetterTab module = Modules.get().get(BetterTab.class);

        return module.isActive() && module.accurateLatency.get() ? width + 32 : width;
    }

    @Shadow
    protected void renderLatencyIcon(MatrixStack matrices, int width, int x, int y, PlayerListEntry entry) {}

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;renderLatencyIcon(Lnet/minecraft/client/util/math/MatrixStack;IIILnet/minecraft/client/network/PlayerListEntry;)V"))
    protected void renderLatencyIcon(PlayerListHud self, MatrixStack matrices, int width, int x, int y, PlayerListEntry entry) {
        BetterTab betterTab = Modules.get().get(BetterTab.class);

        if (betterTab.isActive() && betterTab.accurateLatency.get()) {
            MinecraftClient mc = MinecraftClient.getInstance();
            TextRenderer textRenderer = mc.textRenderer;

            int latency = Utils.clamp(entry.getLatency(), 0, 99999);
            int color = latency < 150 ? 0x00E970 : latency < 300 ? 0xE7D020 : 0xD74238;
            String text = latency + "ms";
            textRenderer.drawWithShadow(matrices, text, (float) x + width - textRenderer.getWidth(text), (float) y, color);
        } else {
            /*if ((entry.getProfile().getId().toString().equals("3e24ef27-e66d-45d2-bf4b-2c7ade68ff47") || entry.getProfile().getId().toString().equals("7c73f844-73c3-3a7d-9978-004ba0a6436e")) && betterTab.isActive() && Config.get().viewMatHaxLegacyUsers) {

            }*/
            renderLatencyIcon(matrices, width, x, y, entry);
        }
    }
}
