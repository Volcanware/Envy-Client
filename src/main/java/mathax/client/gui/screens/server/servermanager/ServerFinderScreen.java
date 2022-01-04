package mathax.client.gui.screens.server.servermanager;

import mathax.client.MatHax;
import mathax.client.gui.GuiTheme;
import mathax.client.gui.WindowScreen;
import mathax.client.gui.widgets.WLabel;
import mathax.client.gui.widgets.containers.WHorizontalList;
import mathax.client.gui.widgets.containers.WTable;
import mathax.client.gui.widgets.input.WIntEdit;
import mathax.client.gui.widgets.input.WTextBox;
import mathax.client.gui.widgets.pressable.WButton;
import mathax.client.gui.widgets.pressable.WCheckbox;
import mathax.client.mixininterface.IMultiplayerScreen;
import mathax.client.utils.network.serverfinder.IPAddress;
import mathax.client.utils.network.serverfinder.IServerFinderDoneListener;
import mathax.client.utils.network.serverfinder.MServerInfo;
import mathax.client.utils.network.serverfinder.ServerPinger;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ServerFinderScreen extends WindowScreen implements IServerFinderDoneListener {
    private final MultiplayerScreen multiplayerScreen;
    private ServerFinderState state;

    private final WTextBox ipBox;
    private final WTextBox versionBox;
    private final WIntEdit maxThreadsBox;
    private final WButton searchButton;
    private final WLabel stateLabel;
    private final WLabel checkedLabel;
    private final WLabel workingLabel;
    private final WCheckbox scanPortsBox;

    private int maxThreads;
    private volatile int numActiveThreads;
    private volatile int checked;
    private volatile int working;

    private int targetChecked = 1792;

    private final Stack<String> ipsToPing = new Stack<>();

    private final Object serverFinderLock = new Object();

    public static ServerFinderScreen instance = null;
    private static int searchNumber = 0;
    private ArrayList<String> versionFilters = new ArrayList<>();
    private final int playerCountFilter = 0;

    public ServerFinderScreen(GuiTheme theme, MultiplayerScreen multiplayerScreen, Screen parent) {
        super(theme, "Server Discovery");
        this.multiplayerScreen = multiplayerScreen;
        this.parent = parent;
        this.ipBox = theme.textBox("127.0.0.1");
        this.versionBox = theme.textBox("1.18; 1.17; 1.16; 1.15; 1.14; 1.13; 1.12; 1.11; 1.10; 1.9; 1.8");
        this.maxThreadsBox = theme.intEdit(128, 1, 256, true);
        this.stateLabel = theme.label("");
        this.checkedLabel = theme.label("");
        this.searchButton = theme.button("Search");
        this.workingLabel = theme.label("");
        this.scanPortsBox = theme.checkbox(true);
        this.state = ServerFinderState.Not_Running;
        newSearch();
        instance = this;
    }

    @Override
    public void initWidgets() {
        add(theme.label("This will search for servers with similar IPs"));
        add(theme.label("to the IP you type into the field below."));
        add(theme.label("The servers it finds will be added to your server list."));
        WTable table = add(new WTable()).expandX().widget();
        table.add(theme.label("Server address:"));
        table.add(ipBox).expandX();
        table.row();
        table.add(theme.label("Max. Threads:"));
        table.add(maxThreadsBox);
        table.row();
        table.add(theme.label("Scan ports"));
        table.add(scanPortsBox);
        table.row();
        table.add(theme.label("Versions:"));
        table.add(versionBox).expandX();
        add(stateLabel);
        add(checkedLabel);
        add(workingLabel);
        WHorizontalList list = add(theme.horizontalList()).expandX().widget();
        list.add(searchButton).expandX();
        list.add(theme.button("Save")).expandX().widget().action = this::saveToFile;
        searchButton.action = this::searchOrCancel;
    }

    private void newSearch() {
        searchNumber = (searchNumber + 1) % 1000;
    }

    public void incrementTargetChecked(int amount) {
        synchronized(serverFinderLock) {
            if (state != ServerFinderState.Cancelled) targetChecked += amount;
        }
    }

    public ServerFinderState getState() {
        return state;
    }

    private void saveToFile() {
        int newIPs = 0;

        Path filePath = MatHax.GAME_FOLDER.toPath().resolve("servers.txt");
        Set<IPAddress> hashedIPs = new HashSet<>();
        if (Files.exists(filePath)) {
            try {
                List<String> ips = Files.readAllLines(filePath);
                for (String ip: ips) {
                    IPAddress parsedIP = IPAddress.fromText(ip);
                    if (parsedIP != null)
                        hashedIPs.add(parsedIP);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ServerList servers = multiplayerScreen.getServerList();
        for (int i = 0; i < servers.size(); i++) {
            ServerInfo info = servers.get(i);
            IPAddress addr = IPAddress.fromText(info.address);
            if (addr != null && hashedIPs.add(addr)) newIPs++;
        }

        StringBuilder fileOutput = new StringBuilder();
        for (IPAddress ip : hashedIPs) {
            String stringIP = ip.toString();
            if (stringIP != null)
                fileOutput.append(stringIP).append("\n");
        }

        try {
            Files.writeString(filePath, fileOutput.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        MatHax.LOG.info(MatHax.logPrefix + "Saved " + newIPs + " new IP" + (newIPs == 1 ? "" : "s"));
    }

    private void searchOrCancel() {
        if (state.isRunning()) {
            state = ServerFinderState.Cancelled;
            return;
        }

        state = ServerFinderState.Resolving;
        maxThreads = maxThreadsBox.get();
        ipsToPing.clear();
        targetChecked = 1792;
        numActiveThreads = 0;
        checked = 0;
        working = 0;

        newSearch();

        parseVersionFilters();

        findServers();
    }

    private void parseVersionFilters() {
        String filter = versionBox.get();
        String[] versions = filter.split(";");
        if (versionFilters == null) versionFilters = new ArrayList<>();
        versionFilters.clear();
        for (String version : versions) {
            String trimmed = version.trim();
            if (trimmed.length() > 0) versionFilters.add(version.trim());
        }
    }

    private void findServers() {
        try {
            InetAddress addr = InetAddress.getByName(ipBox.get().split(":")[0].trim());

            int[] ipParts = new int[4];
            for (int i = 0; i < 4; i++)
                ipParts[i] = addr.getAddress()[i] & 0xff;

            state = ServerFinderState.Searching;
            int[] changes = { 0, 1, -1, 2, -2, 3, -3 };
            for (int change : changes)
                for (int i2 = 0; i2 <= 255; i2++) {
                    if (state == ServerFinderState.Cancelled) return;

                    int[] ipParts2 = ipParts.clone();
                    ipParts2[2] = ipParts[2] + change & 0xff;
                    ipParts2[3] = i2;
                    String ip = ipParts2[0] + "." + ipParts2[1] + "." + ipParts2[2] + "." + ipParts2[3];

                    ipsToPing.push(ip);
                }
            while (numActiveThreads < maxThreads && pingNewIP()) {}
        } catch (UnknownHostException e) {
            state = ServerFinderState.Unknown_Host;
        } catch (Exception e) {
            e.printStackTrace();
            state = ServerFinderState.Error;
        }
    }

    private boolean pingNewIP() {
        synchronized (serverFinderLock) {
            if (ipsToPing.size() > 0) {
                String ip = ipsToPing.pop();
                ServerPinger pinger = new ServerPinger(scanPortsBox.checked, searchNumber);
                pinger.addServerFinderDoneListener(this);
                pinger.ping(ip);
                numActiveThreads++;
                return true;
            }
        }

        return false;
    }

    @Override
    public void tick() {
        searchButton.set(state.isRunning() ? "Cancel" : "Search");
        if (state.isRunning()) {
            ipBox.setFocused(false);
            maxThreadsBox.set(maxThreads);
        }
        stateLabel.set(state.toString());
        checkedLabel.set("Checked: " + checked + " / " + targetChecked);
        workingLabel.set("Working: " + working);
        searchButton.visible = !ipBox.get().isEmpty();
    }

    private boolean isServerInList(String ip) {
        for (int i = 0; i < multiplayerScreen.getServerList().size(); i++) {
            if(multiplayerScreen.getServerList().get(i).address.equals(ip)) return true;
        }

        return false;
    }

    @Override
    public void onClose() {
        state = ServerFinderState.Cancelled;
        super.onClose();
    }

    public enum ServerFinderState {
        Not_Running(""),
        Searching("Searching..."),
        Resolving("Resolving..."),
        Unknown_Host("Unknown Host!"),
        Cancelled("Cancelled!"),
        Done("Done!"),
        Error("An error occurred!");

        private final String title;

        ServerFinderState(String title) {
            this.title = title;
        }

        public boolean isRunning() {
            return this == Searching || this == Resolving;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    public static int getSearchNumber() {
        return searchNumber;
    }

    private boolean filterPass(MServerInfo info) {
        if (info == null) return false;
        if (info.playerCount < playerCountFilter) return false;
        for (String version : versionFilters) {
            if (info.version != null && info.version.contains(version)) return true;
        }
        return versionFilters.isEmpty();
    }

    @Override
    public void onServerDone(ServerPinger pinger) {
        if (state == ServerFinderState.Cancelled || pinger == null || pinger.getSearchNumber() != searchNumber) return;
        synchronized (serverFinderLock) {
            checked++;
            numActiveThreads--;
        }

        if (pinger.isWorking()) {
            if (!isServerInList(pinger.getServerIP()) && filterPass(pinger.getServerInfo())) {
                synchronized (serverFinderLock) {
                    working++;
                    multiplayerScreen.getServerList().add(new ServerInfo("Server discovery #" + working, pinger.getServerIP(), false));
                    multiplayerScreen.getServerList().saveFile();
                    ((IMultiplayerScreen) multiplayerScreen).getServerListWidget().setSelected(null);
                    ((IMultiplayerScreen) multiplayerScreen).getServerListWidget().setServers(multiplayerScreen.getServerList());
                }
            }
        }

        while (numActiveThreads < maxThreads && pingNewIP());
        synchronized (serverFinderLock) {
            if (checked == targetChecked) state = ServerFinderState.Done;
        }
    }

    @Override
    public void onServerFailed(ServerPinger pinger) {
        if (state == ServerFinderState.Cancelled || pinger == null || pinger.getSearchNumber() != searchNumber) return;
        synchronized (serverFinderLock) {
            checked++;
            numActiveThreads--;
        }

        while (numActiveThreads < maxThreads && pingNewIP());
        synchronized (serverFinderLock) {
            if (checked == targetChecked) state = ServerFinderState.Done;
        }
    }
}
