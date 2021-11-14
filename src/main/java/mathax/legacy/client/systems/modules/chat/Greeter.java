package mathax.legacy.client.systems.modules.chat;

import mathax.legacy.client.settings.BoolSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import net.minecraft.item.Items;

public class Greeter extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Boolean> ignoreFriends = sgGeneral.add(new BoolSetting.Builder()
        .name("ignore-friends")
        .description("Will not greet people which are your friends.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> ignoreEnemies = sgGeneral.add(new BoolSetting.Builder()
        .name("ignore-enemies")
        .description("Will not greet people which are your enemies.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> ignoreOthers = sgGeneral.add(new BoolSetting.Builder()
        .name("ignore-others")
        .description("Will not greet random people.")
        .defaultValue(false)
        .build()
    );

    public Greeter() {
        super(Categories.Chat, Items.PAPER, "greeter", "Greets players when they join the server.");
    }
}
