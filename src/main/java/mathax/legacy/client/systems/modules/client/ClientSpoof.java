package mathax.legacy.client.systems.modules.client;

import mathax.legacy.client.MatHaxLegacy;
import mathax.legacy.client.settings.*;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.utils.render.color.SettingColor;
import net.minecraft.item.Items;

public class ClientSpoof extends Module {
    private final SettingGroup sgWatermark = settings.createGroup("Watermark");
    private final SettingGroup sgChatFeedback = settings.createGroup("Chat Feedback");

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
        .defaultValue("MatHax Legacy")
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
        .defaultValue("MatHax Legacy")
        .build()
    );

    public final Setting<SettingColor> chatFeedbackTextColor = sgChatFeedback.add(new ColorSetting.Builder()
        .name("color")
        .description("The text color of the chat feedback.")
        .defaultValue(new SettingColor(MatHaxLegacy.INSTANCE.MATHAX_COLOR.r, MatHaxLegacy.INSTANCE.MATHAX_COLOR.g, MatHaxLegacy.INSTANCE.MATHAX_COLOR.b))
        .build()
    );

    public ClientSpoof() {
        super(Categories.Client, Items.COMMAND_BLOCK, "client-spoof", "Allows you to change the name of the client.");
    }

    public boolean changeWatermark() {
        return isActive() && watermark.get();
    }

    public boolean changeChatFeedback() {
        return isActive() && chatFeedback.get();
    }
}
