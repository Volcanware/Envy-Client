package mathax.client.systems.modules.chat;

import mathax.client.eventbus.EventHandler;
import mathax.client.events.packets.PacketEvent;
import mathax.client.events.world.TickEvent;
import mathax.client.mixin.MinecraftServerAccessor;
import mathax.client.settings.*;
import mathax.client.systems.enemies.Enemies;
import mathax.client.systems.friends.Friends;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import mathax.client.systems.modules.Modules;
import mathax.client.systems.modules.client.DiscordRPC;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*/-------------------------------------------------------------------------------------------------------------------------/*/
/*/ Made by cally72jhb and modified by Matejko06                                                                            /*/
/*/ https://github.com/cally72jhb/vector-addon/blob/main/src/main/java/cally72jhb/addon/system/modules/player/Welcomer.java /*/
/*/-------------------------------------------------------------------------------------------------------------------------/*/

public class Welcomer extends Module {
    private List<PlayerListS2CPacket.Entry> prevEntries;
    private List<PlayerListS2CPacket.Entry> entries;

    private boolean sentWelcome;
    private boolean sentBye;

    private int welcomeTimer;
    private int byeTimer;

    private Random random;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgWelcome = settings.createGroup("Welcome");
    private final SettingGroup sgGoodbye = settings.createGroup("Goodbye");

    // General

    private final Setting<Boolean> ignoreFriends = sgGeneral.add(new BoolSetting.Builder()
        .name("ignore-friends")
        .description("Ignores friended players.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> ignoreEnemies = sgGeneral.add(new BoolSetting.Builder()
        .name("ignore-enemies")
        .description("Ignores enemy players.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> clientSide = sgGeneral.add(new BoolSetting.Builder()
        .name("client-side")
        .description("Sends the messages client-side.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> randomMsg = sgGeneral.add(new BoolSetting.Builder()
        .name("random")
        .description("Sends random messages every join or leave.")
        .defaultValue(true)
        .build()
    );

    // Welcome

    private final Setting<Boolean> welcome = sgWelcome.add(new BoolSetting.Builder()
        .name("enabled")
        .description("Sends messages in the chat when a player joins.")
        .defaultValue(true)
        .build()
    );

    private final Setting<String> welcomeString = sgWelcome.add(new StringSetting.Builder()
        .name("message")
        .description("The message to send when a player joins.")
        .defaultValue("Welcome to %server%, %player%!")
        .visible(() -> !randomMsg.get() && welcome.get())
        .build()
    );

    private final Setting<List<String>> welcomeMessages = sgWelcome.add(new StringListSetting.Builder()
        .name("messages")
        .description("The random messages to send when a player joins.")
        .defaultValue(List.of(
            "Welcome to %server%, %player%!",
            "Hello, %player%!"
        ))
        .visible(() -> randomMsg.get() && welcome.get())
        .build()
    );

    private final Setting<Integer> welcomeDelay = sgWelcome.add(new IntSetting.Builder()
        .name("delay")
        .description("How long to wait before sending another welcome message in ticks.")
        .defaultValue(20)
        .min(0)
        .visible(welcome::get)
        .build()
    );

    // Goodbye

    private final Setting<Boolean> bye = sgGoodbye.add(new BoolSetting.Builder()
        .name("enabled")
        .description("Sends messages in the chat when a player leaves.")
        .defaultValue(true)
        .build()
    );

    private final Setting<String> byeString = sgGoodbye.add(new StringSetting.Builder()
        .name("message")
        .description("The message to send when a player leaves.")
        .defaultValue("Bye, %player%!")
        .visible(() -> !randomMsg.get() && bye.get())
        .build()
    );

    private final Setting<List<String>> byeMessages = sgGoodbye.add(new StringListSetting.Builder()
        .name("messages")
        .description("The random messages to send when a player leaves.")
        .defaultValue(List.of(
            "Bye, %player%!",
            "See you soon, %player%!"
        ))
        .visible(() -> randomMsg.get() && bye.get())
        .build()
    );

    private final Setting<Integer> byeDelay = sgGoodbye.add(new IntSetting.Builder()
        .name("delay")
        .description("How long to wait before sending another bye message in ticks.")
        .defaultValue(20)
        .min(0)
        .visible(bye::get)
        .build()
    );

    public Welcomer() {
        super(Categories.Chat, Items.COMMAND_BLOCK, "welcomer", "Send a chat message when a player joins or leaves.");
    }

    @Override
    public void onActivate() {
        prevEntries = new ArrayList<>();
        random = new Random();
        sentWelcome = false;
        welcomeTimer = 0;
        sentBye = false;
        byeTimer = 0;
    }

    @EventHandler
    private void onReceivePacket(PacketEvent.Receive event) {
        if (!(event.packet instanceof PlayerListS2CPacket packet)) return;
        if (packet.getAction() != PlayerListS2CPacket.Action.ADD_PLAYER && packet.getAction() != PlayerListS2CPacket.Action.REMOVE_PLAYER) return;

        entries = packet.getEntries();

        if (packet.getAction() == PlayerListS2CPacket.Action.ADD_PLAYER)
            for (PlayerListS2CPacket.Entry entry : entries) {
                if (welcome.get() && entry != null && entry.getProfile() != null) {
                    String name = entry.getProfile().getName();
                    if (name == null) return;

                    boolean existed = true;

                    for (PlayerListS2CPacket.Entry prevEntry : prevEntries) {
                        if (prevEntry != null && prevEntry.getDisplayName() != null && entry.getDisplayName().asString().equals(prevEntry.getDisplayName().asString())) existed = false;
                    }

                    if (sentWelcome) return;

                    if (ignoreFriends.get() && Friends.get().get(name) != null) return;
                    if (ignoreEnemies.get() && Enemies.get().get(name) != null) return;

                    if (existed) {
                        if (clientSide.get()) info(apply(name, randomMsg.get() ? welcomeMessages.get() : List.of(welcomeString.get())));
                        else sendMsg(apply(name, randomMsg.get() ? welcomeMessages.get() : List.of(welcomeString.get())));
                    }

                    sentWelcome = true;
            }
        }

        if (packet.getAction() == PlayerListS2CPacket.Action.REMOVE_PLAYER) {
            for (PlayerListS2CPacket.Entry entry : entries) {
                if (bye.get() && entry != null && entry.getProfile() != null) {
                    String name = entry.getProfile().getName();
                    if (name == null) return;

                    boolean existed = true;

                    for (PlayerListS2CPacket.Entry prevEntry : prevEntries) {
                        if (prevEntry != null && prevEntry.getDisplayName() != null && entry.getDisplayName().asString().equals(prevEntry.getDisplayName().asString())) existed = false;
                    }

                    if (sentBye) return;

                    if (ignoreFriends.get() && Friends.get().get(name) != null) return;
                    if (ignoreEnemies.get() && Enemies.get().get(name) != null) return;

                    if (existed) {
                        if (clientSide.get()) info(apply(name, randomMsg.get() ? byeMessages.get() : List.of(byeString.get())));
                        else sendMsg(apply(name, randomMsg.get() ? byeMessages.get() : List.of(byeString.get())));
                    }

                    sentBye = true;
                }
            }
        }

        prevEntries = packet.getEntries();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (welcomeTimer >= welcomeDelay.get()) {
            sentWelcome = false;
            welcomeTimer = 0;
        } else welcomeTimer++;

        if (byeTimer >= byeDelay.get()) {
            sentBye = false;
            byeTimer = 0;
        } else byeTimer++;
    }

    private void sendMsg(String string) {
        mc.player.sendChatMessage(string);
    }

    private String apply(String player, List<String> strings) {
        String string = strings.get(random.nextInt(strings.size()));
        string = string.replace("%server%", getServer());
        return string.replace("%player%", player);
    }

    private String getServer() {

        // Multiplayer
        if (mc.getCurrentServerEntry() != null) {
            String name = mc.isConnectedToRealms() ? "realms" : mc.getCurrentServerEntry().address;

            if (Modules.get().get(DiscordRPC.class).serverVisibility.get()) return name;
            else return "a server";
        }

        if ((mc.getServer()) == null) return "unknown";
        if (((MinecraftServerAccessor) mc.getServer()).getSession() == null) return "unknown";

        // Singleplayer
        if (mc.isInSingleplayer()) {
            File folder = ((MinecraftServerAccessor) mc.getServer()).getSession().getWorldDirectory(mc.world.getRegistryKey()).toFile();
            if (folder.toPath().relativize(mc.runDirectory.toPath()).getNameCount() != 2) folder = folder.getParentFile();
            return folder.getName();
        }

        return "unknown";
    }
}
