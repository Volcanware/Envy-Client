package mathax.client.utils.misc;

import mathax.client.MatHax;
import mathax.client.eventbus.EventHandler;
import mathax.client.events.game.GameJoinedEvent;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.text.Text;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionTypes;

import static mathax.client.MatHax.mc;

public class FakeClientPlayer {
    private static ClientWorld world;
    private static PlayerEntity player;
    private static PlayerListEntry playerListEntry;

    private static String lastId;
    private static boolean needsNewEntry;

    public static void init() {
        MatHax.EVENT_BUS.subscribe(FakeClientPlayer.class);
    }

    public static PlayerEntity getPlayer() {
        String id = mc.getSession().getUuid();

        if (player == null || (!id.equals(lastId))) {
            if (world == null) world = new ClientWorld(new ClientPlayNetworkHandler(mc, null, new ClientConnection(NetworkSide.CLIENTBOUND), mc.getSession().getProfile(), null), new ClientWorld.Properties(Difficulty.NORMAL, false, false), World.OVERWORLD, BuiltinRegistries.DIMENSION_TYPE.entryOf(DimensionTypes.OVERWORLD), 1, 1, mc::getProfiler, null, false, 0);

            player = new OtherClientPlayerEntity(world, mc.getSession().getProfile(), null);

            lastId = id;
            needsNewEntry = true;
        }

        return player;
    }

    public static PlayerListEntry getPlayerListEntry() {
        if (playerListEntry == null || needsNewEntry) {
            playerListEntry = new PlayerListEntry(PlayerListEntryFactory.create(mc.getSession().getProfile(), 0, GameMode.SURVIVAL, Text.of(mc.getSession().getProfile().getName()), null), null);
            needsNewEntry = false;
        }

        return playerListEntry;
    }
}
