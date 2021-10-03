package mathax.legacy.client.gui.screens;

import com.google.common.util.concurrent.Runnables;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import mathax.legacy.client.MatHaxLegacy;
import mathax.legacy.client.Version;
import mathax.legacy.client.gui.GuiThemes;
import mathax.legacy.client.gui.tabs.Tabs;
import mathax.legacy.client.systems.modules.Modules;
import mathax.legacy.client.systems.modules.misc.NameProtect;
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
        if (splashText == null) {
            splashText = client.getSplashTextLoader().get();
        }

        copyrightTextWidth = textRenderer.getWidth(COPYRIGHT);
        copyrightTextX = width - copyrightTextWidth - 2;

        int j = height / 4 + 48;
        int spacingY = 24;

        boolean bl = client.isMultiplayerEnabled();

        addDrawableChild(new ButtonWidget(width / 2 - 100, j, 200, 20, new TranslatableText("menu.singleplayer"), (button) -> client.setScreen(new SelectWorldScreen(this))));
        ButtonWidget.TooltipSupplier tooltipSupplier = bl ? ButtonWidget.EMPTY : new ButtonWidget.TooltipSupplier() {
            private final Text MULTIPLAYER_DISABLED_TEXT = new TranslatableText("title.multiplayer.disabled");

            public void onTooltip(ButtonWidget buttonWidget, MatrixStack matrixStack, int i, int j) {
                if (!buttonWidget.active) {
                    renderOrderedTooltip(matrixStack, client.textRenderer.wrapLines(MULTIPLAYER_DISABLED_TEXT, Math.max(width / 2 - 43, 170)), i, j);
                }
            }

            public void supply(Consumer<Text> consumer) {
                consumer.accept(MULTIPLAYER_DISABLED_TEXT);
            }
        };
        (addDrawableChild(new ButtonWidget(width / 2 - 100, j + spacingY, 200, 20, new TranslatableText("menu.multiplayer"), (button) -> {
            Screen screen = client.options.skipMultiplayerWarning ? new MultiplayerScreen(this) : new MultiplayerWarningScreen(this);
            client.setScreen(screen);
        }, tooltipSupplier))).active = bl;
        addDrawableChild(new ButtonWidget(width / 2 - 100, j + (spacingY * 2) + (spacingY / 2), 200, 20, new LiteralText("MatHax Website"), (button) -> Util.getOperatingSystem().open(MatHaxLegacy.URL)));
        addDrawableChild(new ButtonWidget(width / 2 - 100, j + (spacingY * 3) + (spacingY / 2), 200, 20, new LiteralText("MatHax Discord"), (button) -> Util.getOperatingSystem().open(MatHaxLegacy.URL + "Discord")));
        addDrawableChild(new ButtonWidget(width / 2 - 100, j + (spacingY * 4) + (spacingY / 2), 98, 20, new LiteralText("Click GUI"), (button) -> Tabs.get().get(0).openScreen(GuiThemes.get())));
        addDrawableChild(new ButtonWidget(width / 2 + 2, j + (spacingY * 4) + (spacingY / 2), 98, 20, new LiteralText("Check for Update"), (button) -> Version.checkForUpdate(true)));
        addDrawableChild(new ButtonWidget(width / 2 - 100, j + (spacingY * 5) + (spacingY / 2), 98, 20, new LiteralText("Proxies"), (button) -> client.setScreen(GuiThemes.get().proxiesScreen())));
        addDrawableChild(new ButtonWidget(width / 2 + 2, j + (spacingY * 5) + (spacingY / 2), 98, 20, new LiteralText("Accounts"), (button) -> client.setScreen(GuiThemes.get().accountsScreen())));
        addDrawableChild(new TexturedButtonWidget(width / 2 - 124, j + (spacingY * 7), 20, 20, 0, 106, 20, ButtonWidget.WIDGETS_TEXTURE, 256, 256, (button) -> client.setScreen(new LanguageOptionsScreen(this, client.options, client.getLanguageManager())), new TranslatableText("narrator.button.language")));
        addDrawableChild(new ButtonWidget(width / 2 - 100, j + (spacingY * 7), 98, 20, new TranslatableText("menu.options"), (button) -> client.setScreen(new OptionsScreen(this, client.options))));
        addDrawableChild(new ButtonWidget(width / 2 + 2, j + (spacingY * 7), 98, 20, new TranslatableText("menu.quit"), (button) -> client.scheduleStop()));
        addDrawableChild(new TexturedButtonWidget(width / 2 + 104, j + (spacingY * 7), 20, 20, 0, 0, 20, ACCESSIBILITY_ICON_TEXTURE, 32, 64, (button) -> client.setScreen(new AccessibilityOptionsScreen(this, client.options)), new TranslatableText("narrator.button.accessibility")));
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (backgroundFadeStart == 0L && doBackgroundFade) {
            backgroundFadeStart = Util.getMeasuringTimeMs();
        }

        float fade = doBackgroundFade ? (float)(Util.getMeasuringTimeMs() - backgroundFadeStart) / 1000.0F : 1.0F;
        float xOffset = -1.0f * (((float) mouseX - (float) width / 2.0f) / ((float) width / 32.0f));
        float yOffset = -1.0f * (((float) mouseY - (float) height / 2.0f) / ((float) height / 18.0f));

        int backgroundX = ((int)xOffset - 16) * 3;
        int backgroundY = ((int)yOffset - 16) * 2;
        int width1 = width / 2;

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
            drawTexture(matrices, width1 - 64, 15, 0.0F, 0.0F, 128, 128, 128, 128);

            if (splashText != null) {
                matrices.push();
                matrices.translate(width1 + 56, 100.0D, 0.0D);
                matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-20.0F));
                float h = 1.8F - MathHelper.abs(MathHelper.sin((float) (Util.getMeasuringTimeMs() % 1000L) / 1000.0F * 6.2831855F) * 0.1F);
                h = h * 100.0F / (float) (textRenderer.getWidth(splashText) + 32);
                matrices.scale(h, h, h);
                drawCenteredText(matrices, textRenderer, splashText, 0, -8, 16776960 | ceil);
                matrices.pop();
            }

            String minecraftVersion = "Minecraft " + SharedConstants.getGameVersion().getName() + ("release".equalsIgnoreCase(client.getVersionType()) ? "" : " - " + client.getVersionType());

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

            drawStringWithShadow(matrices, textRenderer, loggedInAs, 2, 2, GRAY);
            drawStringWithShadow(matrices, textRenderer, space, loggedInAsLength + 2, 2, GRAY);
            drawStringWithShadow(matrices, textRenderer, loggedName, loggedInAsLength + spaceLength + 2, 2, WHITE);

            if (!(Modules.get() == null) && !Modules.get().isActive(NameProtect.class) && (client.getSession().getUuid().equals(MatHaxLegacy.devUUID.replace("-", "")) || client.getSession().getUuid().equals(MatHaxLegacy.devOfflineUUID.replace("-", "")))) {
                drawStringWithShadow(matrices, textRenderer, space, loggedInAsLength + spaceLength + loggedNameLength + 2, 2, GRAY);
                drawStringWithShadow(matrices, textRenderer, loggedOpenDeveloper, loggedInAsLength + spaceLength + loggedNameLength + spaceLength + 2, 2, GRAY);
                drawStringWithShadow(matrices, textRenderer, loggedDeveloper, loggedInAsLength + spaceLength + loggedNameLength + spaceLength + loggedOpenDeveloperLength + 2, 2, MatHaxLegacy.INSTANCE.MATHAX_COLOR_INT);
                drawStringWithShadow(matrices, textRenderer, loggedCloseDeveloper, loggedInAsLength + spaceLength + loggedNameLength + spaceLength + loggedOpenDeveloperLength + loggedDeveloperLength + 2, 2, GRAY);
            }

            int watermarkPreviousWidth = 0;
            drawStringWithShadow(matrices, textRenderer, watermarkName, width - watermarkFullLength - 2, 2, MatHaxLegacy.INSTANCE.MATHAX_COLOR_INT);
            watermarkPreviousWidth += watermarkNameLength;
            drawStringWithShadow(matrices, textRenderer, space, width - watermarkFullLength + watermarkPreviousWidth - 2, 2, WHITE);
            watermarkPreviousWidth += spaceLength;
            drawStringWithShadow(matrices, textRenderer, watermarkVersion, width - watermarkFullLength + watermarkPreviousWidth - 2, 2, GRAY);

            int authorPreviousWidth = 0;
            drawStringWithShadow(matrices, textRenderer, authorBy, width - authorFullLength - 2, 16, GRAY);
            authorPreviousWidth += authorByLength;
            drawStringWithShadow(matrices, textRenderer, space, width - authorFullLength + authorPreviousWidth - 2, 16, GRAY);
            authorPreviousWidth += spaceLength;
            drawStringWithShadow(matrices, textRenderer, authorName, width - authorFullLength + authorPreviousWidth - 2, 16, WHITE);

            if (mouseX > copyrightTextX && mouseX < copyrightTextX + copyrightTextWidth && mouseY > height - 10 && mouseY < height) {
                fill(matrices, copyrightTextX, height - 1, copyrightTextX + copyrightTextWidth, height, WHITE);
            }

            for (Element element : children()) {
                if (element instanceof ClickableWidget) {
                    ((ClickableWidget) element).setAlpha(255);
                }
            }

            if (!Version.checkedForLatestTitle) {
                Version.checkedForLatestTitle = true;

                Version.checkForUpdate(false);
            }

            super.render(matrices, mouseX, mouseY, delta);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) return true;
        else {
            if (mouseX > (double)copyrightTextX && mouseX < (double)(copyrightTextX + copyrightTextWidth) && mouseY > (double)(height - 10) && mouseY < (double)height) {
                client.setScreen(new CreditsScreen(false, Runnables.doNothing()));
            }

            return false;
        }
    }
}
