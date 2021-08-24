package mathax.client.legacy;

import mathax.client.legacy.discord.MatHaxDiscordRPC;
import mathax.client.legacy.events.game.GameJoinedEvent;
import mathax.client.legacy.events.game.GameLeftEvent;
import mathax.client.legacy.events.game.ReceiveMessageEvent;
import mathax.client.legacy.events.mathax.CharTypedEvent;
import mathax.client.legacy.events.mathax.ClientInitialisedEvent;
import mathax.client.legacy.events.mathax.KeyEvent;
import mathax.client.legacy.events.world.TickEvent;
import mathax.client.legacy.gui.GuiThemes;
import mathax.client.legacy.gui.renderer.GuiRenderer;
import mathax.client.legacy.gui.tabs.Tabs;
import mathax.client.legacy.bus.EventBus;
import mathax.client.legacy.bus.EventHandler;
import mathax.client.legacy.bus.IEventBus;
import mathax.client.legacy.gui.tabs.builtin.DiscordPresenceTab;
import mathax.client.legacy.renderer.*;
import mathax.client.legacy.systems.Systems;
import mathax.client.legacy.systems.modules.Categories;
import mathax.client.legacy.systems.modules.render.Background;
import mathax.client.legacy.systems.modules.render.hud.HUD;
import mathax.client.legacy.utils.misc.FakeClientPlayer;
import mathax.client.legacy.utils.misc.Names;
import mathax.client.legacy.utils.misc.input.KeyAction;
import mathax.client.legacy.utils.misc.input.KeyBinds;
import mathax.client.legacy.utils.network.Capes;
import mathax.client.legacy.utils.network.MatHaxExecutor;
import mathax.client.legacy.utils.player.DamageUtils;
import mathax.client.legacy.utils.player.EChestMemory;
import mathax.client.legacy.utils.player.Rotations;
import mathax.client.legacy.utils.render.Outlines;
import mathax.client.legacy.utils.render.color.Color;
import mathax.client.legacy.utils.render.color.RainbowColors;
import mathax.client.legacy.utils.world.BlockIterator;
import mathax.client.legacy.utils.world.BlockUtils;
import mathax.client.legacy.systems.config.Config;
import mathax.client.legacy.systems.modules.Modules;
import mathax.client.legacy.utils.Utils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import static mathax.client.legacy.utils.Utils.mc;

public class MatHaxClientLegacy implements ClientModInitializer {
    public static MatHaxClientLegacy INSTANCE;
    public static final IEventBus EVENT_BUS = new EventBus();

    public static final File MCCONFIG_FOLDER = new File(net.fabricmc.loader.FabricLoader.INSTANCE.getConfigDirectory(), "/MatHax/Legacy");
    public static final File FOLDER = new File(FabricLoader.getInstance().getGameDir().toString(), "MatHax/Legacy");
    public static final File VERSION_FOLDER = new File(FOLDER + "/" + getMinecraftVersion());

    public final int MATHAX_COLOR = Color.fromRGBA(230, 75, 100, 255);

    public static final Logger LOG = LogManager.getLogger();

    public static Screen screenToOpen;

    public static final String URL = "https://api.mathaxclient.xyz/";

    public static String logprefix = "[MatHax Legacy] ";

    static ModMetadata metadata = FabricLoader.getInstance().getModContainer("mathaxlegacy").get().getMetadata();

    public static String versionNumber = metadata.getVersion().getFriendlyString();
    public static Integer devBuildNumber = 1;

    public static String devBuild() {
        if (devBuildNumber == 0) {
            return "";
        } else {
            return " Dev-" + devBuildNumber;
        }
    }

    public static String clientVersionWithV = "v" + versionNumber + devBuild();
    public static String discordVersion = "v" + versionNumber + devBuild();

    @Override
    public void onInitializeClient() {
        if (INSTANCE == null) {
            INSTANCE = this;
            return;
        }

        LOG.info(logprefix + "Initializing MatHax Client Legacy " + clientVersionWithV + "...");
        Utils.mc = MinecraftClient.getInstance();
        mc.execute(this::titleLoading);
        mc.execute(this::updateImage);
        EVENT_BUS.registerLambdaFactory("mathax.client.legacy", (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
        List<MatHaxClientLegacyPlus> addons = new ArrayList<>();
        for (EntrypointContainer<MatHaxClientLegacyPlus> entrypoint : FabricLoader.getInstance().getEntrypointContainers("mathaxlegacy", MatHaxClientLegacyPlus.class)) {
            addons.add(entrypoint.getEntrypoint());
        }

        LOG.info(logprefix + "10% initialized!");
        Systems.addPreLoadTask(() -> {
            if (!Modules.get().getFile().exists()) {
                Modules.get().get(Background.class).toggle(false);
                Modules.get().get(HUD.class).toggle(false);
                Modules.get().get(mathax.client.legacy.systems.modules.fun.Capes.class).toggle(false);
            }
        });

        LOG.info(logprefix + "20% initialized!");
        Tabs.init();
        MatHaxDiscordRPC.init();
        GL.init();
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
        // Register categories
        Modules.REGISTERING_CATEGORIES = true;
        Categories.register();
        addons.forEach(MatHaxClientLegacyPlus::onRegisterCategories);
        Modules.REGISTERING_CATEGORIES = false;

        LOG.info(logprefix + "80% initialized!");
        Systems.init();
        mc.execute(this::titleLoaded);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            MatHaxDiscordRPC.disable();
            Systems.save();
            GuiThemes.save();
        }));

        LOG.info(logprefix + "90% initialized!");
        Fonts.load();
        GuiRenderer.init();
        GuiThemes.postInit();
        Capes.init();
        EVENT_BUS.subscribe(this);
        EVENT_BUS.post(new ClientInitialisedEvent()); // TODO: This is there just for compatibility
        addons.forEach(MatHaxClientLegacyPlus::onInitialize);
        Modules.get().sortModules();
        Systems.load();

        LOG.info(logprefix + "100% initialized!");
        mc.execute(this::titleFinal);

        LOG.info(logprefix + "MatHax Client Legacy " + clientVersionWithV + " initialized!");
    }

    public void updateImage() {
        final Window window = MinecraftClient.getInstance().getWindow();
        window.setIcon(getClass().getResourceAsStream("/assets/mathaxlegacy/icons/window/icon64.png"), getClass().getResourceAsStream("/assets/mathaxlegacy/icons/window/icon128.png"));
    }

    public void titleLoading() {
        final Window window = MinecraftClient.getInstance().getWindow();
        window.setTitle("MatHax Client Legacy " + clientVersionWithV + " - " + MinecraftClient.getInstance().getVersionType() + " " + getMinecraftVersion() + " is being loaded...");
    }

    public void titleLoaded() {
        final Window window = MinecraftClient.getInstance().getWindow();
        window.setTitle("MatHax Client Legacy " + clientVersionWithV + " - " + MinecraftClient.getInstance().getVersionType() + " " + getMinecraftVersion() + " loaded!");
    }

    public void titleFinal() {
        final Window window = MinecraftClient.getInstance().getWindow();
        window.setTitle("MatHax Client Legacy " + clientVersionWithV + " - " + MinecraftClient.getInstance().getVersionType() + " " + getMinecraftVersion());
    }

    private void openClickGui() {
        Tabs.get().get(0).openScreen(GuiThemes.get());
    }

    static String getMinecraftVersion(){
        return SharedConstants.getGameVersion().getName();
    }

    @EventHandler
    private void onGameJoined(GameJoinedEvent event) {
        Utils.didntCheckForLatestVersion = true;
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        Utils.didntCheckForLatestVersion = true;
        Systems.save();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        Capes.tick();

        if (screenToOpen != null && mc.currentScreen == null) {
            mc.openScreen(screenToOpen);
            screenToOpen = null;
        }

        if (Utils.canUpdate()) {
            mc.player.getActiveStatusEffects().values().removeIf(statusEffectInstance -> statusEffectInstance.getDuration() <= 0);
        }
    }

    private static String queuePos = "";

    @EventHandler
    private static void onMessageRecieve(ReceiveMessageEvent event) {
        if (DiscordPresenceTab.queuePosition.get()) {
            if (event.message.getString().contains("[MatHax Legacy] ")) return;
            String messageString = event.message.getString();
            if (messageString.contains("Position in queue: ")) {
                String queue = messageString.replace("Position in queue: ", "");
                queuePos = " (Position: " + queue + ")";
            } else {
                queuePos = "";
            }
        }
    }

    public static String getQueuePosition() {
        if (mc.isInSingleplayer()) return "";
        else if (mc.world == null) return "";
        else return queuePos;
    }

    @EventHandler
    private void onKey(KeyEvent event) {
        // Click GUI
        if (event.action == KeyAction.Press && KeyBinds.OPEN_CLICK_GUI.matchesKey(event.key, 0)) {
            if (!Utils.canUpdate() && Utils.isWhitelistedScreen() || mc.currentScreen == null) openClickGui();
        }
    }

    @EventHandler
    private void onCharTyped(CharTypedEvent event) {
        if (mc.currentScreen != null) return;
        if (!Config.get().openChatOnPrefix) return;

        if (event.c == Config.get().prefix.charAt(0)) {
            mc.openScreen(new ChatScreen(Config.get().prefix));
            event.cancel();
        }
    }
}
