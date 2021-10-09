package mathax.legacy.client.gui.screens.servermanager;

import mathax.legacy.client.Version;
import mathax.legacy.client.gui.GuiTheme;
import mathax.legacy.client.gui.WindowScreen;
import mathax.legacy.client.gui.widgets.containers.WTable;
import mathax.legacy.client.gui.widgets.pressable.WCheckbox;
import mathax.legacy.client.mixininterface.IMultiplayerScreen;
import mathax.legacy.client.utils.render.color.Color;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.util.Formatting;

public class ServerCleanUpScreen extends WindowScreen {
    private final MultiplayerScreen multiplayerScreen;
    private WCheckbox removeAll;
    private WCheckbox removeFailed;
    private WCheckbox removeOutdated;
    private WCheckbox removeUnknown;
    private WCheckbox removeFound;
    private WCheckbox rename;

    public ServerCleanUpScreen(GuiTheme theme, MultiplayerScreen multiplayerScreen, Screen parent) {
        super(theme, "Clean Up");
        this.multiplayerScreen = multiplayerScreen;
        this.parent = parent;
    }

    @Override
    public void initWidgets() {
        WTable table = add(new WTable()).widget();
        table.add(theme.label("Servers to remove:"));
        table.row();
        table.add(theme.label("Unknown Hosts")).widget().tooltip = "";
        removeUnknown = table.add(theme.checkbox(true)).widget();
        table.row();
        table.add(theme.label("Outdated Servers"));
        removeOutdated = table.add(theme.checkbox(false)).widget();
        table.row();
        table.add(theme.label("Failed Ping"));
        removeFailed = table.add(theme.checkbox(true)).widget();
        table.row();
        table.add(theme.label("\"Server Finder\" Servers"));
        removeFound = table.add(theme.checkbox(false)).widget();
        table.row();
        table.add(theme.label("Everything")).widget().color = new Color(255, 0, 0);
        removeAll = table.add(theme.checkbox(false)).widget();
        table.row();
        table.add(theme.label("Rename all Servers"));
        rename = table.add(theme.checkbox(true)).widget();
        table.row();
        table.add(theme.button("Execute")).expandX().widget().action = this::cleanUp;
    }

    private void cleanUp() {
        for (int i = multiplayerScreen.getServerList().size() - 1; i >= 0; i--) {
            ServerInfo server = multiplayerScreen.getServerList().get(i);

            if (removeAll.checked || shouldRemove(server)) multiplayerScreen.getServerList().remove(server);
        }

        if (rename.checked)
            for (int i = 0; i < multiplayerScreen.getServerList().size(); i++) {
                ServerInfo server = multiplayerScreen.getServerList().get(i);
                server.name = "Server Finder " + (i + 1);
            }

        saveServerList();
        client.setScreen(parent);
    }

    private boolean shouldRemove(ServerInfo server) {
        if (server == null) return false;

        if (removeUnknown.checked && isUnknownHost(server)) return true;

        if (removeOutdated.checked && !isSameProtocol(server)) return true;

        if (removeFailed.checked && isFailedPing(server)) return true;

        return removeFound.checked && isFoundServer(server);
    }

    private boolean isUnknownHost(ServerInfo server) {
        if (server.label == null) return false;

        if (server.label.getString() == null) return false;

        return server.label.getString().equals(Formatting.DARK_RED + "Can't resolve hostname");
    }

    private boolean isSameProtocol(ServerInfo server) {
        return server.protocolVersion == Version.getMinecraftProtocol();
    }

    private boolean isFailedPing(ServerInfo server) {
        return server.ping != -2L && server.ping < 0L;
    }

    private boolean isFoundServer(ServerInfo server) {
        return server.name != null && server.name.startsWith("Server Finder ");
    }

    private void saveServerList() {
        multiplayerScreen.getServerList().saveFile();

        MultiplayerServerListWidget serverListSelector = ((IMultiplayerScreen)multiplayerScreen).getServerListWidget();

        serverListSelector.setSelected(null);
        serverListSelector.setServers(multiplayerScreen.getServerList());
    }
}
