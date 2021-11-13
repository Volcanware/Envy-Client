package mathax.legacy.client.systems.modules.misc;

/*/-------------------------------------------------------------------------------------------------------------------------/*/
/*/ Imported from Atomic                                                                                                    /*/
/*/ https://github.com/0x151/Atomic/blob/master/src/main/java/me/zeroX150/atomic/feature/module/impl/exploit/PingSpoof.java /*/
/*/-------------------------------------------------------------------------------------------------------------------------/*/

import mathax.legacy.client.eventbus.EventHandler;
import mathax.legacy.client.events.packets.PacketEvent;
import mathax.legacy.client.settings.*;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import net.minecraft.item.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayPongC2SPacket;

import java.util.ArrayList;
import java.util.List;

public class PingSpoof extends Module {
    private record PacketEntry(Packet<?> packet, double delay, long entryTime) {}

    private final List<PacketEntry> entries = new ArrayList<>();
    private final List<Packet<?>> dontRepeat = new ArrayList<>();

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("Determines what mode to use. Delaying everything increases C2S lag, Delay Pong fakes it.")
        .defaultValue(Mode.Delay_Everything)
        .build()
    );

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("The delay between packet sending in ms.")
        .defaultValue(50)
        .min(0)
        .sliderRange(0, 5000)
        .build()
    );

    public PingSpoof() {
        super(Categories.Misc, Items.COMMAND_BLOCK, "ping-spoof", "Spoofs your ping.");
    }

    @Override
    public void onActivate() {
        entries.clear();
        dontRepeat.clear();
    }

    @EventHandler
    public void onPacket(PacketEvent.Send event) {
        if (!dontRepeat.contains(event.packet) && shouldDelayPacket(event.packet)) {
            event.setCancelled(true);
            entries.add(new PacketEntry(event.packet, delay.get(), System.currentTimeMillis()));
        } else dontRepeat.remove(event.packet);
    }

    boolean shouldDelayPacket(Packet<?> packet) {
        if (mode.get() == Mode.Delay_Everything) return true;
        else return packet instanceof PlayPongC2SPacket || packet instanceof KeepAliveC2SPacket;
    }

    @Override
    public String getInfoString() {
        return delay.get() + "ms";
    }

    public enum Mode {
        Delay_Everything,
        Delay_Pong;

        @Override
        public String toString() {
            return super.toString().replace("_", " ");
        }
    }
}
