package mathax.client.utils.network.serverfinder;

public interface IServerFinderDisconnectListener {
    void onServerDisconnect();
    void onServerFailed();
}
