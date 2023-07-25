package envy.client.systems.modules.experimental;

import envy.client.events.packets.PacketEvent;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;

public class SecretClose extends Module{
    public SecretClose(){
        super(Categories.Experimental, Items.DIAMOND_HOE, "SecretClose", "spoofs yourself still being in inventory");
    }
    public void  onSendPacket(PacketEvent.Send event){
        if (event.packet instanceof CloseHandledScreenC2SPacket){
            event.setCancelled(true);
        }


    }
}
