package mathax.legacy.client.systems.modules.client;

import mathax.legacy.client.settings.*;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.utils.Version;
import mathax.legacy.client.utils.render.color.SettingColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;

public class ClientSpoof extends Module {
    private final SettingGroup sgWatermark = settings.createGroup("Watermark");
    private final SettingGroup sgChatFeedback = settings.createGroup("Chat Feedback");
    private final SettingGroup sgWindow = settings.createGroup("Window");

    // Watermark

    public final Setting<Boolean> watermark = sgWatermark.add(new BoolSetting.Builder()
        .name("enabled")
        .description("Changes the watermark client name.")
        .defaultValue(false)
        .build()
    );

    public final Setting<String> watermarkText = sgWatermark.add(new StringSetting.Builder()
        .name("text")
        .description("The text to replace the watermark with.")
        .defaultValue("Meteor Client")
        .build()
    );

    public final Setting<Boolean> watermarkMeteorIcon = sgWatermark.add(new BoolSetting.Builder()
        .name("meteor-icon")
        .description("Changes the watermark icon to Meteor.")
        .defaultValue(true)
        .build()
    );

    // Chat Feedback

    public final Setting<Boolean> chatFeedback = sgChatFeedback.add(new BoolSetting.Builder()
        .name("enabled")
        .description("Changes the chat feedback client name.")
        .defaultValue(false)
        .build()
    );

    public final Setting<String> chatFeedbackText = sgChatFeedback.add(new StringSetting.Builder()
        .name("text")
        .description("The text to replace chat feedback with.")
        .defaultValue("Meteor")
        .build()
    );

    public final Setting<Boolean> chatFeedbackChangeTextColor = sgChatFeedback.add(new BoolSetting.Builder()
        .name("change-color")
        .description("Changes the chat feedback client name color.")
        .defaultValue(true)
        .build()
    );

    public final Setting<SettingColor> chatFeedbackTextColor = sgChatFeedback.add(new ColorSetting.Builder()
        .name("color")
        .description("The text color of the chat feedback.")
        .defaultValue(new SettingColor(145, 61, 226))
        .build()
    );

    public final Setting<Boolean> chatFeedbackMeteorIcon = sgChatFeedback.add(new BoolSetting.Builder()
        .name("meteor-icon")
        .description("Changes the chat feedback icon to Meteor.")
        .defaultValue(true)
        .build()
    );

    // Window

    public final Setting<Boolean> window = sgWindow.add(new BoolSetting.Builder()
        .name("enabled")
        .description("Changes the title and icon of the window.")
        .defaultValue(false)
        .onChanged(window -> {
            titleChanged();
            iconChanged();
        })
        .build()
    );

    public final Setting<Boolean> windowMeteorTitle = sgWindow.add(new BoolSetting.Builder()
        .name("meteor-title")
        .description("Changes the title of the window to Meteor.")
        .defaultValue(true)
        .onChanged(title -> titleChanged())
        .build()
    );

    public final Setting<Boolean> windowMeteorIcon = sgWindow.add(new BoolSetting.Builder()
        .name("meteor-icon")
        .description("Changes the window icon to Meteor.")
        .defaultValue(true)
        .onChanged(icon -> iconChanged())
        .build()
    );

    public ClientSpoof() {
        super(Categories.Client, Items.COMMAND_BLOCK, "client-spoof", "Allows you to change the name of the client.");

        runInMainMenu = true;
    }

    @Override
    public void onActivate() {
        // Icon
        if (window.get() && windowMeteorIcon.get()) setMeteorIcon();

        // Window Title
        if (window.get() && windowMeteorTitle.get()) setMeteorTitle();
    }

    @Override
    public void onDeactivate() {
        // Icon
        resetIcon();

        // Window Title
        resetTitle();
    }

    private void titleChanged() {
        if (window.get()) {
            if (windowMeteorTitle.get()) setMeteorTitle();
            else resetTitle();
        } else resetTitle();
    }

    public void setMeteorTitle() {
        mc.getWindow().setTitle("Meteor Client " + Version.getStylized() + " - " + MinecraftClient.getInstance().getVersionType() + " " + Version.getMinecraft());
    }

    private void resetTitle() {
        mc.getWindow().setTitle("MatHax Legacy " + Version.getStylized() + " - " + MinecraftClient.getInstance().getVersionType() + " " + Version.getMinecraft());
    }

    private void iconChanged() {
        if (window.get()) {
            if (windowMeteorIcon.get()) setMeteorIcon();
            else resetIcon();
        } else resetIcon();
    }

    public void setMeteorIcon() {
        mc.getWindow().setIcon(getClass().getResourceAsStream("/assets/mathaxlegacy/textures/icons/meteor64.png"), getClass().getResourceAsStream("/assets/mathaxlegacy/textures/icons/meteor128.png"));
    }

    private void resetIcon() {
        mc.getWindow().setIcon(getClass().getResourceAsStream("/assets/mathaxlegacy/textures/icons/icon64.png"), getClass().getResourceAsStream("/assets/mathaxlegacy/textures/icons/icon128.png"));
    }

    public boolean changeWatermark() {
        return isActive() && watermark.get();
    }

    public boolean changeWatermarkIcon() {
        return isActive() && watermark.get() && watermarkMeteorIcon.get();
    }

    public boolean changeChatFeedback() {
        return isActive() && chatFeedback.get();
    }

    public boolean changeChatFeedbackColor() {
        return isActive() && chatFeedback.get() && chatFeedbackChangeTextColor.get();
    }

    public boolean changeChatFeedbackIcon() {
        return isActive() && chatFeedback.get() && chatFeedbackMeteorIcon.get();
    }

    public boolean changeWindowTitle() {
        return isActive() && window.get() && windowMeteorTitle.get();
    }

    public boolean changeWindowIcon() {
        return isActive() && window.get() && windowMeteorIcon.get();
    }
}
