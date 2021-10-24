package mathax.legacy.client.utils.network.serverfinder;

public interface IServerFinderDoneListener {
    void onServerDone(ServerPinger pinger);
    void onServerFailed(ServerPinger pinger);
}
