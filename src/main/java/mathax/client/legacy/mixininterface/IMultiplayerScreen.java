package mathax.client.legacy.mixininterface;

import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.ServerInfo;

public interface IMultiplayerScreen
{
    MultiplayerServerListWidget getServerListSelector();

    void connectToServer(ServerInfo server);
}
