package envy.client.systems.modules.experimental;

import envy.client.eventbus.EventHandler;
import envy.client.events.world.TickEvent;
import envy.client.settings.BoolSetting;
import envy.client.settings.Setting;
import envy.client.settings.SettingGroup;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class Disabler extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> OnGround = sgGeneral.add(new BoolSetting.Builder()
        .name("OnGround")
        .description("Bypasses by sending a packet with OnGround set to true.")
        .defaultValue(false)
        .build()
    );

/*    private final Setting<Boolean> PVPTEMPLE = sgGeneral.add(new BoolSetting.Builder()
        .name("PvPTemple")
        .description("PvPTemple Bypass Legacy")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> Offset = sgGeneral.add(new BoolSetting.Builder()
        .name("Offset")
        .description("Attemps to Bypass by sending a position packet with a small offset.")
        .defaultValue(false)
        .build()
    );*/
    public Disabler() {
        super(Categories.Experimental, Items.PAPER, "Disabler", "Attempts to Cripple Anticheat checks");
    }

    @EventHandler
    public boolean onTick(TickEvent.Post event) {

        if (OnGround.get()) {
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY(), mc.player.getZ(), true));
        }
        //These Need to be converted to Packets
        //Chief's Problem
/*        if (PVPTEMPLE.get()) {
            mc.player.setOnGround(false);
            mc.player.setPos(mc.player.getX(), mc.player.getY() + 0.42, mc.player.getZ());
            mc.player.setYaw(0);
            mc.player.setPitch(0);
        }*/

/*        if (Offset.get()) {
            Vec3d playerPos = mc.player.getPos();
            double offsetX = 0.05;
            double offsetY = 0;
            double offsetZ = 0.05;
            Vec3d offsetVector = new Vec3d(offsetX, offsetY, offsetZ);
            Vec3d newPlayerPos = new Vec3d(playerPos.x + 0.5, playerPos.y, playerPos.z + 0.5).add(offsetVector);
            mc.player.setPosition(newPlayerPos.x, newPlayerPos.y, newPlayerPos.z);
        }*/
        return true;
    }
}
