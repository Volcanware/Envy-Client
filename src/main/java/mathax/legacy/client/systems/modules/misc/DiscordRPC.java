package mathax.legacy.client.systems.modules.misc;

import mathax.legacy.client.MatHaxLegacy;
import mathax.legacy.client.Version;
import mathax.legacy.client.gui.WidgetScreen;
import mathax.legacy.client.gui.screens.*;
import mathax.legacy.client.gui.screens.TitleScreen;
import mathax.legacy.client.gui.screens.accounts.AccountsScreen;
import mathax.legacy.client.gui.screens.accounts.AddAlteningAccountScreen;
import mathax.legacy.client.gui.screens.accounts.AddCrackedAccountScreen;
import mathax.legacy.client.gui.screens.accounts.AddPremiumAccountScreen;
import mathax.legacy.client.gui.screens.clickgui.ModuleScreen;
import mathax.legacy.client.gui.screens.clickgui.ModulesScreen;
import mathax.legacy.client.gui.screens.servermanager.ServerCleanUpScreen;
import mathax.legacy.client.gui.screens.servermanager.ServerFinderScreen;
import mathax.legacy.client.gui.screens.servermanager.ServerManagerScreen;
import mathax.legacy.client.gui.screens.settings.ColorSettingScreen;
import mathax.legacy.client.gui.tabs.builtin.*;
import mathax.legacy.client.mixin.MinecraftServerAccessor;
import mathax.legacy.client.settings.*;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.systems.modules.Modules;
import mathax.legacy.client.utils.render.PeekScreen;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.*;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.client.gui.screen.world.EditWorldScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.item.Items;

import java.io.File;

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
        super(Categories.Misc, Items.COMMAND_BLOCK, "discord-rpc", "Shows MatHax Legacy as your Discord status.");

        runInMainMenu = true;
    }

    @Override
    public void onActivate() {
        MatHaxLegacy.LOG.info(MatHaxLegacy.logPrefix + "Enabling Discord Rich Presence...");
        net.arikia.dev.drpc.DiscordRPC.discordInitialize(APP_ID, handlers, true, STEAM_ID);
        rpc.startTimestamp = System.currentTimeMillis() / 1000;
        rpc.details = Version.getStylized() + " | " + getUsername() + getPlayerHealth();
        rpc.state = getActivity();
        rpc.largeImageKey = "logo";
        rpc.largeImageText = "MatHax Legacy " + Version.getStylized();
        applySmallImage();
        rpc.smallImageText = getActivity();
        rpc.partyId = "ae488379-351d-4a4f-ad32-2b9b01c91657";
        rpc.joinSecret = "MTI4NzM0OjFpMmhuZToxMjMxMjM=";
        rpc.partySize = mc.getNetworkHandler() != null ? mc.getNetworkHandler().getPlayerList().size() : 1;
        rpc.partyMax = 1;
        net.arikia.dev.drpc.DiscordRPC.discordUpdatePresence(rpc);
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                net.arikia.dev.drpc.DiscordRPC.discordRunCallbacks();
                try {
                    rpc.details = Version.getStylized() + " | " + getUsername() + getPlayerHealth();
                    rpc.state = getActivity();
                    rpc.largeImageKey = "logo";
                    rpc.largeImageText = "MatHax Legacy " + Version.getStylized();
                    applySmallImage();
                    rpc.smallImageText = getActivity();
                    rpc.partySize = mc.getNetworkHandler() != null ? mc.getNetworkHandler().getPlayerList().size() : 1;
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
        MatHaxLegacy.LOG.info(MatHaxLegacy.logPrefix + "Discord Rich Presence enabled!");
    }

    @Override
    public void onDeactivate() {
        MatHaxLegacy.LOG.info(MatHaxLegacy.logPrefix + "Disabling Discord Rich Presence...");
        net.arikia.dev.drpc.DiscordRPC.discordClearPresence();
        net.arikia.dev.drpc.DiscordRPC.discordShutdown();
        MatHaxLegacy.LOG.info(MatHaxLegacy.logPrefix + "Discord Rich Presence disabled!");
    }

    // For shutdown hook
    public static void deactivate() {
        MatHaxLegacy.LOG.info(MatHaxLegacy.logPrefix + "Disabling Discord Rich Presence...");
        net.arikia.dev.drpc.DiscordRPC.discordClearPresence();
        net.arikia.dev.drpc.DiscordRPC.discordShutdown();
        MatHaxLegacy.LOG.info(MatHaxLegacy.logPrefix + "Discord Rich Presence disabled!");
    }

    private String getActivity() {
        if (mc == null || mc.getOverlay() instanceof SplashOverlay) return "Minecraft is loading...";
        else if (mc.currentScreen instanceof TitleScreen || mc.currentScreen instanceof net.minecraft.client.gui.screen.TitleScreen) return "In main menu";
        else if (mc.currentScreen instanceof MultiplayerScreen || mc.currentScreen instanceof ServerManagerScreen) return "In server selection";
        else if (mc.currentScreen instanceof ServerFinderScreen) return "Using server finder";
        else if (mc.currentScreen instanceof ServerCleanUpScreen) return "Using server cleanup";
        else if (mc.currentScreen instanceof ConnectScreen || mc.currentScreen instanceof DirectConnectScreen) return "Connecting to " + getNakedWorldActivity();
        else if (mc.currentScreen instanceof DisconnectedScreen) return "Got disconnected from " + getNakedWorldActivity();
        else if (mc.currentScreen instanceof GameMenuScreen) return "Game paused on " + getNakedWorldActivity();
        else if (mc.currentScreen instanceof PeekScreen) return "Using .peek on " + getNakedWorldActivity();
        else if (mc.currentScreen instanceof StatsScreen) return "Viewing stats";
        else if (mc.currentScreen instanceof ModulesScreen) return "In click gui";
        else if (mc.currentScreen instanceof ModuleScreen) return "Editing module " + ((ModuleScreen) mc.currentScreen).module.title;
        else if (mc.currentScreen instanceof OptionsScreen) return "Changing Minecraft settings";
        else if (mc.currentScreen instanceof AccessibilityOptionsScreen) return "Changing Minecraft accessibility settings";
        else if (mc.currentScreen instanceof ChatOptionsScreen) return "Changing Minecraft chat settings";
        else if (mc.currentScreen instanceof SoundOptionsScreen) return "Changing Minecraft sound settings";
        else if (mc.currentScreen instanceof LanguageOptionsScreen) return "Changing Minecraft language";
        else if (mc.currentScreen instanceof VideoOptionsScreen) return "Changing Minecraft video settings";
        else if (mc.currentScreen instanceof SkinOptionsScreen) return "Changing Minecraft skin settings";
        else if (mc.currentScreen instanceof PackScreen) return "Changing resourcepack";
        else if (mc.currentScreen instanceof ControlsOptionsScreen) return "Changing Minecraft keybinds";
        else if (mc.currentScreen instanceof NarratorOptionsScreen) return "Changing Narrator settings";
        else if (mc.currentScreen instanceof SelectWorldScreen) return "In world selection";
        else if (mc.currentScreen instanceof EditWorldScreen) return "Editing a world";
        else if (mc.currentScreen instanceof CreateWorldScreen || mc.currentScreen instanceof EditGameRulesScreen) return "Creating a new world";
        else if (mc.currentScreen instanceof LevelLoadingScreen) return "Loading a world";
        else if (mc.currentScreen instanceof SaveLevelScreen) return "Saving a world";
        else if (mc.currentScreen instanceof AddServerScreen) return "Adding a server";
        else if (mc.currentScreen instanceof BaritoneTab.BaritoneScreen) return "Configuring Baritone";
        else if (mc.currentScreen instanceof ConfigTab.ConfigScreen) return "Editing Config";
        else if (mc.currentScreen instanceof EnemiesTab.EnemiesScreen) return "Editing Enemies";
        else if (mc.currentScreen instanceof FriendsTab.FriendsScreen) return "Editing Friends";
        else if (mc.currentScreen instanceof GuiTab.GuiScreen) return "Editing GUI";
        else if (mc.currentScreen instanceof HudTab.HudScreen || mc.currentScreen instanceof HudElementScreen) return "Editing HUD";
        else if (mc.currentScreen instanceof MacrosTab.MacrosScreen) return "Configuring Macros";
        else if (mc.currentScreen instanceof MacrosTab.MacroEditorScreen) return "Configuring a Macro";
        else if (mc.currentScreen instanceof ProfilesTab.ProfilesScreen) return "Changing profiles";
        else if (mc.currentScreen instanceof ColorSettingScreen) return "Changing color";
        else if (mc.currentScreen instanceof AccountsScreen) return "In account manager";
        else if (mc.currentScreen instanceof AddAlteningAccountScreen) return "Adding Altening account";
        else if (mc.currentScreen instanceof AddCrackedAccountScreen) return "Adding cracked account";
        else if (mc.currentScreen instanceof AddPremiumAccountScreen) return "Adding premium account";
        else if (mc.currentScreen instanceof ProxiesScreen) return "Editing proxies";
        else if (mc.currentScreen instanceof CreditsScreen) return  "Reading credits";
        else if (mc.currentScreen instanceof RealmsScreen) return  "Browsing Realms";
        else if (mc.currentScreen instanceof WidgetScreen) return  "Browsing MatHax Legacy's GUI";
        else {
            if (mc.currentScreen != null) {
                String className = mc.currentScreen.getClass().getName();

                if (className.contains("me.jellysquid.mods.sodium.client")) rpc.state = "Changing Sodium video settings";
                else if (className.contains("com.terraformersmc.modmenu.gui")) rpc.state = "Viewing loaded mods";
            }

            if (mc.world != null) {
                return getWorldActivity();
            }
        }

        return "In " + mc.currentScreen.getTitle().getString();
    }

    private String getUsername() {
        NameProtect nameProtect = Modules.get().get(NameProtect.class);

        if (nameProtect.isActive()) return Modules.get().get(NameProtect.class).getName(mc.getSession().getUsername());
        else return mc.getSession().getUsername();
    }

    private String getPlayerHealth() {
        if (!Modules.get().get(DiscordRPC.class).playerHealth.get()) return "";
        if (mc.world == null) return "";
        if (mc.player == null) return "";
        if (mc.player.isDead()) return " | Dead";
        if (mc.player.isCreative()) return " | Creative Mode";
        if (mc.player.isSpectator()) return " | Spectator Mode";
        return " | " + Math.round(mc.player.getHealth() + mc.player.getAbsorptionAmount()) + " HP";
    }

    private String getWorldActivity() {

        // Multiplayer
        if (mc.getCurrentServerEntry() != null) {
            String name = mc.isConnectedToRealms() ? "realms" : mc.getCurrentServerEntry().address;

            if (Modules.get().get(DiscordRPC.class).serverVisibility.get()) {
                return "Playing on " + name;
            } else {
                return "Playing on a server";
            }
        }

        if ((mc.getServer()) == null) return "Could not get server/world";
        if (((MinecraftServerAccessor) mc.getServer()).getSession() == null) return "Could not get server/world";

        // Singleplayer
        if (mc.isInSingleplayer()) {
            File folder = ((MinecraftServerAccessor) mc.getServer()).getSession().getWorldDirectory(mc.world.getRegistryKey());
            if (folder.toPath().relativize(mc.runDirectory.toPath()).getNameCount() != 2) {
                folder = folder.getParentFile();
            }

            return "Playing singleplayer (" + folder.getName() + ")";
        }

        return "Could not get server/world";
    }

    private String getNakedWorldActivity() {

        // Multiplayer
        if (mc.getCurrentServerEntry() != null) {
            String name = mc.isConnectedToRealms() ? "realms" : mc.getCurrentServerEntry().address;

            if (Modules.get().get(DiscordRPC.class).serverVisibility.get()) {
                return name;
            } else {
                return "a server";
            }
        }

        if ((mc.getServer()) == null) return "Unknown";
        if (((MinecraftServerAccessor) mc.getServer()).getSession() == null) return "Unknown";

        // Singleplayer
        if (mc.isInSingleplayer()) {
            File folder = ((MinecraftServerAccessor) mc.getServer()).getSession().getWorldDirectory(mc.world.getRegistryKey());
            if (folder.toPath().relativize(mc.runDirectory.toPath()).getNameCount() != 2) {
                folder = folder.getParentFile();
            }

            return "singleplayer (" + folder.getName() + ")";
        }

        return "Unknown";
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
