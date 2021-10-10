package mathax.legacy.client.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
    private boolean canFade = true;

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init()V", at = @At("HEAD"))
    private void init(CallbackInfo info) {
        MinecraftClient.getInstance().setScreen(new mathax.legacy.client.gui.screens.TitleScreen(canFade));
        canFade = false;
    }
}
