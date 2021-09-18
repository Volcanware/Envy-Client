package mathax.legacy.client.gui.tabs.builtin;

import mathax.legacy.client.MatHaxLegacy;
import mathax.legacy.client.gui.GuiTheme;
import mathax.legacy.client.gui.tabs.Tab;
import mathax.legacy.client.gui.tabs.TabScreen;
import mathax.legacy.client.gui.tabs.WindowTabScreen;
import mathax.legacy.client.settings.*;
import net.minecraft.client.gui.screen.Screen;

public class DiscordPresenceTab extends Tab {
    private static final Settings settings = new Settings();
    private static final SettingGroup sgGeneral = settings.getDefaultGroup();
    private static final SettingGroup sgServer = settings.createGroup("Server");
    private static final SettingGroup sgSmallImage = settings.createGroup("Small Image");

    public static final Setting<Boolean> enabled = sgGeneral.add(new BoolSetting.Builder()
        .name("enabled")
        .description("Toggles Discord Rich Presence.")
        .defaultValue(true)
        .onChanged(status -> {
            if (status) {
                MatHaxLegacy.DiscordRPC.init();
            } else {
                MatHaxLegacy.DiscordRPC.disable();
            }
        })
        .build()
    );

    public static final Setting<Boolean> playerHealth = sgGeneral.add(new BoolSetting.Builder()
        .name("health")
        .description("Determines if your Health will be visible on the RPC.")
        .defaultValue(true)
        .build()
    );

    public static final Setting<Boolean> serverVisibility = sgServer.add(new BoolSetting.Builder()
        .name("server-visiblity")
        .description("Determines if the server IP will be visible on the RPC.")
        .defaultValue(true)
        .build()
    );

    public static final Setting<Boolean> queuePosition = sgServer.add(new BoolSetting.Builder()
        .name("queue-position")
        .description("Appends Queue position to MatHax RPC if in Queue.")
        .defaultValue(true)
        .build()
    );

    public static final Setting<SmallImageMode> smallImageMode = sgSmallImage.add(new EnumSetting.Builder<SmallImageMode>()
        .name("small-images")
        .description("Shows cats or dogs on MatHax RPC.")
        .defaultValue(SmallImageMode.Cats)
        .build()
    );

    /*public static final Setting<Integer> switchDelay = sgSmallImage.add(new IntSetting.Builder()
        .name("switch-delay")
        .description("The delay between switching to another image in seconds.")
        .defaultValue(5)
        .min(2)
        .sliderMax(600)
        .build()
    );*/

    public DiscordPresenceTab() {
        super("Discord Presence");
    }

    public static DiscordPresenceScreen currentScreen;

    @Override
    public TabScreen createScreen(GuiTheme theme) {
        return currentScreen = new DiscordPresenceScreen(theme, this);
    }

    @Override
    public boolean isScreen(Screen screen) {
        return screen instanceof DiscordPresenceScreen;
    }

    public static class DiscordPresenceScreen extends WindowTabScreen {
        public DiscordPresenceScreen(GuiTheme theme, Tab tab) {
            super(theme, tab);

            settings.onActivated();
        }

        @Override
        public void initWidgets() {
            add(theme.settings(settings)).expandX();
        }
    }

    public enum SmallImageMode {
        Cats,
        Dogs
    }
}
