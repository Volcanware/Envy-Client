package mathax.client.systems.modules.experimental;

import mathax.client.eventbus.EventHandler;
import mathax.client.settings.BoolSetting;
import mathax.client.settings.Setting;
import mathax.client.settings.SettingGroup;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import mathax.client.systems.modules.Modules;
import mathax.client.systems.modules.world.Timer;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

public class Disabler extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> OnGround = sgGeneral.add(new BoolSetting.Builder()
        .name("OnGround")
        .description("Bypasses by sending a packet with OnGround set to true.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> PVPTEMPLE = sgGeneral.add(new BoolSetting.Builder()
        .name("PvPTemple")
        .description("PvPTemple Bypass Legacy")
        .defaultValue(false)
        .build()
    );
    public Disabler() {
        super(Categories.Experimental, Items.PAPER, "Disabler", "Attempts to Cripple Anticheat checks");
    }

    @EventHandler
    public boolean onTick() {

        if (OnGround.get()) {
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY(), mc.player.getZ(), true));
        }
        if (PVPTEMPLE.get()) {
            mc.player.setOnGround(false);
            mc.player.setPos(mc.player.getX(), mc.player.getY() + 0.42, mc.player.getZ());
            mc.player.setYaw(0);
            mc.player.setPitch(0);
        }
        return true;
    }
}
