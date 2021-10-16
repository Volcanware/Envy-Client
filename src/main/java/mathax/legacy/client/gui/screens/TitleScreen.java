package mathax.legacy.client.gui.screens;

import com.google.common.util.concurrent.Runnables;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import mathax.legacy.client.MatHaxLegacy;
import mathax.legacy.client.utils.Version;
import mathax.legacy.client.gui.GuiThemes;
import mathax.legacy.client.gui.tabs.Tabs;
import mathax.legacy.client.systems.modules.Modules;
import mathax.legacy.client.systems.modules.misc.NameProtect;
import mathax.legacy.client.systems.proxies.Proxies;
import mathax.legacy.client.systems.proxies.Proxy;
import mathax.legacy.client.utils.render.color.Color;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.option.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

// TODO: REDESIGN SO ALL BUTTONS CAN BE ACCESSED IN EVERY GUI SCALE AND RESOLUTION AND RESIZE MATHAX LOGO SO IT IS NOT SO BIG
// TODO: [LATER] Make MatHax theme & widgets work instead of buttons

public class TitleScreen extends Screen {
    private final int WHITE = Color.fromRGBA(255, 255, 255, 255);
    private final int GRAY = Color.fromRGBA(175, 175, 175, 255);

    public static final String COPYRIGHT = "Copyright Mojang AB. Do not distribute!";
    private int copyrightTextWidth;
    private int copyrightTextX;

    private static final Identifier LOGO = new Identifier("mathaxlegacy", "textures/icons/icon.png");
    private static final Identifier BACKGROUND = new Identifier("mathaxlegacy", "textures/title/background.png");
    private static final Identifier ACCESSIBILITY_ICON_TEXTURE = new Identifier("minecraft", "textures/gui/accessibility.png");

    @Nullable
    private String splashText;

    private final boolean doBackgroundFade;
    private long backgroundFadeStart;

    public TitleScreen() {
        this(false);
    }

    public TitleScreen(boolean doBackgroundFade) {
        super(new TranslatableText("narrator.screen.title"));
        this.doBackgroundFade = doBackgroundFade;
    }

    public static CompletableFuture<Void> loadTexturesAsync(TextureManager textureManager, Executor executor) {
        return CompletableFuture.allOf(textureManager.loadTextureAsync(LOGO, executor), textureManager.loadTextureAsync(BACKGROUND, executor));
    }

    public boolean isPauseScreen() {
        return false;
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }

    protected void init() {
        // Splash Loader

        if (splashText == null) splashText = client.getSplashTextLoader().get();

        // Copyright

        copyrightTextWidth = textRenderer.getWidth(COPYRIGHT);
        copyrightTextX = width - copyrightTextWidth - 2;

        // Buttons

        int y = height / 4 + 36;
        int spacingY = 24;

        addDrawableChild(new ButtonWidget(width / 2 - 204, y, 200, 20, new TranslatableText("menu.singleplayer"), (button) -> client.setScreen(new SelectWorldScreen(this))));
        addDrawableChild(new ButtonWidget(width / 2 + 4, y, 200, 20, new TranslatableText("menu.multiplayer"), (button) -> client.setScreen(new MultiplayerScreen(this))));
        addDrawableChild(new ButtonWidget(width / 2 - 204, y + (spacingY * 2) - (spacingY / 2), 200, 20, new LiteralText("Website"), (button) -> Util.getOperatingSystem().open(MatHaxLegacy.URL)));
        addDrawableChild(new ButtonWidget(width / 2 + 4, y + (spacingY * 2) - (spacingY / 2), 200, 20, new LiteralText("Discord"), (button) -> Util.getOperatingSystem().open(MatHaxLegacy.URL + "Discord")));
        addDrawableChild(new ButtonWidget(width / 2 - 204, y + (spacingY * 3) - (spacingY / 2), 96, 20, new LiteralText("Proxies"), (button) -> client.setScreen(GuiThemes.get().proxiesScreen())));
        addDrawableChild(new ButtonWidget(width / 2 - 100, y + (spacingY * 3) - (spacingY / 2), 96, 20, new LiteralText("Accounts"), (button) -> client.setScreen(GuiThemes.get().accountsScreen())));
        addDrawableChild(new ButtonWidget(width / 2 + 4, y + (spacingY * 3) - (spacingY / 2), 96, 20, new LiteralText("Click GUI"), (button) -> Tabs.get().get(0).openScreen(GuiThemes.get())));
        addDrawableChild(new ButtonWidget(width / 2 + 108, y + (spacingY * 3) - (spacingY / 2), 96, 20, new LiteralText("Check for Update"), (button) -> Version.checkForUpdate(true)));
        addDrawableChild(new TexturedButtonWidget(width / 2 - 124, y + (spacingY * 4), 20, 20, 0, 106, 20, ButtonWidget.WIDGETS_TEXTURE, 256, 256, (button) -> client.setScreen(new LanguageOptionsScreen(this, client.options, client.getLanguageManager())), new TranslatableText("narrator.button.language")));
        addDrawableChild(new ButtonWidget(width / 2 - 100, y + (spacingY * 4), 96, 20, new TranslatableText("menu.options"), (button) -> client.setScreen(new OptionsScreen(this, client.options))));
        addDrawableChild(new ButtonWidget(width / 2 + 4, y + (spacingY * 4), 96, 20, new TranslatableText("menu.quit"), (button) -> client.scheduleStop()));
        addDrawableChild(new TexturedButtonWidget(width / 2 + 104, y + (spacingY * 4), 20, 20, 0, 0, 20, ACCESSIBILITY_ICON_TEXTURE, 32, 64, (button) -> client.setScreen(new AccessibilityOptionsScreen(this, client.options)), new TranslatableText("narrator.button.accessibility")));
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (backgroundFadeStart == 0L && doBackgroundFade) backgroundFadeStart = Util.getMeasuringTimeMs();

        float fade = doBackgroundFade ? (float)(Util.getMeasuringTimeMs() - backgroundFadeStart) / 1000.0F : 1.0F;
        float xOffset = -1.0f * (((float) mouseX - (float) width / 2.0f) / ((float) width / 32.0f));
        float yOffset = -1.0f * (((float) mouseY - (float) height / 2.0f) / ((float) height / 18.0f));

        int backgroundX = ((int) xOffset - 16) * 3;
        int backgroundY = ((int) yOffset - 16) * 2;
        int widthHalf = width / 2;

        double width2 = width * 1.2;
        double height2 = height * 1.2;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, doBackgroundFade ? (float)MathHelper.ceil(MathHelper.clamp(fade, 0.0F, 1.0F)) : 1.0F);
        drawTexture(matrices, backgroundX, backgroundY, (int)width2, (int)height2, 0.0F, 0.0F, 16, 128, 16, 128);

        float fade2 = doBackgroundFade ? MathHelper.clamp(fade - 1.0F, 0.0F, 1.0F) : 1.0F;
        int ceil = MathHelper.ceil(fade2 * 255.0F) << 24;

        if ((ceil & -67108864) != 0) {
            RenderSystem.setShaderTexture(0, LOGO);
            int logoScale, splashX, splashY;
            if (client.options.guiScale < 3) {
                logoScale = 128;
                splashX = widthHalf + 56;
                splashY = 100;
            } else {
                logoScale = 64;
                splashX = widthHalf + 28;
                splashY = 75;
            }

            drawTexture(matrices, widthHalf - (logoScale / 2), 15, 0.0F, 0.0F, logoScale, logoScale, logoScale, logoScale);

            if (splashText != null) {
                matrices.push();
                matrices.translate(splashX, splashY, 0);
                matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-15.0F));
                float h = 1.8F - MathHelper.abs(MathHelper.sin((float) (Util.getMeasuringTimeMs() % 1000L) / 1000.0F * 6.2831855F) * 0.1F);
                h = h * 100.0F / (float) (textRenderer.getWidth(splashText) + 32);
                matrices.scale(h, h, h);
                drawCenteredText(matrices, textRenderer, splashText, 0, -8, 16776960 | ceil);
                matrices.pop();
            }

            String minecraftVersion = "Minecraft " + SharedConstants.getGameVersion().getName() + ("release".equalsIgnoreCase(client.getVersionType()) ? "" : " - " + client.getVersionType());

            float y = 2;
            float y2 = y + textRenderer.fontHeight + y;

            String space = " ";
            int spaceLength = textRenderer.getWidth(space);

            String loggedInAs = "Logged in as";
            String loggedName = Modules.get().get(NameProtect.class).getName(client.getSession().getUsername());
            String loggedOpenDeveloper = "[";
            String loggedDeveloper = "Developer";
            String loggedCloseDeveloper = "]";

            int loggedInAsLength = textRenderer.getWidth(loggedInAs);
            int loggedNameLength = textRenderer.getWidth(loggedName);
            int loggedOpenDeveloperLength = textRenderer.getWidth(loggedOpenDeveloper);
            int loggedDeveloperLength = textRenderer.getWidth(loggedDeveloper);

            Proxy proxy = Proxies.get().getEnabled();
            String proxyUsing = proxy != null ? "Using proxy" + " " : "Not using a proxy";
            String proxyDetails = proxy != null ? "(" + proxy.name + ") " + proxy.address + ":" + proxy.port : null;

            int proxiesLeftWidth = textRenderer.getWidth(proxyUsing);

            String watermarkName = "MatHax Legacy";
            String watermarkVersion = Version.getStylized();

            int watermarkNameLength = textRenderer.getWidth(watermarkName);
            int watermarkVersionLength = textRenderer.getWidth(watermarkVersion);
            int watermarkFullLength = watermarkNameLength + spaceLength + watermarkVersionLength;

            String authorBy = "By";
            String authorName = "Matejko06";

            int authorByLength = textRenderer.getWidth(authorBy);
            int authorNameLength = textRenderer.getWidth(authorName);
            int authorFullLength = authorByLength + spaceLength + authorNameLength;

            drawStringWithShadow(matrices, textRenderer, minecraftVersion, 2, height - 10, WHITE);

            drawStringWithShadow(matrices, textRenderer, COPYRIGHT, copyrightTextX, height - 10, WHITE);

            drawStringWithShadow(matrices, textRenderer, loggedInAs, 2, (int) y, GRAY);
            drawStringWithShadow(matrices, textRenderer, space, loggedInAsLength + 2, (int) y, GRAY);
            drawStringWithShadow(matrices, textRenderer, loggedName, loggedInAsLength + spaceLength + 2, (int) y, WHITE);

            if (!(Modules.get() == null) && !Modules.get().isActive(NameProtect.class) && (client.getSession().getUuid().equals(MatHaxLegacy.devUUID.replace("-", "")) || client.getSession().getUuid().equals(MatHaxLegacy.devOfflineUUID.replace("-", "")))) {
                drawStringWithShadow(matrices, textRenderer, space, loggedInAsLength + spaceLength + loggedNameLength + 2, (int) y, GRAY);
                drawStringWithShadow(matrices, textRenderer, loggedOpenDeveloper, loggedInAsLength + spaceLength + loggedNameLength + spaceLength + 2, (int) y, GRAY);
                drawStringWithShadow(matrices, textRenderer, loggedDeveloper, loggedInAsLength + spaceLength + loggedNameLength + spaceLength + loggedOpenDeveloperLength + 2, (int) y, MatHaxLegacy.INSTANCE.MATHAX_COLOR_INT);
                drawStringWithShadow(matrices, textRenderer, loggedCloseDeveloper, loggedInAsLength + spaceLength + loggedNameLength + spaceLength + loggedOpenDeveloperLength + loggedDeveloperLength + 2, (int) y, GRAY);
            }

            int watermarkPreviousWidth = 0;
            drawStringWithShadow(matrices, textRenderer, watermarkName, width - watermarkFullLength - 2, (int) y, MatHaxLegacy.INSTANCE.MATHAX_COLOR_INT);
            watermarkPreviousWidth += watermarkNameLength;
            drawStringWithShadow(matrices, textRenderer, space, width - watermarkFullLength + watermarkPreviousWidth - 2, (int) y, WHITE);
            watermarkPreviousWidth += spaceLength;
            drawStringWithShadow(matrices, textRenderer, watermarkVersion, width - watermarkFullLength + watermarkPreviousWidth - 2, (int) y, GRAY);

            int authorPreviousWidth = 0;
            drawStringWithShadow(matrices, textRenderer, authorBy, width - authorFullLength - 2, (int) y2, GRAY);
            authorPreviousWidth += authorByLength;
            drawStringWithShadow(matrices, textRenderer, space, width - authorFullLength + authorPreviousWidth - 2, (int) y2, GRAY);
            authorPreviousWidth += spaceLength;
            drawStringWithShadow(matrices, textRenderer, authorName, width - authorFullLength + authorPreviousWidth - 2, (int) y2, WHITE);

            drawStringWithShadow(matrices, textRenderer, proxyUsing, 2, (int) y2, GRAY);
            if (proxyDetails != null) drawStringWithShadow(matrices, textRenderer, proxyDetails, 2 + proxiesLeftWidth, (int) y2, WHITE);

            if (mouseX > copyrightTextX && mouseX < copyrightTextX + copyrightTextWidth && mouseY > height - 10 && mouseY < height) {
                fill(matrices, copyrightTextX, height - 1, copyrightTextX + copyrightTextWidth, height, WHITE);
            }

            for (Element element : children()) {
                if (element instanceof ClickableWidget) {
                    ((ClickableWidget) element).setAlpha(255);
                }
            }

            if (Version.didntCheckForLatestTitle) {
                Version.didntCheckForLatestTitle = false;

                Version.checkForUpdate(false);
            }

            super.render(matrices, mouseX, mouseY, delta);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) return true;
        else if (mouseX > (double)copyrightTextX && mouseX < (double)(copyrightTextX + copyrightTextWidth) && mouseY > (double)(height - 10) && mouseY < (double)height) client.setScreen(new CreditsScreen(false, Runnables.doNothing()));
        return false;
    }
}
