package mathax.legacy.client.mixin;

import mathax.legacy.client.MatHaxLegacy;
import mathax.legacy.client.Version;
import mathax.legacy.client.gui.GuiThemes;
import mathax.legacy.client.systems.modules.misc.NameProtect;
import mathax.legacy.client.systems.proxies.Proxy;
import mathax.legacy.client.utils.Utils;
import mathax.legacy.client.utils.misc.LastServerInfo;
import mathax.legacy.client.utils.render.color.Color;
import mathax.legacy.client.systems.modules.Modules;
import mathax.legacy.client.systems.proxies.Proxies;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin extends Screen {

    private int textColor1;
    private int textColor2;

    private String loggedInAs;
    private int loggedInAsLength;

    @Shadow
    @Final
    private Screen parent;

    public MultiplayerScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo info) {
        Version.checkedForLatestTitleText = false;
        Version.checkedForLatest = false;

        textColor1 = Color.fromRGBA(255, 255, 255, 255);
        textColor2 = Color.fromRGBA(175, 175, 175, 255);

        loggedInAs = "Logged in as ";
        loggedInAsLength = textRenderer.getWidth(loggedInAs);

        addDrawableChild(new ButtonWidget(this.width - 75 - 3, 3, 75, 20, new LiteralText("Accounts"), button -> {
            client.setScreen(GuiThemes.get().accountsScreen());
        }));

        addDrawableChild(new ButtonWidget(this.width - 75 - 3 - 75 - 2, 3, 75, 20, new LiteralText("Proxies"), button -> {
            client.setScreen(GuiThemes.get().proxiesScreen());
        }));

        addDrawableChild(new ButtonWidget(this.width / 2 - 154, 10, 100, 20, new LiteralText("Last Server"), button -> {
            LastServerInfo.reconnect(parent);
        }));
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo info) {
        float x = 3;
        float y = 3;

        // Logged in as
        textRenderer.drawWithShadow(matrices, loggedInAs, x, y, textColor1);
        textRenderer.drawWithShadow(matrices, Modules.get().get(NameProtect.class).getName(client.getSession().getUsername()) + getDeveloper(), x + loggedInAsLength, y, textColor2);

        y += textRenderer.fontHeight + 2;

        // Proxy
        Proxy proxy = Proxies.get().getEnabled();

        String left = proxy != null ? "Using proxy " : "Not using a proxy";
        String right = proxy != null ? "(" + proxy.name + ") " + proxy.ip + ":" + proxy.port : null;

        textRenderer.drawWithShadow(matrices, left, x, y, textColor1);
        if (right != null) textRenderer.drawWithShadow(matrices, right, x + textRenderer.getWidth(left), y, textColor2);
    }

    private String getDeveloper() {
        if (Modules.get() == null) return "";
        if (Modules.get().isActive(NameProtect.class)) return "";
        if ((Utils.mc.getSession().getUuid().equals(MatHaxLegacy.devUUID.replace("-", "")) || Utils.mc.getSession().getUuid().equals(MatHaxLegacy.devOfflineUUID.replace("-", "")))) return Formatting.WHITE + " [Developer]";
        else return "";
    }

    @Inject(at = {@At("HEAD")},
        method = {"connect(Lnet/minecraft/client/network/ServerInfo;)V"})
    private void onConnect(ServerInfo entry, CallbackInfo ci)
    {
        LastServerInfo.setLastServer(entry);
    }
}
