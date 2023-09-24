package mathax.client.mixin;

import mathax.client.systems.modules.Modules;
import mathax.client.utils.misc.LastServerInfo;
import mathax.client.systems.modules.misc.AutoReconnect;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static mathax.client.MatHax.mc;

@Mixin(DisconnectedScreen.class)
public abstract class DisconnectedScreenMixin extends Screen {
    @Unique private ButtonWidget reconnectBtn;
    @Unique private double time = Modules.get().get(AutoReconnect.class).time.get() * 20;

    protected DisconnectedScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/GridWidget;refreshPositions()V", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    private void addButtons(CallbackInfo info, GridWidget.Adder adder) {
        AutoReconnect autoReconnect = Modules.get().get(AutoReconnect.class);

        if (autoReconnect.lastServerConnection != null) {
            reconnectBtn = adder.add(new ButtonWidget.Builder(Text.literal(getText()), button -> tryConnecting()).build());

            adder.add(
                new ButtonWidget.Builder(Text.literal("Toggle Auto Reconnect"), button -> {
                    autoReconnect.toggle();
                    reconnectBtn.setMessage(Text.literal(getText()));
                    time = autoReconnect.time.get() * 20;
                }).build()
            );
        }
    }

    @Override
    public void tick() {
        AutoReconnect autoReconnect = Modules.get().get(AutoReconnect.class);
        if (!autoReconnect.isActive() || autoReconnect.lastServerConnection == null) return;

        if (time <= 0) {
            tryConnecting();
        } else {
            time--;
            if (reconnectBtn != null) reconnectBtn.setMessage(Text.literal(getText()));
        }
    }

    private String getText() {
        String reconnectText = "Reconnect";
        if (Modules.get().isActive(AutoReconnect.class)) reconnectText += " " + String.format("(%.1f)", time / 20);
        return reconnectText;
    }

    private void tryConnecting() {
        var lastServer = Modules.get().get(AutoReconnect.class).lastServerConnection;
        ConnectScreen.connect(new TitleScreen(), mc, lastServer.left(), lastServer.right(), false);
    }
}
