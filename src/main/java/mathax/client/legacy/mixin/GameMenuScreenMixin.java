package mathax.client.legacy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen {
    private static final Identifier mathaxTexture = new Identifier("mathaxlegacy", "textures/logo/big-text.png");

    public GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Inject(at = {@At("TAIL")}, method = {"render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V"})
    private void onRender(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        //RenderSystem.setShaderTexture(0, mathaxTexture);
        //DrawableHelper.drawTexture(matrixStack, 5, 5, 0, 0, 1392 / 5, 128 / 5, 1392 / 5, 128 / 5);
    }
}
