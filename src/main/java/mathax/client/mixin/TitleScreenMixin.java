package mathax.client.mixin;

import mathax.client.MatHax;
import mathax.client.systems.modules.Modules;
import mathax.client.systems.modules.misc.NameProtect;
import mathax.client.systems.proxies.Proxies;
import mathax.client.systems.proxies.Proxy;
import mathax.client.utils.Utils;
import mathax.client.utils.Version;
import mathax.client.utils.render.color.Color;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.Person;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.Comparator;
import java.util.List;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
    private final int WHITE = Color.fromRGBA(255, 255, 255, 255);
    private final int GRAY = Color.fromRGBA(175, 175, 175, 255);
    private final int RED = Color.fromRGBA(255, 0, 0, 255);

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)I", ordinal = 0))
    private void checkForUpdate(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (Version.UpdateChecker.checkForLatestTitle) {
            Version.UpdateChecker.checkForLatestTitle = false;

            Version.UpdateChecker.checkForUpdate();
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        float y = 2;
        float y2 = y + textRenderer.fontHeight + y;

        String space = " ";
        int spaceLength = textRenderer.getWidth(space);

        String loggedInAs = "Logged in as";
        int loggedInAsLength = textRenderer.getWidth(loggedInAs);
        String loggedName = Modules.get().get(NameProtect.class).getName(client.getSession().getUsername());
        int loggedNameLength = textRenderer.getWidth(loggedName);
        String loggedOpenDeveloper = "[";
        int loggedOpenDeveloperLength = textRenderer.getWidth(loggedOpenDeveloper);
        String loggedDeveloper = "Developer";
        int loggedDeveloperLength = textRenderer.getWidth(loggedDeveloper);
        String loggedCloseDeveloper = "]";


        Proxy proxy = Proxies.get().getEnabled();
        String proxyUsing = proxy != null ? "Using proxy" + " " : "Not using a proxy";
        int proxyUsingLength = textRenderer.getWidth(proxyUsing);
        String proxyDetails = proxy != null ? "(" + proxy.name + ") " + proxy.address + ":" + proxy.port : null;

        String watermarkName = "Envy Client";
        int watermarkNameLength = textRenderer.getWidth(watermarkName);
        String watermarkVersion = Version.getStylized();
        int watermarkVersionLength = textRenderer.getWidth(watermarkVersion);
        int watermarkFullLength = watermarkNameLength + spaceLength + watermarkVersionLength;

        String authorBy = "Made by:";
        int authorByLength = textRenderer.getWidth(authorBy);

        context.drawTextWithShadow(textRenderer, loggedInAs, 2, (int) y, GRAY);
        context.drawTextWithShadow(textRenderer, space, loggedInAsLength + 2, (int) y, GRAY);
        context.drawTextWithShadow(textRenderer, loggedName, loggedInAsLength + spaceLength + 2, (int) y, WHITE);

        if (Modules.get() != null && !Modules.get().isActive(NameProtect.class) && Utils.isDeveloper(client.getSession().getUuid())) {
            context.drawTextWithShadow(textRenderer, space, loggedInAsLength + spaceLength + loggedNameLength + 2, (int) y, GRAY);
            context.drawTextWithShadow(textRenderer, loggedOpenDeveloper, loggedInAsLength + spaceLength + loggedNameLength + spaceLength + 2, (int) y, GRAY);
            context.drawTextWithShadow(textRenderer, loggedDeveloper, loggedInAsLength + spaceLength + loggedNameLength + spaceLength + loggedOpenDeveloperLength + 2, (int) y, MatHax.INSTANCE.MATHAX_COLOR_INT);
            context.drawTextWithShadow(textRenderer, loggedCloseDeveloper, loggedInAsLength + spaceLength + loggedNameLength + spaceLength + loggedOpenDeveloperLength + loggedDeveloperLength + 2, (int) y, GRAY);
        }

        int watermarkPreviousWidth = 0;
        context.drawTextWithShadow(textRenderer, watermarkName, width - watermarkFullLength - 2, (int) y, MatHax.INSTANCE.MATHAX_COLOR_INT);
        watermarkPreviousWidth += watermarkNameLength;
        context.drawTextWithShadow(textRenderer, space, width - watermarkFullLength + watermarkPreviousWidth - 2, (int) y, WHITE);
        watermarkPreviousWidth += spaceLength;
        context.drawTextWithShadow(textRenderer, watermarkVersion, width - watermarkFullLength + watermarkPreviousWidth - 2, (int) y, GRAY);

        int authorY = (int) y2;
        context.drawTextWithShadow(textRenderer, authorBy, width - authorByLength - 2, authorY, GRAY);

        List<String> importantAuthors = List.of("Volcan");
        for (String importantAuthor : importantAuthors) {
            authorY += textRenderer.fontHeight + 1;
            context.drawTextWithShadow(textRenderer, importantAuthor, width - textRenderer.getWidth(importantAuthor), authorY, RED);
        }

        assert FabricLoader.getInstance().getModContainer("envy").isPresent();  // 🤔 should we ever be not loaded? (skid protection)
        for (Person author : FabricLoader.getInstance().getModContainer("envy").get().getMetadata().getAuthors().stream().filter(person -> !importantAuthors.contains(person.getName())).sorted(Comparator.comparingInt(person -> textRenderer.getWidth(person.getName()))).toList()) {
            authorY += textRenderer.fontHeight + 1;
            context.drawTextWithShadow(textRenderer, author.getName(), width - textRenderer.getWidth(author.getName()), authorY, WHITE);
        }

        context.drawTextWithShadow(textRenderer, proxyUsing, 2, (int) y2, GRAY);
        if (proxyDetails != null) context.drawTextWithShadow(textRenderer, proxyDetails, 2 + proxyUsingLength, (int) y2, WHITE);
    }
}
