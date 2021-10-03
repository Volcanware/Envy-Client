package mathax.legacy.client.mixin;

import mathax.legacy.client.gui.screens.TitleScreen;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init()V", at = @At("HEAD"))
    private void init(CallbackInfo info) {
        MinecraftClient.getInstance().setScreen(new mathax.legacy.client.gui.screens.TitleScreen());
    }
}
