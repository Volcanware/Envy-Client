package mathax.client.gui.screens.server.servermanager;

import mathax.client.gui.GuiTheme;
import mathax.client.gui.WindowScreen;
import mathax.client.gui.widgets.containers.WTable;
import mathax.client.gui.widgets.pressable.WCheckbox;
import mathax.client.mixininterface.IMultiplayerScreen;
import mathax.client.utils.Version;
import mathax.client.utils.render.color.Color;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.util.Formatting;

public class ServerCleanUpScreen extends WindowScreen {
    private final MultiplayerScreen multiplayerScreen;
    private final WCheckbox removeAll;
    private final WCheckbox removeFailed;
    private final WCheckbox removeOutdated;
    private final WCheckbox removeUnknown;
    private final WCheckbox removeFound;
    private final WCheckbox rename;

    public ServerCleanUpScreen(GuiTheme theme, MultiplayerScreen multiplayerScreen, Screen parent) {
        super(theme, "Clean Up");
        this.multiplayerScreen = multiplayerScreen;
        this.parent = parent;
        this.removeUnknown = theme.checkbox(true);
        this.removeOutdated = theme.checkbox(false);
        this.removeFailed = theme.checkbox(true);
        this.removeFound = theme.checkbox(false);
        this.removeAll = theme.checkbox(false);
        this.rename = theme.checkbox(false);
    }

    @Override
    public void initWidgets() {
        WTable table = add(new WTable()).widget();
        table.add(theme.label("Servers to remove:"));
        table.row();
        table.add(theme.label("Unknown Hosts")).widget().tooltip = "";
        table.add(removeUnknown).widget();
        table.row();
        table.add(theme.label("Outdated Servers"));
        table.add(removeOutdated).widget();
        table.row();
        table.add(theme.label("Failed Ping"));
        table.add(removeFailed).widget();
        table.row();
        table.add(theme.label("\"Server Finder\" Servers"));
        table.add(removeFound).widget();
        table.row();
        table.add(theme.label("Everything")).widget().color = new Color(255, 0, 0);
        table.add(removeAll).widget();
        table.row();
        table.add(theme.label("Rename all Servers"));
        table.add(rename).widget();
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
