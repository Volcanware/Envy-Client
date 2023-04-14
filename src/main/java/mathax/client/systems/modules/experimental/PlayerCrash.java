package mathax.client.systems.modules.experimental;

import mathax.client.eventbus.EventHandler;
import mathax.client.events.world.TickEvent;
import mathax.client.settings.DoubleSetting;
import mathax.client.settings.Setting;
import mathax.client.settings.SettingGroup;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

import static mathax.client.systems.modules.movement.FastFall.Mode.*;
import static mathax.client.systems.modules.movement.FastFall.Mode.Vanilla;

public class PlayerCrash extends Module {


    public PlayerCrash() {
        super(Categories.Experimental, Items.BOOK, "PlayerCrash", "crashes players with packets");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public void onTick(TickEvent.Post event) {
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(Math.random() >= 0.5));
        mc.player.networkHandler.sendPacket(new KeepAliveC2SPacket((int) (Math.random() * 8)));
    }
//    private final Setting<Double>  packets  = sgGeneral.add(new DoubleSetting.Builder()
//        .name("packets")
//        .defaultValue(400)
//        .min(0)
//        .sliderRange(0, 1000)
//        .build()
//    );

}


