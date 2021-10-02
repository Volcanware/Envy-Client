package mathax.legacy.client.systems.modules.misc;

import mathax.legacy.client.MatHaxLegacy;
import mathax.legacy.client.Version;
import mathax.legacy.client.gui.screens.*;
import mathax.legacy.client.gui.screens.TitleScreen;
import mathax.legacy.client.gui.screens.settings.ColorSettingScreen;
import mathax.legacy.client.gui.tabs.builtin.*;
import mathax.legacy.client.settings.*;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.systems.modules.Modules;
import mathax.legacy.client.utils.Utils;
import mathax.legacy.client.utils.render.PeekScreen;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.*;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.EditWorldScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.item.Items;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscordRPC extends Module {
    private static final String APP_ID = "878967665501306920";
    private static final String STEAM_ID = "";
    private static final DiscordRichPresence rpc = new DiscordRichPresence();
    private static final DiscordEventHandlers handlers = new DiscordEventHandlers();

    public static int delay = 0;
    public static int number = 1;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Boolean> playerHealth = sgGeneral.add(new BoolSetting.Builder()
        .name("health")
        .description("Determines if your Health will be visible on the RPC.")
        .defaultValue(true)
        .build()
    );

    public final Setting<Boolean> serverVisibility = sgGeneral.add(new BoolSetting.Builder()
        .name("server-visiblity")
        .description("Determines if the server IP will be visible on the RPC.")
        .defaultValue(true)
        .build()
    );

    private final Setting<SmallImageMode> smallImageMode = sgGeneral.add(new EnumSetting.Builder<SmallImageMode>()
        .name("small-images")
        .description("Shows cats or dogs on MatHax RPC.")
        .defaultValue(SmallImageMode.Cats)
        .build()
    );

    public DiscordRPC() {
        super(Categories.Misc, Items.COMMAND_BLOCK, "discord-RPC", "Shows MatHax Legacy as your Discord status");

        runInMainMenu = true;
    }

    @Override
    public void onActivate() {
        MatHaxLegacy.LOG.info(MatHaxLegacy.logprefix + "Enabling Discord Rich Presence...");
        net.arikia.dev.drpc.DiscordRPC.discordInitialize(APP_ID, handlers, true, STEAM_ID);
        rpc.startTimestamp = System.currentTimeMillis() / 1000;
        rpc.details = Placeholders.apply("%version% | %username%" + Utils.getDiscordPlayerHealth());
        rpc.state = Placeholders.apply("%activity%");
        rpc.largeImageKey = "logo";
        rpc.largeImageText = "MatHax Legacy " + Version.getStylized();
        applySmallImage();
        rpc.smallImageText = Placeholders.apply("%activity%");
        rpc.partyId = "ae488379-351d-4a4f-ad32-2b9b01c91657";
        rpc.joinSecret = "MTI4NzM0OjFpMmhuZToxMjMxMjM=";
        rpc.partySize = MinecraftClient.getInstance().getNetworkHandler() != null ? MinecraftClient.getInstance().getNetworkHandler().getPlayerList().size() : 1;
        rpc.partyMax = 1;
        net.arikia.dev.drpc.DiscordRPC.discordUpdatePresence(rpc);
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                net.arikia.dev.drpc.DiscordRPC.discordRunCallbacks();
                try {
                    rpc.details = Placeholders.apply("%version% | %username%" + Utils.getDiscordPlayerHealth());
                    rpc.state = Placeholders.apply("%activity%");
                    rpc.largeImageKey = "logo";
                    rpc.largeImageText = "MatHax Legacy " + Version.getStylized();
                    applySmallImage();
                    rpc.smallImageText = Placeholders.apply("%activity%");
                    rpc.partySize = MinecraftClient.getInstance().getNetworkHandler() != null ? MinecraftClient.getInstance().getNetworkHandler().getPlayerList().size() : 1;
                    rpc.partyMax = 1;
                    net.arikia.dev.drpc.DiscordRPC.discordUpdatePresence(rpc);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }, "RPC-Callback-Handler").start();
        MatHaxLegacy.LOG.info(MatHaxLegacy.logprefix + "Discord Rich Presence enabled!");
    }

    @Override
    public void onDeactivate() {
        MatHaxLegacy.LOG.info(MatHaxLegacy.logprefix + "Disabling Discord Rich Presence...");
        net.arikia.dev.drpc.DiscordRPC.discordClearPresence();
        net.arikia.dev.drpc.DiscordRPC.discordShutdown();
        MatHaxLegacy.LOG.info(MatHaxLegacy.logprefix + "Discord Rich Presence disabled!");
    }

    // For shutdown hook
    public static void deactivate() {
        MatHaxLegacy.LOG.info(MatHaxLegacy.logprefix + "Disabling Discord Rich Presence...");
        net.arikia.dev.drpc.DiscordRPC.discordClearPresence();
        net.arikia.dev.drpc.DiscordRPC.discordShutdown();
        MatHaxLegacy.LOG.info(MatHaxLegacy.logprefix + "Discord Rich Presence disabled!");
    }

    private static class Placeholders  {
        private static final Pattern pattern = Pattern.compile("(%activity%|%version%|%username%|%health%)");

        public static String apply(String string) {
            Matcher matcher = pattern.matcher(string);
            StringBuffer sb = new StringBuffer(string.length());

            while (matcher.find()) {
                matcher.appendReplacement(sb, getReplacement(matcher.group(1)));
            }
            matcher.appendTail(sb);

            return sb.toString();
        }

        private static String getReplacement(String placeholder) {
            switch (placeholder) {
                case "%activity%":
                    if (MinecraftClient.getInstance() == null || MinecraftClient.getInstance().getOverlay() instanceof SplashOverlay) {
                        return "Minecraft is loading...";
                    } else if (MinecraftClient.getInstance().currentScreen instanceof TitleScreen || MinecraftClient.getInstance().currentScreen instanceof net.minecraft.client.gui.screen.TitleScreen) {
                        return "In main menu";
                    } else if (MinecraftClient.getInstance().currentScreen instanceof MultiplayerScreen) {
                        return "In server selection";
                    } else if (MinecraftClient.getInstance().currentScreen instanceof ServerManagerScreen) {
                        return "In server selection";
                    } else if (MinecraftClient.getInstance().currentScreen instanceof ServerFinderScreen) {
                        return "Using server finder";
                    } else if (MinecraftClient.getInstance().currentScreen instanceof CleanUpScreen) {
                        return "Using server cleanup";
                    } else if (MinecraftClient.getInstance().currentScreen instanceof ConnectScreen) {
                        return "Connecting to " + Utils.getNakedActivity();
                    } else if (MinecraftClient.getInstance().currentScreen instanceof DisconnectedScreen) {
                        return "Got disconnected from " + Utils.getNakedActivity();
                    } else if (MinecraftClient.getInstance().currentScreen instanceof GameMenuScreen) {
                        return "Game paused on " + Utils.getNakedActivity();
                    } else if (MinecraftClient.getInstance().currentScreen instanceof PeekScreen) {
                        return "Using .peek on " + Utils.getNakedActivity();
                    } else if (MinecraftClient.getInstance().currentScreen instanceof StatsScreen) {
                        return "Viewing stats";
                    } else if (MinecraftClient.getInstance().currentScreen instanceof OptionsScreen) {
                        return "Changing Minecraft settings";
                    } else if (MinecraftClient.getInstance().currentScreen instanceof AccessibilityOptionsScreen) {
                        return "Changing Minecraft accessibility settings";
                    } else if (MinecraftClient.getInstance().currentScreen instanceof ChatOptionsScreen) {
                        return "Changing Minecraft chat settings";
                    } else if (MinecraftClient.getInstance().currentScreen instanceof SoundOptionsScreen) {
                        return "Changing Minecraft sound settings";
                    } else if (MinecraftClient.getInstance().currentScreen instanceof LanguageOptionsScreen) {
                        return "Changing Minecraft language";
                    } else if (MinecraftClient.getInstance().currentScreen instanceof VideoOptionsScreen) {
                        return "Changing Minecraft video settings";
                    } else if (MinecraftClient.getInstance().currentScreen instanceof SkinOptionsScreen) {
                        return "Changing Minecraft skin settings";
                    } else if (MinecraftClient.getInstance().currentScreen instanceof PackScreen) {
                        return "Changing resourcepack";
                    } else if (MinecraftClient.getInstance().currentScreen instanceof ControlsOptionsScreen) {
                        return "Changing Minecraft keybinds";
                    } else if (MinecraftClient.getInstance().currentScreen instanceof NarratorOptionsScreen) {
                        return "Changing Narrator settings";
                    } else if (MinecraftClient.getInstance().currentScreen instanceof SelectWorldScreen) {
                        return "In world selection";
                    } else if (MinecraftClient.getInstance().currentScreen instanceof EditWorldScreen) {
                        return "Editing a world";
                    } else if (MinecraftClient.getInstance().currentScreen instanceof CreateWorldScreen) {
                        return "Creating a new world";
                    } else if (MinecraftClient.getInstance().currentScreen instanceof AddServerScreen) {
                        return "Adding a server";
                    } else if (MinecraftClient.getInstance().currentScreen instanceof DirectConnectScreen) {
                        return "In direct connect";
                    } else if (MinecraftClient.getInstance().currentScreen instanceof ModuleScreen) {
                        return "Editing module " + ((ModuleScreen) MinecraftClient.getInstance().currentScreen).module.title;
                    } else if (MinecraftClient.getInstance().currentScreen instanceof BaritoneTab.BaritoneScreen) {
                        return "Configuring Baritone";
                    } else if (MinecraftClient.getInstance().currentScreen instanceof ConfigTab.ConfigScreen) {
                        return "Editing Config";
                    } else if (MinecraftClient.getInstance().currentScreen instanceof EnemiesTab.EnemiesScreen) {
                        return "Editing Enemies";
                    } else if (MinecraftClient.getInstance().currentScreen instanceof FriendsTab.FriendsScreen) {
                        return "Editing Friends";
                    } else if (MinecraftClient.getInstance().currentScreen instanceof GuiTab.GuiScreen) {
                        return "Editing GUI";
                    } else if (MinecraftClient.getInstance().currentScreen instanceof HudTab.HudScreen) {
                        return "Editing HUD";
                    } else if (MinecraftClient.getInstance().currentScreen instanceof HudElementScreen) {
                        return "Editing HUD";
                    } else if (MinecraftClient.getInstance().currentScreen instanceof MacrosTab.MacrosScreen) {
                        return "Configuring Macros";
                    } else if (MinecraftClient.getInstance().currentScreen instanceof MacrosTab.MacroEditorScreen) {
                        return "Configuring a Macro";
                    } else if (MinecraftClient.getInstance().currentScreen instanceof ModulesScreen) {
                        return "In click gui";
                    } else if (MinecraftClient.getInstance().currentScreen instanceof ProfilesTab.ProfilesScreen) {
                        return "Changing profiles";
                    } else if (MinecraftClient.getInstance().currentScreen instanceof ColorSettingScreen) {
                        return "Changing color";
                    } else if (MinecraftClient.getInstance().currentScreen instanceof AccountsScreen) {
                        return "In account manager";
                    } else if (MinecraftClient.getInstance().currentScreen instanceof AddAlteningAccountScreen) {
                        return "Adding the altening alt";
                    } else if (MinecraftClient.getInstance().currentScreen instanceof AddCrackedAccountScreen) {
                        return "Adding cracked account";
                    } else if (MinecraftClient.getInstance().currentScreen instanceof AddPremiumAccountScreen) {
                        return "Adding premium account";
                    } else if (MinecraftClient.getInstance().currentScreen instanceof ProxiesScreen) {
                        return "Editing proxies";
                    } else {
                        if (!(MinecraftClient.getInstance().world == null)) {
                            return Utils.getActivity();
                        } else {
                            return "In " + MinecraftClient.getInstance().currentScreen.getTitle().toString();
                        }
                    }
                case "%version%":
                    return Version.getStylized();
                case "%username%":
                    return MinecraftClient.getInstance().getSession().getUsername();
                default:
                    return "In " + MinecraftClient.getInstance().currentScreen.getTitle().toString();
            }
        }
    }

    private static void applySmallImage() {
        if (delay == 5) {
            if (number == 16) number = 1;
            if (Modules.get().get(DiscordRPC.class).smallImageMode.get() == SmallImageMode.Dogs) {
                rpc.smallImageKey = "dog-" + number;
            } else {
                rpc.smallImageKey = "cat-" + number;
            }
            ++number;
            delay = 0;
        } else {
            ++delay;
        }
    }

    public enum SmallImageMode {
        Cats,
        Dogs
    }
}
