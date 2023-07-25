package envy.client.systems.modules.player;

import envy.client.eventbus.EventHandler;
import envy.client.events.packets.PacketEvent;
import envy.client.events.world.TickEvent;
import envy.client.mixin.PlayerMoveC2SPacketAccessor;
import envy.client.settings.BoolSetting;
import envy.client.settings.Setting;
import envy.client.settings.SettingGroup;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class AntiHunger extends Module {
    private boolean lastOnGround;
    private boolean sendOnGroundTruePacket;
    private boolean ignorePacket;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Boolean> sprint = sgGeneral.add(new BoolSetting.Builder()
        .name("sprint")
        .description("Spoofs sprinting packets.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> onGround = sgGeneral.add(new BoolSetting.Builder()
        .name("on-ground")
        .description("Spoofs the onGround flag.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> waterCheck = sgGeneral.add(new BoolSetting.Builder()
        .name("water-check")
        .description("Pauses the module if you are in water.")
        .defaultValue(true)
        .build()
    );

    public AntiHunger() {
        super(Categories.Player, Items.COOKED_BEEF, "anti-hunger", "Reduces (does NOT remove) hunger consumption.");
    }

    @Override
    public boolean onActivate() {
        lastOnGround = mc.player.isOnGround();
        sendOnGroundTruePacket = true;
        return false;
    }

    @EventHandler
    private void onSendPacket(PacketEvent.Send event) {
        if (ignorePacket) return;

        if (event.packet instanceof ClientCommandC2SPacket && sprint.get()) {
            ClientCommandC2SPacket.Mode mode = ((ClientCommandC2SPacket) event.packet).getMode();

            if (mode == ClientCommandC2SPacket.Mode.START_SPRINTING || mode == ClientCommandC2SPacket.Mode.STOP_SPRINTING) event.cancel();
        }

        if (event.packet instanceof PlayerMoveC2SPacket && onGround.get() && mc.player.isOnGround() && mc.player.fallDistance <= 0.0 && !mc.interactionManager.isBreakingBlock()) ((PlayerMoveC2SPacketAccessor) event.packet).setOnGround(false);
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (waterCheck.get() && mc.player.isTouchingWater()) {
            ignorePacket = true;
            return;
        }

        if (mc.player.isOnGround() && !lastOnGround && !sendOnGroundTruePacket) sendOnGroundTruePacket = true;

        if (mc.player.isOnGround() && sendOnGroundTruePacket && onGround.get()) {
            ignorePacket = true;
            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
            ignorePacket = false;

            sendOnGroundTruePacket = false;
        }

        lastOnGround = mc.player.isOnGround();
    }
}
