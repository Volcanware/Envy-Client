package mathax.legacy.client.systems.modules.misc;

import mathax.legacy.client.MatHaxLegacy;
import mathax.legacy.client.gui.screens.music.MusicScreen;
import mathax.legacy.client.gui.screens.music.PlaylistViewScreen;
import mathax.legacy.client.gui.screens.music.PlaylistsScreen;
import mathax.legacy.client.utils.Version;
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
import mathax.legacy.client.gui.tabs.builtin.*;
import mathax.legacy.client.mixin.MinecraftServerAccessor;
import mathax.legacy.client.settings.*;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.systems.modules.Modules;
import mathax.legacy.client.utils.misc.LastServerInfo;
import mathax.legacy.client.utils.render.PeekScreen;
import mathax.legacy.client.utils.render.prompts.OkPrompt;
import mathax.legacy.client.utils.render.prompts.YesNoPrompt;
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

    public static boolean mcFirstLoad = true;

    private static int delay = 0;
    private static int number = 1;

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

    private String getActivity() {
        if (mc.getOverlay() instanceof SplashOverlay) return "Something is loading...";
        else if (mc.currentScreen instanceof TitleScreen || mc.currentScreen instanceof net.minecraft.client.gui.screen.TitleScreen) return "In main menu";
        else if (mc.currentScreen instanceof MultiplayerScreen || mc.currentScreen instanceof ServerManagerScreen) return "In server selection";
        else if (mc.currentScreen instanceof DirectConnectScreen) return "Using direct connect";
        else if (mc.currentScreen instanceof ServerFinderScreen) return "Using server finder";
        else if (mc.currentScreen instanceof ServerCleanUpScreen) return "Using server cleanup";
        else if (mc.currentScreen instanceof ConnectScreen) return "Connecting to " + getNakedWorldActivity();
        else if (mc.currentScreen instanceof DisconnectedScreen) return "Got disconnected from " + getNakedWorldActivity();
        else if (mc.currentScreen instanceof GameMenuScreen) return "Game paused on " + getNakedWorldActivity();
        else if (mc.currentScreen instanceof PeekScreen) return "Using .peek on " + getNakedWorldActivity();
        else if (mc.currentScreen instanceof ModulesScreen) {
            if (mc.world != null && serverVisibility.get()) return "In Click GUI (" + getNakedWorldActivity2() + ")";
            else return "In Click GUI";
        } else if (mc.currentScreen instanceof ModuleScreen) {
            if (mc.world != null) return "Editing module " + ((ModuleScreen) mc.currentScreen).module.title + " (" + getNakedWorldActivity2() + ")";
            else return "Editing module " + ((ModuleScreen) mc.currentScreen).module.title;
        } else if (mc.currentScreen instanceof PackScreen) return "Changing resourcepack";
        else if (mc.currentScreen instanceof OptionsScreen) return "Changing Minecraft settings";
        else if (mc.currentScreen instanceof AccessibilityOptionsScreen) return "Changing Minecraft accessibility settings";
        else if (mc.currentScreen instanceof ChatOptionsScreen) return "Changing Minecraft chat settings";
        else if (mc.currentScreen instanceof SoundOptionsScreen) return "Changing Minecraft sound settings";
        else if (mc.currentScreen instanceof LanguageOptionsScreen) return "Changing Minecraft language";
        else if (mc.currentScreen instanceof VideoOptionsScreen) return "Changing Minecraft video settings";
        else if (mc.currentScreen instanceof SkinOptionsScreen) return "Changing Minecraft skin settings";
        else if (mc.currentScreen instanceof ControlsOptionsScreen) return "Changing Minecraft keybinds";
        else if (mc.currentScreen instanceof NarratorOptionsScreen) return "Changing Narrator settings";
        else if (mc.currentScreen instanceof StatsScreen) return "Viewing stats";
        else if (mc.currentScreen instanceof SelectWorldScreen) return "In world selection";
        else if (mc.currentScreen instanceof EditWorldScreen) return "Editing a world";
        else if (mc.currentScreen instanceof CreateWorldScreen || mc.currentScreen instanceof EditGameRulesScreen) return "Creating a new world";
        else if (mc.currentScreen instanceof LevelLoadingScreen || mc.currentScreen instanceof SaveLevelScreen) return "Loading/Saving a world";
        else if (mc.currentScreen instanceof AddServerScreen) return "Adding/Editing a server";
        else if (mc.currentScreen instanceof BaritoneTab.BaritoneScreen) return "Configuring Baritone";
        else if (mc.currentScreen instanceof ConfigTab.ConfigScreen) return "Editing config";
        else if (mc.currentScreen instanceof EnemiesTab.EnemiesScreen) return "Editing enemies";
        else if (mc.currentScreen instanceof FriendsTab.FriendsScreen) return "Editing friends";
        else if (mc.currentScreen instanceof GuiTab.GuiScreen) return "Editing GUI";
        else if (mc.currentScreen instanceof HudTab.HudScreen || mc.currentScreen instanceof HudElementScreen) return "Editing HUD";
        else if (mc.currentScreen instanceof MacrosTab.MacrosScreen) return "Configuring macros";
        else if (mc.currentScreen instanceof MacrosTab.MacroEditorScreen) return "Configuring a macro";
        else if (mc.currentScreen instanceof ProfilesTab.ProfilesScreen) return "Changing profiles";
        else if (mc.currentScreen instanceof MusicScreen) return "Configuring music";
        else if (mc.currentScreen instanceof PlaylistsScreen) return "Viewing playlists";
        else if (mc.currentScreen instanceof PlaylistViewScreen) {
            if (((PlaylistViewScreen) mc.currentScreen).getTitleString().contains("Search")) return "Searching for a song";
            else return "Viewing a playlist";
        } else if (mc.currentScreen instanceof AccountsScreen) return "In account manager";
        else if (mc.currentScreen instanceof AddCrackedAccountScreen) return "Adding cracked account";
        else if (mc.currentScreen instanceof AddPremiumAccountScreen) return "Adding premium account";
        else if (mc.currentScreen instanceof AddAlteningAccountScreen) return "Adding The Altening account";
        else if (mc.currentScreen instanceof ProxiesScreen) return "Editing proxies";
        else if (mc.currentScreen instanceof CreditsScreen) return "Reading credits";
        else if (mc.currentScreen instanceof RealmsScreen) return "Browsing Realms";
        else if (mc.currentScreen instanceof YesNoPrompt.PromptScreen) return "Viewing a prompt";
        else if (mc.currentScreen instanceof OkPrompt.PromptScreen) return "Viewing a prompt";
        else if (mc.currentScreen instanceof ProgressScreen) return "Loading something...";
        else if (mc.world != null) return getWorldActivity();

        String className = mc.currentScreen.getClass().getName();
        if (className.contains("me.jellysquid.mods.sodium.client")) return "Changing Sodium video settings";
        else if (className.contains("net.coderbot.iris.gui.screen")) return "Changing Iris shaderpack";
        else if (className.contains("com.terraformersmc.modmenu.gui")) return "Viewing loaded mods";
        else if (className.contains("com.viaversion.fabric.mc117.gui")) return "Changing Minecraft version";

        return "Unknown Activity";
    }

    private String getUsername() {
        if (Modules.get().isActive(NameProtect.class)) return Modules.get().get(NameProtect.class).getName(mc.getSession().getUsername());
        else return mc.getSession().getUsername();
    }

    private String getPlayerHealth() {
        if (!Modules.get().get(DiscordRPC.class).playerHealth.get() || mc.world == null || mc.player == null) return "";
        else if (mc.player.isDead()) return " | Dead";
        else if (mc.player.isCreative()) return " | Creative Mode";
        else if (mc.player.isSpectator()) return " | Spectator Mode";
        return " | " + Math.round(mc.player.getHealth() + mc.player.getAbsorptionAmount()) + " HP";
    }

    private String getWorldActivity() {

        // Multiplayer
        if (mc.getCurrentServerEntry() != null) {
            String name = mc.isConnectedToRealms() ? "realms" : mc.getCurrentServerEntry().address;

            if (Modules.get().get(DiscordRPC.class).serverVisibility.get()) return "Playing on " + name;
            else return "Playing on a server";
        }

        if ((mc.getServer()) == null) return "Could not get server/world";
        if (((MinecraftServerAccessor) mc.getServer()).getSession() == null) return "Could not get server/world";

        // Singleplayer
        if (mc.isInSingleplayer()) {
            File folder = ((MinecraftServerAccessor) mc.getServer()).getSession().getWorldDirectory(mc.world.getRegistryKey());
            if (folder.toPath().relativize(mc.runDirectory.toPath()).getNameCount() != 2) folder = folder.getParentFile();

            return "Playing singleplayer (" + folder.getName() + ")";
        }

        return "Could not get server/world";
    }

    private String getNakedWorldActivity() {

        // Disconnected etc...
        if (!mc.isInSingleplayer() && mc.getCurrentServerEntry() != null && LastServerInfo.getLastServer() != null) {
            if (Modules.get().get(DiscordRPC.class).serverVisibility.get()) return LastServerInfo.getLastServer().address;
            else return "a server";
        }

        // Multiplayer
        if (mc.getCurrentServerEntry() != null) {
            String name = mc.isConnectedToRealms() ? "realms" : mc.getCurrentServerEntry().address;

            if (Modules.get().get(DiscordRPC.class).serverVisibility.get()) return name;
            else return "a server";
        }

        if ((mc.getServer()) == null) return "Unknown";
        if (((MinecraftServerAccessor) mc.getServer()).getSession() == null) return "Unknown";

        // Singleplayer
        if (mc.isInSingleplayer()) {
            File folder = ((MinecraftServerAccessor) mc.getServer()).getSession().getWorldDirectory(mc.world.getRegistryKey());
            if (folder.toPath().relativize(mc.runDirectory.toPath()).getNameCount() != 2) folder = folder.getParentFile();

            return "singleplayer (" + folder.getName() + ")";
        }

        return "Unknown";
    }

    private String getNakedWorldActivity2() {

        // Disconnected etc...
        if (!mc.isInSingleplayer() && mc.getCurrentServerEntry() != null && LastServerInfo.getLastServer() != null) {
            if (Modules.get().get(DiscordRPC.class).serverVisibility.get()) return LastServerInfo.getLastServer().address;
            else return "A server";
        }

        // Multiplayer
        if (mc.getCurrentServerEntry() != null) {
            String name = mc.isConnectedToRealms() ? "Realms" : mc.getCurrentServerEntry().address;

            if (Modules.get().get(DiscordRPC.class).serverVisibility.get()) return name;
            else return "A server";
        }

        if ((mc.getServer()) == null) return "Unknown";
        if (((MinecraftServerAccessor) mc.getServer()).getSession() == null) return "Unknown";

        // Singleplayer
        if (mc.isInSingleplayer()) {
            File folder = ((MinecraftServerAccessor) mc.getServer()).getSession().getWorldDirectory(mc.world.getRegistryKey());
            if (folder.toPath().relativize(mc.runDirectory.toPath()).getNameCount() != 2) folder = folder.getParentFile();

            return "Singleplayer (" + folder.getName() + ")";
        }

        return "Unknown";
    }

    private static void applySmallImage() {
        if (delay == 5) {
            if (number == 16) number = 1;
            if (Modules.get().get(DiscordRPC.class).smallImageMode.get() == SmallImageMode.Dogs) rpc.smallImageKey = "dog-" + number;
            else rpc.smallImageKey = "cat-" + number;
            ++number;
            delay = 0;
        } else ++delay;
    }

    public enum SmallImageMode {
        Cats,
        Dogs
    }
}
