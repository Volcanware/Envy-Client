package mathax.legacy.client.systems.modules.movement;

import mathax.legacy.client.MatHaxLegacy;
import mathax.legacy.client.events.packets.PacketEvent;
import mathax.legacy.client.events.render.Render3DEvent;
import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.mixininterface.IVec3d;
import mathax.legacy.client.renderer.ShapeMode;
import mathax.legacy.client.settings.*;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.eventbus.EventHandler;
import mathax.legacy.client.systems.modules.render.PopChams;
import mathax.legacy.client.utils.Utils;
import mathax.legacy.client.utils.entity.fakeplayer.FakePlayerEntity;
import mathax.legacy.client.utils.render.WireframeEntityRenderer;
import mathax.legacy.client.utils.render.color.SettingColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Blink extends Module {
    private final List<PlayerMoveC2SPacket> packets = new ArrayList<>();
    private final List<BlinkPlayer> blinks = new ArrayList<>();

    private int timer = 0;

    private final SettingGroup sgRender = settings.createGroup("Render");

    // Render

    private final Setting<Boolean> render = sgRender.add(new BoolSetting.Builder()
        .name("render")
        .description("Renders a ghost in the place you really are in.")
        .defaultValue(true)
        .build()
    );

    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode")
        .description("How the shapes are rendered.")
        .defaultValue(ShapeMode.Both)
        .build()
    );

    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
        .name("side-color")
        .description("The side color.")
        .defaultValue(new SettingColor(MatHaxLegacy.INSTANCE.MATHAX_COLOR.r, MatHaxLegacy.INSTANCE.MATHAX_COLOR.g, MatHaxLegacy.INSTANCE.MATHAX_COLOR.b, 75))
        .build()
    );

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
        .name("line-color")
        .description("The line color.")
        .defaultValue(new SettingColor(MatHaxLegacy.INSTANCE.MATHAX_COLOR.r, MatHaxLegacy.INSTANCE.MATHAX_COLOR.g, MatHaxLegacy.INSTANCE.MATHAX_COLOR.b))
        .build()
    );


    public Blink() {
        super(Categories.Movement, Items.TINTED_GLASS, "blink", "Allows you to essentially teleport while suspending motion updates.");
    }

    @Override
    public void onActivate() {
        synchronized (blinks) {
            blinks.add(new BlinkPlayer(mc.player));
        }
    }

    @Override
    public void onDeactivate() {
        synchronized (packets) {
            packets.forEach(p -> mc.player.networkHandler.sendPacket(p));
            packets.clear();
            timer = 0;
        }

        synchronized (blinks) {
            blinks.clear();
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        timer++;
    }

    @EventHandler
    private void onSendPacket(PacketEvent.Send event) {
        if (!(event.packet instanceof PlayerMoveC2SPacket)) return;
        event.cancel();

        synchronized (packets) {
            PlayerMoveC2SPacket p = (PlayerMoveC2SPacket) event.packet;
            PlayerMoveC2SPacket prev = packets.size() == 0 ? null : packets.get(packets.size() - 1);

            if (prev != null && p.isOnGround() == prev.isOnGround() && p.getYaw(-1) == prev.getYaw(-1) && p.getPitch(-1) == prev.getPitch(-1) && p.getX(-1) == prev.getX(-1) && p.getY(-1) == prev.getY(-1) && p.getZ(-1) == prev.getZ(-1)) return;

            packets.add(p);
        }
    }

    @Override
    public String getInfoString() {
        return String.format("%.1f", timer / 20f);
    }

    @EventHandler
    private void onRender3D(Render3DEvent event) {
        if (!render.get()) return;
        synchronized (blinks) {
            blinks.forEach(blinkPlayer -> blinkPlayer.render(event));
        }
    }

    private class BlinkPlayer extends FakePlayerEntity {
        public BlinkPlayer(PlayerEntity player) {
            super(player, "blink", Utils.getPlayerHealth(), true);
        }

        public void render(Render3DEvent event) {
            WireframeEntityRenderer.render(event, this, 1, sideColor.get(), lineColor.get(), shapeMode.get());
        }
    }
}
