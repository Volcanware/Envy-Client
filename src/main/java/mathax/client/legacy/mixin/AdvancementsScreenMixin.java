package mathax.client.legacy.mixin;

import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AdvancementsScreen.class)
public class AdvancementsScreenMixin {
    private static final Identifier mathaxTexture = new Identifier("mathaxlegacy", "textures/logo/big-text.png");

    @Inject(at = {@At("TAIL")}, method = {"render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V"})
    private void onRender(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        //RenderSystem.setShaderTexture(0, mathaxTexture);
        //DrawableHelper.drawTexture(matrixStack, 5, 5, 0, 0, 1392 / 5, 128 / 5, 1392 / 5, 128 / 5);
    }
}
