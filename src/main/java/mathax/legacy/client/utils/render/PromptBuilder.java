package mathax.legacy.client.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import mathax.legacy.client.gui.GuiTheme;
import mathax.legacy.client.gui.GuiThemes;
import mathax.legacy.client.gui.WindowScreen;
import mathax.legacy.client.gui.widgets.containers.WHorizontalList;
import mathax.legacy.client.gui.widgets.pressable.WButton;
import mathax.legacy.client.gui.widgets.pressable.WCheckbox;
import mathax.legacy.client.systems.config.Config;
import net.minecraft.client.gui.screen.Screen;

import java.util.ArrayList;
import java.util.List;

import static mathax.legacy.client.utils.Utils.mc;

public class PromptBuilder {
    private final GuiTheme theme;
    private final Screen parent;
    private String title = "";
    private final List<String> messages = new ArrayList<>();
    private Runnable onYes = () -> {};
    private Runnable onNo = () -> {};
    public static String promptId = null;
    private WCheckbox dontShowAgainCheckbox;

    public PromptBuilder() {
        this(GuiThemes.get(), mc.currentScreen);
    }

    public PromptBuilder(GuiTheme theme, Screen parent) {
        this.theme = theme;
        this.parent = parent;
    }

    public PromptBuilder title(String title) {
        this.title = title;
        return this;
    }

    public PromptBuilder message(String message) {
        this.messages.add(message);
        return this;
    }

    public PromptBuilder message(String message, Object... args) {
        this.messages.add(String.format(message, args));
        return this;
    }

    public PromptBuilder onYes(Runnable runnable) {
        this.onYes = runnable;
        return this;
    }

    public PromptBuilder onNo(Runnable runnable) {
        this.onNo = runnable;
        return this;
    }

    public PromptBuilder promptId(String from) {
        this.promptId = from;
        return this;
    }

    public void show() {
        if (promptId == null) this.promptId(this.title);

        if (Config.get().dontShowAgainPrompts.contains(promptId)) {
            onNo.run();
            return;
        }

        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
                Screen prompt = new PromptScreen(theme);
                mc.setScreen(prompt);
            });
        }
        else {
            Screen prompt = new PromptScreen(theme);
            mc.setScreen(prompt);
        }
    }

    public class PromptScreen extends WindowScreen {
        public PromptScreen(GuiTheme theme) {
            super(theme, PromptBuilder.this.title);

            this.parent = PromptBuilder.this.parent;
        }

        @Override
        public void initWidgets() {
            for (String line : messages) {
                add(theme.label(line)).expandX();
            }

            add(theme.horizontalSeparator()).expandX();

            if (!promptId.equals("new-update-button")) {
                WHorizontalList checkboxContainer = add(theme.horizontalList()).expandX().widget();
                dontShowAgainCheckbox = checkboxContainer.add(theme.checkbox(false)).widget();
                checkboxContainer.add(theme.label("Don't show this prompt again.")).expandX();
            }

            WHorizontalList list = add(theme.horizontalList()).expandX().widget();

            WButton yesButton = list.add(theme.button("Yes")).expandX().widget();
            yesButton.action = () -> {
                onYes.run();
                this.onClose();
            };

            WButton noButton = list.add(theme.button("No")).expandX().widget();
            noButton.action = () -> {
                onNo.run();
                if (!promptId.equals("new-update-button")) {
                    if (dontShowAgainCheckbox.checked)
                        Config.get().dontShowAgainPrompts.add(promptId);
                }
                this.onClose();
            };

            if (!promptId.equals("new-update-button")) {
                dontShowAgainCheckbox.action = () -> {
                    yesButton.visible = !dontShowAgainCheckbox.checked;
                };
            }
        }
    }
}
