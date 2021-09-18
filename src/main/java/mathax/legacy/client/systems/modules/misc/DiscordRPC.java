package mathax.legacy.client.systems.modules.misc;

import mathax.legacy.client.MatHaxLegacy;
import mathax.legacy.client.Version;
import mathax.legacy.client.bus.EventHandler;
import mathax.legacy.client.events.game.ReceiveMessageEvent;
import mathax.legacy.client.settings.*;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.utils.Utils;
import mathax.legacy.client.utils.placeholders.DiscordPlaceholder;
import mathax.legacy.client.utils.placeholders.Placeholders;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.minecraft.item.Items;

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

    private final Setting<Boolean> queuePosition = sgGeneral.add(new BoolSetting.Builder()
        .name("queue-position")
        .description("Appends Queue position to MatHax RPC if in Queue.")
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
        super(Categories.Misc, Items.COMMAND_BLOCK, "discord-RPC", "Shows MatHax Legacy as your Discord status.");

        runInMainMenu = true;
    }

    @Override
    public void onActivate() {
        MatHaxLegacy.LOG.info(MatHaxLegacy.logprefix + "Enabling Discord Rich Presence...");
        net.arikia.dev.drpc.DiscordRPC.discordInitialize(APP_ID, handlers, true, STEAM_ID);
        rpc.startTimestamp = System.currentTimeMillis() / 1000;
        rpc.details = Placeholders.apply("%version% | %username%" + Utils.getDiscordPlayerHealth());
        rpc.state = DiscordPlaceholder.apply("%activity%" + getQueue());
        rpc.largeImageKey = "logo";
        rpc.largeImageText = "MatHax Legacy " + Version.getStylized();
        applySmallImage();
        rpc.smallImageText = DiscordPlaceholder.apply("%activity%" + getQueue());
        rpc.partyId = "ae488379-351d-4a4f-ad32-2b9b01c91657";
        rpc.joinSecret = "MTI4NzM0OjFpMmhuZToxMjMxMjM=";
        rpc.partySize = Utils.mc.getNetworkHandler() != null ? Utils.mc.getNetworkHandler().getPlayerList().size() : 1;
        rpc.partyMax = 1;
        net.arikia.dev.drpc.DiscordRPC.discordUpdatePresence(rpc);
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                net.arikia.dev.drpc.DiscordRPC.discordRunCallbacks();
                try {
                    rpc.details = DiscordPlaceholder.apply("%version% | %username%" + Utils.getDiscordPlayerHealth());
                    rpc.state = DiscordPlaceholder.apply("%activity%" + getQueue());
                    rpc.largeImageKey = "logo";
                    rpc.largeImageText = "MatHax Legacy " + Version.getStylized();
                    applySmallImage();
                    rpc.smallImageText = DiscordPlaceholder.apply("%activity%" + getQueue());
                    rpc.partySize = Utils.mc.getNetworkHandler() != null ? Utils.mc.getNetworkHandler().getPlayerList().size() : 1;
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
        deactivate();
    }

    public static void deactivate() {
        MatHaxLegacy.LOG.info(MatHaxLegacy.logprefix + "Disabling Discord Rich Presence...");
        net.arikia.dev.drpc.DiscordRPC.discordClearPresence();
        net.arikia.dev.drpc.DiscordRPC.discordShutdown();
        MatHaxLegacy.LOG.info(MatHaxLegacy.logprefix + "Discord Rich Presence disabled!");
    }

    private static String queuePos = "";

    @EventHandler
    private void onMessageRecieve(ReceiveMessageEvent event) {
        if (queuePosition.get()) {
            if (event.message.getString().contains("[MatHax Legacy] ")) return;
            String messageString = event.message.getString();
            if (messageString.contains("Position in queue: ")) {
                String queue = messageString.replace("Position in queue: ", "");
                queuePos = " (Position: " + queue + ")";
            } else {
                queuePos = "";
            }
        } else {
            queuePos = "";
        }
    }

    public static String getQueue() {
        if (Utils.mc.isInSingleplayer()) return "";
        else if (Utils.mc.world == null) return "";
        else return queuePos;
    }

    private void applySmallImage() {
        if (delay == 5) {
            if (number == 16) number = 1;
            if (smallImageMode.get() == SmallImageMode.Dogs) {
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
