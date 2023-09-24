package mathax.client.systems.modules.misc;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import mathax.client.MatHax;
import mathax.client.eventbus.EventHandler;
import mathax.client.events.world.ServerConnectBeginEvent;
import mathax.client.settings.DoubleSetting;
import mathax.client.settings.Setting;
import mathax.client.settings.SettingGroup;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.item.Items;

public class AutoReconnect extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    public final Setting<Double> time = sgGeneral.add(new DoubleSetting.Builder()
        .name("delay")
        .description("The amount of seconds to wait before reconnecting to the server.")
        .defaultValue(5)
        .min(0)
        .sliderRange(0, 15)
        .decimalPlaces(1)
        .build()
    );

    public Pair<ServerAddress, ServerInfo> lastServerConnection;

    public AutoReconnect() {
        super(Categories.Misc, Items.REPEATER, "auto-reconnect", "Automatically reconnects when disconnected from a server.");
        MatHax.EVENT_BUS.subscribe(new StaticListener());
    }

    private class StaticListener {
        @EventHandler
        private void onGameJoined(ServerConnectBeginEvent event) {
            lastServerConnection = new ObjectObjectImmutablePair<>(event.address, event.info);
        }
    }
}
