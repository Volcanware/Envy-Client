package envy.client.systems.modules.world;

import envy.client.eventbus.EventHandler;
import envy.client.events.packets.PacketEvent;
import envy.client.mixininterface.IPlayerInteractEntityC2SPacket;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;

public class MountBypass extends Module {
    private boolean dontCancel;
    //please dont
    public MountBypass() {
        super(Categories.World, Items.SADDLE, "mount-bypass", "Allows you to bypass the IllegalStack plugin and put chests on entities.");
    }

    @EventHandler
    public void onSendPacket(PacketEvent.Send event) {
        if (dontCancel) {
            dontCancel = false;
            return;
        }

        if (event.packet instanceof IPlayerInteractEntityC2SPacket packet) {
            if (packet.getType() == PlayerInteractEntityC2SPacket.InteractType.INTERACT_AT && packet.getEntity() instanceof AbstractDonkeyEntity) event.cancel();
        }
    }
}
