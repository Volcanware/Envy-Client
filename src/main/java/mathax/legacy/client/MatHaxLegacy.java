package mathax.legacy.client;

import mathax.legacy.client.events.mathaxlegacy.CharTypedEvent;
import mathax.legacy.client.events.mathaxlegacy.KeyEvent;
import mathax.legacy.client.events.mathaxlegacy.MouseButtonEvent;
import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.gui.GuiThemes;
import mathax.legacy.client.gui.renderer.GuiRenderer;
import mathax.legacy.client.gui.tabs.Tabs;
import mathax.legacy.client.eventbus.EventBus;
import mathax.legacy.client.eventbus.EventHandler;
import mathax.legacy.client.eventbus.IEventBus;
import mathax.legacy.client.music.Music;
import mathax.legacy.client.renderer.*;
import mathax.legacy.client.renderer.text.Fonts;
import mathax.legacy.client.systems.Systems;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.client.ClientSpoof;
import mathax.legacy.client.systems.modules.combat.*;
import mathax.legacy.client.systems.modules.client.CapesModule;
import mathax.legacy.client.systems.modules.client.DiscordRPC;
import mathax.legacy.client.systems.modules.client.MiddleClickFriend;
import mathax.legacy.client.systems.modules.render.Background;
import mathax.legacy.client.systems.modules.render.Zoom;
import mathax.legacy.client.systems.modules.render.hud.HUD;
import mathax.legacy.client.utils.Version;
import mathax.legacy.client.utils.misc.FakeClientPlayer;
import mathax.legacy.client.utils.misc.KeyBind;
import mathax.legacy.client.utils.misc.Names;
import mathax.legacy.client.utils.misc.input.KeyAction;
import mathax.legacy.client.utils.misc.input.KeyBinds;
import mathax.legacy.client.utils.network.MatHaxExecutor;
import mathax.legacy.client.utils.player.DamageUtils;
import mathax.legacy.client.utils.player.EChestMemory;
import mathax.legacy.client.utils.player.Rotations;
import mathax.legacy.client.utils.render.EntityShaders;
import mathax.legacy.client.utils.render.color.Color;
import mathax.legacy.client.utils.render.color.RainbowColors;
import mathax.legacy.client.utils.world.BlockIterator;
import mathax.legacy.client.utils.world.BlockUtils;
import mathax.legacy.client.systems.config.Config;
import mathax.legacy.client.systems.modules.Modules;
import mathax.legacy.client.utils.Utils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.List;

/*/------------------------------------------------------------------/*/
/*/ THIS CLIENT IS A FORK OF METEOR CLIENT BY MINEGAME159 & SEASNAIL /*/
/*/ https://meteorclient.com                                         /*/
/*/ https://github.com/MeteorDevelopment/meteor-client               /*/
/*/------------------------------------------------------------------/*/
/*/ Music player used from Motor Tunez made by JFronny               /*/
/*/ https://github.com/JFronny/MotorTunez                            /*/
/*/------------------------------------------------------------------/*/

public class MatHaxLegacy implements ClientModInitializer {
    public static MinecraftClient mc;
    public static MatHaxLegacy INSTANCE;
    public static final IEventBus EVENT_BUS = new EventBus();

    public static final File GAME_FOLDER = new File(FabricLoader.getInstance().getGameDir().toString());
    public static final File FOLDER = new File(GAME_FOLDER, "MatHax/Legacy");
    public static final File VERSION_FOLDER = new File(FOLDER + "/" + Version.getMinecraft());
    public static final File MUSIC_FOLDER = new File(FOLDER + "/Music");

    public final Color MATHAX_COLOR = new Color(230, 75, 100, 255);
    public final int MATHAX_COLOR_INT = Color.fromRGBA(230, 75, 100, 255);
    public final Color MATHAX_BACKGROUND_COLOR = new Color(30, 30, 45, 255);
    public final int MATHAX_BACKGROUND_COLOR_INT = Color.fromRGBA(30, 30, 45, 255);

    public static final Logger LOG = LogManager.getLogger();
    public static String logPrefix = "[MatHax Legacy] ";

    public static final String URL = "https://mathaxclient.xyz/";
    public static final String API_URL = "https://api.mathaxclient.xyz/";

    public static List<String> getDeveloperUUIDs() {
        return Arrays.asList(

            // MATEJKO06
            "3e24ef27e66d45d2bf4b2c7ade68ff47",
            "7c73f84473c33a7d9978004ba0a6436e"

        );
    }

    public static List<String> getSplashes() {
        return Arrays.asList(

            // SPLASHES
            Formatting.RED + "MatHax on top!",
            Formatting.GRAY + "Matejko06" + Formatting.RED + " based god",
            Formatting.RED + "MatHaxClient.xyz",
            Formatting.RED + "MatHaxClient.xyz/Discord",
            Formatting.RED + Version.getStylized(),

            // MEME SPLASHES
            Formatting.YELLOW + "cope",
            Formatting.YELLOW + "I am funny -HiIAmFunny",
            Formatting.YELLOW + "IntelliJ IDEa",
            Formatting.YELLOW + "I <3 nns",
            Formatting.YELLOW + "haha 69",
            Formatting.YELLOW + "420 XDDDDDD",
            Formatting.YELLOW + "ayy",
            Formatting.YELLOW + "too ez",
            Formatting.YELLOW + "owned",
            Formatting.YELLOW + "your mom :joy:",
            Formatting.YELLOW + "BOOM BOOM BOOM!",
            Formatting.YELLOW + "I <3 forks",
            Formatting.YELLOW + "based",
            Formatting.YELLOW + "Pog",
            Formatting.YELLOW + "Big Rat on top!",
            Formatting.YELLOW + "bigrat.monster",

            // PERSONALIZED
            Formatting.YELLOW + "You're cool, " + Formatting.GRAY + MinecraftClient.getInstance().getSession().getUsername(),
            Formatting.YELLOW + "Owning with " + Formatting.GRAY + MinecraftClient.getInstance().getSession().getUsername()

        );
    }

    @Override
    public void onInitializeClient() {
        // Instance
        if (INSTANCE == null) {
            INSTANCE = this;
            return;
        }

        // Log
        LOG.info(logPrefix + "Initializing MatHax Legacy " + Version.getStylized() + "...");

        // Global Minecraft client accessor
        mc = MinecraftClient.getInstance();

        // Icon
        mc.getWindow().setIcon(getClass().getResourceAsStream("/assets/mathaxlegacy/textures/icons/icon64.png"), getClass().getResourceAsStream("/assets/mathaxlegacy/textures/icons/icon128.png"));

        // Title
        mc.getWindow().setTitle("MatHax Legacy " + Version.getStylized() + " - " + MinecraftClient.getInstance().getVersionType() + " " + Version.getMinecraft() + " is being loaded...");

        // Register event handlers
        EVENT_BUS.registerLambdaFactory("mathax.legacy.client", (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));

        // Pre-load
        Systems.addPreLoadTask(() -> {
            if (!Modules.get().getFile().exists()) {
                // ACTIVATE
                Modules.get().get(CapesModule.class).forceToggle(true); // CAPES
                Modules.get().get(DiscordRPC.class).forceToggle(true); // DISCORD RPC
                Modules.get().get(Background.class).forceToggle(true); // BACKGROUND
                Modules.get().get(MiddleClickFriend.class).forceToggle(true); // MIDDLE CLICK FRIEND
                Modules.get().get(HUD.class).forceToggle(true); // HUD

                // VISIBILITY
                Modules.get().get(CapesModule.class).setVisible(false); // CAPES
                Modules.get().get(DiscordRPC.class).setVisible(false); // DISCORD RPC
                Modules.get().get(Background.class).setVisible(false); // BACKGROUND
                Modules.get().get(MiddleClickFriend.class).setVisible(false); // MIDDLE CLICK FRIEND
                Modules.get().get(Zoom.class).setVisible(false); // ZOOM
                Modules.get().get(HUD.class).setVisible(false); // HUD

                // KEYBINDS
                Modules.get().get(Zoom.class).keybind.set(KeyBind.fromKey(GLFW.GLFW_KEY_C)); // ZOOM

                // KEYBIND OPTIONS
                Modules.get().get(Zoom.class).toggleOnBindRelease = true; // ZOOM

                // TOASTS
                Modules.get().get(AnchorAura.class).setToggleToast(true); // ANCHOR AURA
                Modules.get().get(BedAura.class).setToggleToast(true); // BED AURA
                Modules.get().get(CEVBreaker.class).setToggleToast(true); // CEV BREAKER
                Modules.get().get(CrystalAura.class).setToggleToast(true); // CRYSTAL AURA
                Modules.get().get(KillAura.class).setToggleToast(true); // KILL AURA

                // MESSAGES
                Modules.get().get(Zoom.class).setToggleMessage(false); // ZOOM

                // RESET HUD LOCATIONS
                Modules.get().get(HUD.class).reset.run(); // HUD
            }
        });

        // Pre init
        Utils.init();
        GL.init();
        Shaders.init();
        Renderer2D.init();
        EntityShaders.initOutlines();
        MatHaxExecutor.init();
        RainbowColors.init();
        BlockIterator.init();
        EChestMemory.init();
        Rotations.init();
        Names.init();
        FakeClientPlayer.init();
        PostProcessRenderer.init();
        Tabs.init();
        GuiThemes.init();
        Fonts.init();
        DamageUtils.init();
        BlockUtils.init();
        Music.init();

        // Register module categories
        Categories.init();

        // Load systems
        Systems.init();

        // Event bus
        EVENT_BUS.subscribe(this);

        // Sorting modules
        Modules.get().sortModules();

        // Load saves
        Systems.load();

        // Post init
        Fonts.load();
        GuiRenderer.init();
        GuiThemes.postInit();

        // Loaded window title
        mc.getWindow().setTitle("MatHax Legacy " + Version.getStylized() + " - " + MinecraftClient.getInstance().getVersionType() + " " + Version.getMinecraft() + " loaded!");

        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            net.arikia.dev.drpc.DiscordRPC.discordClearPresence();
            net.arikia.dev.drpc.DiscordRPC.discordShutdown();
            Systems.save();
            GuiThemes.save();
        }));

        ClientSpoof cs = Modules.get().get(ClientSpoof.class);
        if (cs.isActive() && cs.changeWindowIcon()) cs.setMeteorIcon();
        else mc.getWindow().setIcon(getClass().getResourceAsStream("/assets/mathaxlegacy/textures/icons/icon64.png"), getClass().getResourceAsStream("/assets/mathaxlegacy/textures/icons/icon128.png"));
        if (cs.isActive() && cs.changeWindowTitle()) cs.setMeteorTitle();
        else mc.getWindow().setTitle("MatHax Legacy " + Version.getStylized() + " - " + MinecraftClient.getInstance().getVersionType() + " " + Version.getMinecraft());

        // Log
        LOG.info(logPrefix + "MatHax Legacy " + Version.getStylized() + " initialized!");
    }

    // Music Volume

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (Music.player == null) return;
        if (Music.player.getVolume() != Config.get().musicVolume) Music.player.setVolume(Config.get().musicVolume);
    }

    // Developer

    public static boolean isDeveloper(String uuid) {
        uuid = uuid.replace("-", "");
        return getDeveloperUUIDs().contains(uuid);
    }

    // Click GUI keys

    @EventHandler
    private void onKeyGUI(KeyEvent event) {
        if (event.action == KeyAction.Press && KeyBinds.OPEN_CLICK_GUI.matchesKey(event.key, 0)) {
            if (mc.getOverlay() instanceof SplashOverlay) return;
            if (Utils.canOpenClickGUI()) openClickGUI();
        }
    }

    @EventHandler
    private void onMouseButtonGUI(MouseButtonEvent event) {
        if (event.action == KeyAction.Press && event.button != GLFW.GLFW_MOUSE_BUTTON_LEFT && KeyBinds.OPEN_CLICK_GUI.matchesMouse(event.button) && Utils.canOpenClickGUI()) openClickGUI();
    }

    // Click GUI

    private void openClickGUI() {
        Tabs.get().get(0).openScreen(GuiThemes.get());
    }

    // Console

    @EventHandler
    private void onCharTyped(CharTypedEvent event) {
        if (mc.currentScreen != null || !Config.get().prefixOpensConsole || Config.get().prefix.isBlank()) return;

        if (event.c == Config.get().prefix.charAt(0)) {
            mc.setScreen(new ChatScreen(Config.get().prefix));
            event.cancel();
        }
    }
}
