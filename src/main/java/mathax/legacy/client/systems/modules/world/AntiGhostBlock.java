package mathax.legacy.client.systems.modules.world;

import mathax.legacy.client.eventbus.EventHandler;
import mathax.legacy.client.events.entity.player.BreakBlockEvent;
import mathax.legacy.client.events.entity.player.PlaceBlockEvent;
import mathax.legacy.client.events.game.GameLeftEvent;
import mathax.legacy.client.events.packets.PacketEvent;
import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.settings.BoolSetting;
import mathax.legacy.client.settings.IntSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AntiGhostBlock extends Module {
    SettingGroup sgModuleFixes = settings.createGroup("Module Fixes");

    public final Setting<Boolean> bedAuraFix = sgModuleFixes.add(new BoolSetting.Builder()
        .name("bed-aura-fix")
        .description("Fixes Bed Aura issues with Anti Ghost Block.")
        .defaultValue(false)
        .build()
    );

    public AntiGhostBlock() {
        super(Categories.World, Items.BARRIER, "anti-ghost-block", "Automatically tries to remove ghost blocks.");
    }

    @EventHandler
    public void onBreakBlock(BreakBlockEvent event) {
        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, event.blockPos, Direction.UP));
    }
}
