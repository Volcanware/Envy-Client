package envy.client.utils.misc;

import envy.client.Envy;
import envy.client.mixininterface.IMultiplayerScreen;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;

public enum LastServerInfo {;
    private static ServerInfo lastServer;

    public static ServerInfo getLastServer() {
        return lastServer;
    }

    public static void setLastServer(ServerInfo server) {
        lastServer = server;
    }

    public static void joinLastServer(MultiplayerScreen mpScreen) {
        if (lastServer == null) return;

        ((IMultiplayerScreen)mpScreen).connectToServer(lastServer);
    }

    public static void reconnect(Screen prevScreen) {
        if (lastServer == null) return;

        ConnectScreen.connect(prevScreen, Envy.mc, ServerAddress.parse(lastServer.address), lastServer);
    }
}
