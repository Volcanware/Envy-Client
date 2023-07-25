package envy.client.systems.modules.combat;


import envy.client.eventbus.EventHandler;
import envy.client.events.packets.PacketEvent;
import envy.client.events.world.TickEvent;
import envy.client.mixininterface.IPlayerInteractEntityC2SPacket;
import envy.client.mixininterface.IPlayerMoveC2SPacket;
import envy.client.mixininterface.IVec3d;
import envy.client.settings.BoolSetting;
import envy.client.settings.EnumSetting;
import envy.client.settings.Setting;
import envy.client.settings.SettingGroup;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import envy.client.systems.modules.Modules;
import envy.client.utils.player.MoveHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class Criticals extends Module {

    private int ticks;
    private int offGroundTicks;
    private PlayerInteractEntityC2SPacket attackPacket;

    private HandSwingC2SPacket swingPacket;

    private boolean sendPackets;

    private int sendTimer;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("The mode on how Criticals will function.")
        .defaultValue(Mode.Packet)
        .build()
    );

    private final Setting<Boolean> ka = sgGeneral.add(new BoolSetting.Builder()
        .name("only-killaura")
        .description("Only performs crits when using killaura.")
        .defaultValue(false)
        .build()
    );

    public Criticals() {
        super(Categories.Combat, Items.DIAMOND_SWORD, "criticals", "Performs critical attacks when you hit your target.");
    }

    @Override
    public boolean onActivate() {
        attackPacket = null;
        swingPacket = null;
        sendPackets = false;
        sendTimer = 0;
        return false;
    }

    @Override
    public void onDeactivate() {
        this.ticks = 0;
    }

    @EventHandler
    private void onSendPacket(PacketEvent.Send event) {
        if (event.packet instanceof IPlayerInteractEntityC2SPacket packet && packet.getType() == PlayerInteractEntityC2SPacket.InteractType.ATTACK) {
            if (skipCrit()) return;

            Entity entity =  packet.getEntity();

            if (!(entity instanceof LivingEntity) || (entity != Modules.get().get(KillAura.class).getTarget() && ka.get())) return;

            switch (mode.get()) {
                case Packet -> {
                    sendPacket(0.0625);
                    sendPacket(0);
                }
                case Bypass -> {
                    sendPacket(0.11);
                    sendPacket(0.1100013579);
                    sendPacket(0.0000013579);
                }
                default -> {
                    if (!sendPackets) {
                        sendPackets = true;
                        sendTimer = mode.get() == Mode.Jump ? 6 : 4;
                        attackPacket = (PlayerInteractEntityC2SPacket) event.packet;

                        if (mode.get() == Mode.Jump) mc.player.jump();
                        else ((IVec3d) mc.player.getVelocity()).setY(0.25);
                        event.cancel();
                    }
                }
            }
        } else if (event.packet instanceof HandSwingC2SPacket && mode.get() != Mode.Packet) {
            if (skipCrit()) return;

            if (sendPackets && swingPacket == null) {
                swingPacket = (HandSwingC2SPacket) event.packet;

                event.cancel();
            }
        }
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (sendPackets) {
            if (sendTimer <= 0) {
                sendPackets = false;

                if (attackPacket == null || swingPacket == null) return;
                mc.getNetworkHandler().sendPacket(attackPacket);
                mc.getNetworkHandler().sendPacket(swingPacket);

                attackPacket = null;
                swingPacket = null;
            } else sendTimer--;
        }

        if (mode.get() == Mode.Low) {
            if (mc.player.isOnGround()) {
                this.offGroundTicks = 0;
            } else {
                ++this.offGroundTicks;
            }

            if (this.offGroundTicks == 1) {
                MoveHelper.motionY(-(0.01 - Math.random() / 500.0));
            }

            if (mc.player.isOnGround()) {
                MoveHelper.motionY(0.1 + Math.random() / 500.0);
            }
        }
        if (mode.get() == Mode.Low2) {
            if (mc.player.isOnGround()) {
                mc.player.setPosition(mc.player.getPos().x, mc.player.getPos().y + (0.3 - Math.random() / 500.0), mc.player.getPos().z);
            }
        }
    }

    private void sendPacket(double height) {
        double x = mc.player.getX();
        double y = mc.player.getY();
        double z = mc.player.getZ();

        PlayerMoveC2SPacket packet = new PlayerMoveC2SPacket.PositionAndOnGround(x, y + height, z, false);
        ((IPlayerMoveC2SPacket) packet).setNbt(1337);

        mc.player.networkHandler.sendPacket(packet);
    }

    private boolean skipCrit() {
        return !mc.player.isOnGround() || mc.player.isSubmergedInWater() || mc.player.isInLava() || mc.player.isClimbing();
    }

    @Override
    public String getInfoString() {
        return mode.get().name();
    }

    public enum Mode {
        Packet("Packet"),
        Bypass("Bypass"),
        Jump("Jump"),
        Low("Low"),
        Low2("Low 2"),
        Mini_Jump("Mini Jump");

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
