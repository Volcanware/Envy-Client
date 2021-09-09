package mathax.legacy.client.systems.modules.misc;

import mathax.legacy.client.MatHaxLegacy;
import mathax.legacy.client.events.world.ConnectToServerEvent;
import mathax.legacy.client.settings.DoubleSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.bus.EventHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.item.Items;

public class AutoReconnect extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Double> time = sgGeneral.add(new DoubleSetting.Builder()
        .name("delay")
        .description("The amount of seconds to wait before reconnecting to the server.")
        .defaultValue(5)
        .min(0)
        .decimalPlaces(1)
        .build()
    );

    public ServerInfo lastServerInfo;

    public AutoReconnect() {
        super(Categories.Misc, Items.REPEATER, "auto-reconnect", "Automatically reconnects when disconnected from a server.");
        MatHaxLegacy.EVENT_BUS.subscribe(new StaticListener());
    }

    private class StaticListener {
        @EventHandler
        private void onConnectToServer(ConnectToServerEvent event) {
            lastServerInfo = mc.isInSingleplayer() ? null : mc.getCurrentServerEntry();
        }
    }
}
