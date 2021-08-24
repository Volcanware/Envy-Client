package mathax.client.legacy.discord;

import mathax.client.legacy.utils.Utils;
import mathax.client.legacy.utils.misc.LastServerInfo;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import mathax.client.legacy.MatHaxClientLegacy;
import mathax.client.legacy.gui.tabs.builtin.DiscordPresenceTab;
import mathax.client.legacy.utils.misc.placeholders.DiscordPlaceholder;
import mathax.client.legacy.utils.misc.placeholders.Placeholders;

import static mathax.client.legacy.utils.Utils.mc;

public class MatHaxDiscordRPC {

    private static final String APP_ID = "878967665501306920";
    private static final String STEAM_ID = "";

    private static final DiscordRichPresence rpc = new DiscordRichPresence();
    private static final DiscordEventHandlers handlers = new DiscordEventHandlers();
    public static int delay = 0;
    public static int number = 1;

    public static void init() {
        if (DiscordPresenceTab.enabled.get()) {
            MatHaxClientLegacy.LOG.info(MatHaxClientLegacy.logprefix + "Enabling Discord Rich Presence...");
            DiscordRPC.discordInitialize(APP_ID, handlers, true, STEAM_ID);
            rpc.startTimestamp = System.currentTimeMillis() / 1000;
            rpc.details = Placeholders.apply("%version% | %username%" + Utils.getDiscordPlayerHealth());
            rpc.state = DiscordPlaceholder.apply("%activity%" + MatHaxClientLegacy.getQueuePosition());
            rpc.largeImageKey = "logo";
            rpc.largeImageText = "MatHax Legacy " + MatHaxClientLegacy.discordVersion;
            applySmallImage();
            rpc.smallImageText = DiscordPlaceholder.apply("%activity%" + MatHaxClientLegacy.getQueuePosition());
            rpc.partyId = "ae488379-351d-4a4f-ad32-2b9b01c91657";
            rpc.joinSecret = "MTI4NzM0OjFpMmhuZToxMjMxMjM=";
            rpc.partySize = mc.getNetworkHandler() != null ? mc.getNetworkHandler().getPlayerList().size() : 1;
            rpc.partyMax = 1;
            DiscordRPC.discordUpdatePresence(rpc);
            new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    DiscordRPC.discordRunCallbacks();
                    try {
                        rpc.details = DiscordPlaceholder.apply("%version% | %username%" + Utils.getDiscordPlayerHealth());
                        rpc.state = DiscordPlaceholder.apply("%activity%" + MatHaxClientLegacy.getQueuePosition());
                        rpc.largeImageKey = "logo";
                        rpc.largeImageText = "MatHax Legacy " + MatHaxClientLegacy.discordVersion;
                        applySmallImage();
                        rpc.smallImageText = DiscordPlaceholder.apply("%activity%" + MatHaxClientLegacy.getQueuePosition());
                        rpc.partySize = mc.getNetworkHandler() != null ? mc.getNetworkHandler().getPlayerList().size() : 1;
                        rpc.partyMax = 1;
                        DiscordRPC.discordUpdatePresence(rpc);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                    }
                }
            }, "RPC-Callback-Handler").start();
            MatHaxClientLegacy.LOG.info(MatHaxClientLegacy.logprefix + "Discord Rich Presence enabled!");
        }
    }

    public static void disable() {
        MatHaxClientLegacy.LOG.info(MatHaxClientLegacy.logprefix + "Disabling Discord Rich Presence...");
        DiscordRPC.discordClearPresence();
        DiscordRPC.discordShutdown();
        MatHaxClientLegacy.LOG.info(MatHaxClientLegacy.logprefix + "Discord Rich Presence disabled!");
    }

    private static void applySmallImage() {
        if (delay == 5) {
            if (number == 16) {
                number = 1;
            }
            if (DiscordPresenceTab.smallImageMode.get() == DiscordPresenceTab.SmallImageMode.Dogs) {
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
}
