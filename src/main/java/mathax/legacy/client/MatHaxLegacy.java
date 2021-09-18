package mathax.legacy.client;

import mathax.legacy.client.events.game.GameJoinedEvent;
import mathax.legacy.client.events.game.GameLeftEvent;
import mathax.legacy.client.events.mathax.CharTypedEvent;
import mathax.legacy.client.events.mathax.ClientInitialisedEvent;
import mathax.legacy.client.events.mathax.KeyEvent;
import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.gui.GuiThemes;
import mathax.legacy.client.gui.renderer.GuiRenderer;
import mathax.legacy.client.gui.tabs.Tabs;
import mathax.legacy.client.bus.EventBus;
import mathax.legacy.client.bus.EventHandler;
import mathax.legacy.client.bus.IEventBus;
import mathax.legacy.client.renderer.*;
import mathax.legacy.client.systems.Systems;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.misc.CapesModule;
import mathax.legacy.client.systems.modules.misc.DiscordRPC;
import mathax.legacy.client.systems.modules.render.Background;
import mathax.legacy.client.systems.modules.render.hud.HUD;
import mathax.legacy.client.utils.misc.FakeClientPlayer;
import mathax.legacy.client.utils.misc.Names;
import mathax.legacy.client.utils.misc.input.KeyAction;
import mathax.legacy.client.utils.misc.input.KeyBinds;
import mathax.legacy.client.utils.network.Capes;
import mathax.legacy.client.utils.network.MatHaxExecutor;
import mathax.legacy.client.utils.player.DamageUtils;
import mathax.legacy.client.utils.player.EChestMemory;
import mathax.legacy.client.utils.player.Rotations;
import mathax.legacy.client.utils.render.Outlines;
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
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.util.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.invoke.MethodHandles;

/*/                                                                              /*/
/*/ THIS CLIENT IS AN RECODED VERSION OF METEOR CLIENT BY MINEGAME159 & SEASNAIL /*/
/*/ https://meteorclient.com                                                     /*/
/*/ https://github.com/MeteorDevelopment/meteor-client                           /*/
/*/                                                                              /*/

public class MatHaxLegacy implements ClientModInitializer {
    public static MatHaxLegacy INSTANCE;
    public static final IEventBus EVENT_BUS = new EventBus();
    public static Screen screenToOpen;

    public static final File MCCONFIG_FOLDER = new File(net.fabricmc.loader.FabricLoader.INSTANCE.getConfigDirectory(), "/MatHax/Legacy");
    public static final File FOLDER = new File(FabricLoader.getInstance().getGameDir().toString(), "MatHax/Legacy");
    public static final File VERSION_FOLDER = new File(FOLDER + "/" + Version.getMinecraft());

    public final Color MATHAX_COLOR = new Color(230, 75, 100);
    public final int MATHAX_COLOR_INT = Color.fromRGBA(230, 75, 100, 255);
    public final Color MATHAX_BACKGROUND_COLOR = new Color(30, 30, 45);
    public final int MATHAX_BACKGROUND_COLOR_INT = Color.fromRGBA(30, 30, 45, 255);

    public static final Logger LOG = LogManager.getLogger();
    public static String logprefix = "[MatHax Legacy] ";

    public static String devUUID = "3e24ef27-e66d-45d2-bf4b-2c7ade68ff47";
    public static String devOfflineUUID = "7c73f844-73c3-3a7d-9978-004ba0a6436e";

    public static final String URL = "https://mathaxclient.xyz/";
    public static final String API_URL = "https://api.mathaxclient.xyz/";

    @Override
    public void onInitializeClient() {
        if (INSTANCE == null) {
            INSTANCE = this;
            return;
        }

        LOG.info(logprefix + "Initializing MatHax Legacy " + Version.getStylized() + "...");
        Utils.mc = MinecraftClient.getInstance();
        Utils.mc.execute(() -> titleIconManager(1));
        EVENT_BUS.registerLambdaFactory("mathax.legacy.client", (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));

        LOG.info(logprefix + "10% initialized!");
        Systems.addPreLoadTask(() -> {
            if (!Modules.get().getFile().exists()) {
                Modules.get().get(CapesModule.class).toggle(); // CAPES
                Modules.get().get(CapesModule.class).setVisible(false); // CAPES
                Modules.get().get(DiscordRPC.class).toggle(); // DISCORD RPC
                Modules.get().get(DiscordRPC.class).setVisible(false); // DISCORD RPC
                Modules.get().get(Background.class).toggle(); // BACKGROUND
                Modules.get().get(Background.class).setVisible(false); // BACKGROUND
                Modules.get().get(HUD.class).toggle(); // HUD
                Modules.get().get(HUD.class).setVisible(false); // HUD
                Modules.get().get(HUD.class).reset.run(); // DEFAULT HUD LOCATIONS AND TOGGLES
            }
        });
        Tabs.init();
        GL.init();

        LOG.info(logprefix + "20% initialized!");
        Shaders.init();
        Renderer2D.init();
        Outlines.init();

        LOG.info(logprefix + "30% initialized!");
        RainbowColors.init();
        MatHaxExecutor.init();

        LOG.info(logprefix + "40% initialized!");
        BlockIterator.init();
        EChestMemory.init();
        Rotations.init();

        LOG.info(logprefix + "50% initialized!");
        Names.init();
        FakeClientPlayer.init();
        PostProcessRenderer.init();

        LOG.info(logprefix + "60% initialized!");
        GuiThemes.init();
        Fonts.init();
        DamageUtils.init();
        BlockUtils.init();

        LOG.info(logprefix + "70% initialized!");
        Modules.REGISTERING_CATEGORIES = true;
        Categories.register();
        Modules.REGISTERING_CATEGORIES = false;
        Systems.init();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DiscordRPC.deactivate();
            Systems.save();
            GuiThemes.save();
        }));

        LOG.info(logprefix + "80% initialized!");
        Fonts.load();
        GuiRenderer.init();
        GuiThemes.postInit();
        Utils.mc.execute(() -> titleIconManager(2));

        LOG.info(logprefix + "90% initialized!");
        Capes.init();
        EVENT_BUS.subscribe(this);
        EVENT_BUS.post(new ClientInitialisedEvent()); // TODO: This is there just for compatibility
        Modules.get().sortModules();
        Systems.load();

        LOG.info(logprefix + "100% initialized!");
        Utils.mc.execute(() -> titleIconManager(3));

        LOG.info(logprefix + "MatHax Legacy " + Version.getStylized() + " initialized!");
    }

    public void titleIconManager(Integer process) {
        final Window window = MinecraftClient.getInstance().getWindow();
        switch (process) {
            case 1:
                window.setIcon(getClass().getResourceAsStream("/assets/mathaxlegacy/textures/icons/window/icon64.png"), getClass().getResourceAsStream("/assets/mathaxlegacy/textures/icons/window/icon128.png"));
                window.setTitle("MatHax Legacy " + Version.getStylized() + " - " + MinecraftClient.getInstance().getVersionType() + " " + Version.getMinecraft() + " is being loaded...");
            case 2:
                window.setTitle("MatHax Legacy " + Version.getStylized() + " - " + MinecraftClient.getInstance().getVersionType() + " " + Version.getMinecraft() + " loaded!");
            case 3:
                window.setTitle("MatHax Legacy " + Version.getStylized() + " - " + MinecraftClient.getInstance().getVersionType() + " " + Version.getMinecraft());
        }
    }

    private void openClickGUI() {
        Tabs.get().get(0).openScreen(GuiThemes.get());
    }

    @EventHandler
    private void onGameJoined(GameJoinedEvent event) {
        Version.checkedForLatest = false;
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        Version.checkedForLatest = false;
        Systems.save();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        Capes.tick();

        if (screenToOpen != null && Utils.mc.currentScreen == null) {
            Utils.mc.setScreen(screenToOpen);
            screenToOpen = null;
        }

        if (Utils.canUpdate()) {
            Utils.mc.player.getActiveStatusEffects().values().removeIf(statusEffectInstance -> statusEffectInstance.getDuration() <= 0);
        }
    }

    @EventHandler
    private void onKey(KeyEvent event) {
        // Click GUI
        if (event.action == KeyAction.Press && KeyBinds.OPEN_CLICK_GUI.matchesKey(event.key, 0)) {
            if (Utils.mc.getOverlay() instanceof SplashOverlay) return;
            if (!Utils.canUpdate() && Utils.isWhitelistedScreen() || Utils.mc.currentScreen == null) openClickGUI();
        }
    }

    @EventHandler
    private void onCharTyped(CharTypedEvent event) {
        if (Utils.mc.currentScreen != null) return;
        if (!Config.get().openChatOnPrefix) return;

        if (event.c == Config.get().prefix.charAt(0)) {
            Utils.mc.setScreen(new ChatScreen(Config.get().prefix));
            event.cancel();
        }
    }
}
