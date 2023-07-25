package envy.client.mixin;

import envy.client.utils.Version;
import envy.client.utils.misc.LastServerInfo;
import net.minecraft.client.gui.screen.DirectConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DirectConnectScreen.class)
public class DirectConnectScreenMixin extends Screen {

	@Shadow
    @Final
    private ServerInfo serverEntry;

	private DirectConnectScreenMixin(Text title) {
        super(title);
	}

	@Inject(at = @At("TAIL"), method = "saveAndClose()V")
	private void onSaveAndClose(CallbackInfo info) {
        Version.UpdateChecker.checkForLatest = true;
		LastServerInfo.setLastServer(serverEntry);
	}
}
