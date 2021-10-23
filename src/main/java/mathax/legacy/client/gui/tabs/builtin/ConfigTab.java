package mathax.legacy.client.gui.tabs.builtin;

import mathax.legacy.client.gui.GuiTheme;
import mathax.legacy.client.gui.tabs.Tab;
import mathax.legacy.client.gui.tabs.TabScreen;
import mathax.legacy.client.gui.tabs.WindowTabScreen;
import mathax.legacy.client.renderer.text.Fonts;
import mathax.legacy.client.systems.config.Config;
import mathax.legacy.client.utils.misc.NbtUtils;
import mathax.legacy.client.utils.render.color.RainbowColors;
import mathax.legacy.client.settings.*;
import mathax.legacy.client.utils.Utils;
import mathax.legacy.client.utils.render.prompts.YesNoPrompt;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.nbt.NbtCompound;

public class ConfigTab extends Tab {
    private static final Settings settings = new Settings();
    private static final SettingGroup sgGeneral = settings.getDefaultGroup();
    private static final SettingGroup sgChat = settings.createGroup("Chat");
    private static final SettingGroup sgToasts = settings.createGroup("Toasts");

    // General

    public static final Setting<Boolean> customFont = sgGeneral.add(new BoolSetting.Builder()
        .name("custom-font")
        .description("Use a custom font.")
        .defaultValue(true)
        .onChanged(aBoolean -> Config.get().customFont = aBoolean)
        .onModuleActivated(booleanSetting -> booleanSetting.set(Config.get().customFont))
        .build()
    );

    public static final Setting<String> font = sgGeneral.add(new ProvidedStringSetting.Builder()
        .name("font")
        .description("Custom font to use (picked from .minecraft/MatHax/Legacy/Fonts folder).")
        .supplier(Fonts::getAvailableFonts)
        .defaultValue(Fonts.DEFAULT_FONT)
        .onChanged(s -> {
            Config.get().font = s;
            Fonts.load();
        })
        .onModuleActivated(stringSetting -> stringSetting.set(Config.get().font))
        .visible(customFont::get)
        .build()
    );

    public static final Setting<Integer> rotationHoldTicks = sgGeneral.add(new IntSetting.Builder()
        .name("rotation-hold")
        .description("Hold long to hold server side rotation when not sending any packets.")
        .defaultValue(4)
        .onChanged(integer -> Config.get().rotationHoldTicks = integer)
        .onModuleActivated(integerSetting -> integerSetting.set(Config.get().rotationHoldTicks))
        .build()
    );

    public static final Setting<Boolean> useTeamColor = sgGeneral.add(new BoolSetting.Builder()
        .name("use-team-color")
        .description("Uses player's team color for rendering things like esp and tracers.")
        .defaultValue(false)
        .onChanged(aBoolean -> Config.get().useTeamColor = aBoolean)
        .onModuleActivated(booleanSetting -> booleanSetting.set(Config.get().useTeamColor))
        .build()
    );

    public static final Setting<Double> rainbowSpeed = sgGeneral.add(new DoubleSetting.Builder()
        .name("rainbow-speed")
        .description("The global rainbow speed.")
        .defaultValue(0.5)
        .range(0, 10)
        .sliderMax(5)
        .onChanged(value -> RainbowColors.GLOBAL.setSpeed(value / 100))
        .onModuleActivated(setting -> setting.set(RainbowColors.GLOBAL.getSpeed() * 100))
        .build()
    );

    // Chat

    public static final Setting<String> prefix = sgChat.add(new StringSetting.Builder()
        .name("prefix")
        .description("The command prefix.")
        .defaultValue(".")
        .onChanged(s -> Config.get().prefix = s)
        .onModuleActivated(stringSetting -> stringSetting.set(Config.get().prefix))
        .build()
    );

    public static final Setting<Boolean> openChatOnPrefix = sgChat.add(new BoolSetting.Builder()
        .name("open-chat-on-prefix")
        .description("Open chat when command prefix is pressed. Works like pressing '/' in vanilla.")
        .defaultValue(true)
        .onChanged(aBoolean -> Config.get().openChatOnPrefix = aBoolean)
        .onModuleActivated(booleanSetting -> booleanSetting.set(Config.get().openChatOnPrefix))
        .build()
    );

    public static final Setting<Boolean> rainbowPrefix = sgChat.add(new BoolSetting.Builder()
        .name("rainbow-prefix")
        .description("Makes the [MatHax Legacy] prefix in chat info rainbow.")
        .defaultValue(false)
        .onChanged(aBoolean -> Config.get().rainbowPrefix = aBoolean)
        .onModuleActivated(booleanSetting -> booleanSetting.set(Config.get().rainbowPrefix))
        .build()
    );

    public static final Setting<Boolean> chatCommandsInfo = sgChat.add(new BoolSetting.Builder()
        .name("chat-commands-info")
        .description("Sends a chat message when you use chat commands (eg toggling module, changing a setting, etc).")
        .defaultValue(true)
        .onChanged(aBoolean -> Config.get().chatCommandsInfo = aBoolean)
        .onModuleActivated(booleanSetting -> booleanSetting.set(Config.get().chatCommandsInfo))
        .build()
    );

    public static final Setting<Boolean> deleteChatCommandsInfo = sgChat.add(new BoolSetting.Builder()
        .name("delete-chat-commands-info")
        .description("Deletes previous chat messages.")
        .defaultValue(true)
        .onChanged(aBoolean -> Config.get().deleteChatCommandsInfo = aBoolean)
        .onModuleActivated(booleanSetting -> booleanSetting.set(Config.get().deleteChatCommandsInfo))
        .visible(chatCommandsInfo::get)
        .build()
    );

    // Toasts

    public static final Setting<Boolean> chatCommandsToast = sgToasts.add(new BoolSetting.Builder()
        .name("chat-commands-toast")
        .description("Sends a toast when you use chat commands (eg changing a setting, etc).")
        .defaultValue(true)
        .onChanged(aBoolean -> Config.get().chatCommandsToast = aBoolean)
        .onModuleActivated(booleanSetting -> booleanSetting.set(Config.get().chatCommandsToast))
        .build()
    );

    public static final Setting<Boolean> playSoundToast = sgToasts.add(new BoolSetting.Builder()
        .name("play-sound")
        .description("Plays a sound when a toast appears.")
        .defaultValue(true)
        .onChanged(aBoolean -> Config.get().playSoundToast = aBoolean)
        .onModuleActivated(booleanSetting -> booleanSetting.set(Config.get().playSoundToast))
        .build()
    );

    public static ConfigScreen currentScreen;

    public ConfigTab() {
        super("Config");
    }

    @Override
    public TabScreen createScreen(GuiTheme theme) {
        return currentScreen = new ConfigScreen(theme, this);
    }

    @Override
    public boolean isScreen(Screen screen) {
        return screen instanceof ConfigScreen;
    }

    public static class ConfigScreen extends WindowTabScreen {
        public ConfigScreen(GuiTheme theme, Tab tab) {
            super(theme, tab);

            settings.onActivated();

            onClosed(() -> {
                String prefix = Config.get().prefix;

                if (prefix.isBlank()) {
                    YesNoPrompt.create(theme, this.parent)
                        .title("Empty command prefix")
                        .message("You have set your command prefix to nothing.")
                        .message("This WILL prevent you from sending chat messages.")
                        .message("Do you want to reset your prefix back to '.'?")
                        .onYes(() -> Config.get().prefix = ".")
                        .id("empty-command-prefix")
                        .show();
                }
                else if (prefix.equals("/")) {
                    YesNoPrompt.create(theme, this.parent)
                        .title("Potential prefix conflict")
                        .message("You have set your command prefix to '/', which is used by minecraft.")
                        .message("This can cause conflict issues between meteor and minecraft commands.")
                        .message("Do you want to reset your prefix to '.'?")
                        .onYes(() -> Config.get().prefix = ".")
                        .id("minecraft-prefix-conflict")
                        .show();
                }
                else if (prefix.length() > 7) {
                    YesNoPrompt.create(theme, this.parent)
                        .title("Long command prefix")
                        .message("You have set your command prefix to a very long string.")
                        .message("This means that in order to execute any command, you will need to type %s followed by the command you want to run.", prefix)
                        .message("Do you want to reset your prefix back to '.'?")
                        .onYes(() -> Config.get().prefix = ".")
                        .id("long-command-prefix")
                        .show();
                }
                else if (isUsedKey()) {
                    YesNoPrompt.create(theme, this.parent)
                        .title("Prefix keybind")
                        .message("You have \"Open Chat On Prefix\" setting enabled and your command prefix has a conflict with another keybind.")
                        .message("Do you want to disable \"Open Chat On Prefix\" setting?")
                        .onYes(() -> Config.get().openChatOnPrefix = false)
                        .id("prefix-keybind")
                        .show();
                }
            });
        }

        @Override
        public void initWidgets() {
            add(theme.settings(settings)).expandX();
        }

        @Override
        public void tick() {
            super.tick();

            settings.tick(window, theme);
        }

        @Override
        public boolean toClipboard() {
            return NbtUtils.toClipboard(Config.get());
        }

        @Override
        public boolean fromClipboard() {
            NbtCompound clipboard = NbtUtils.fromClipboard(Config.get().toTag());

            if (clipboard != null) {
                Config.get().fromTag(clipboard);
                return true;
            }

            return false;
        }
    }

    private static boolean isUsedKey() {
        if (!Config.get().openChatOnPrefix) return false;

        String prefixKeybindTranslation = String.format("key.keyboard.%s",  Config.get().prefix.toLowerCase().substring(0,1));
        for (KeyBinding key: Utils.mc.options.keysAll) {
            if (key.getBoundKeyTranslationKey().equals(prefixKeybindTranslation)) return true;
        }

        return false;
    }
}
