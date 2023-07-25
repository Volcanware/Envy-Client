package envy.client.utils.misc;

import envy.client.Envy;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.text.Text;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionTypes;

public class FakeClientPlayer {
    private static ClientWorld world;
    private static PlayerEntity player;
    private static PlayerListEntry playerListEntry;

    private static String lastId;
    private static boolean needsNewEntry;

    public static void init() {
        Envy.EVENT_BUS.subscribe(FakeClientPlayer.class);
    }

    public static PlayerEntity getPlayer() {
        String id = Envy.mc.getSession().getUuid();

        if (player == null || (!id.equals(lastId))) {
            if (world == null) world = new ClientWorld(new ClientPlayNetworkHandler(Envy.mc, null, new ClientConnection(NetworkSide.CLIENTBOUND), Envy.mc.getSession().getProfile(), null), new ClientWorld.Properties(Difficulty.NORMAL, false, false), World.OVERWORLD, BuiltinRegistries.DIMENSION_TYPE.entryOf(DimensionTypes.OVERWORLD), 1, 1, Envy.mc::getProfiler, null, false, 0);

            player = new OtherClientPlayerEntity(world, Envy.mc.getSession().getProfile(), null);

            lastId = id;
            needsNewEntry = true;
        }

        return player;
    }

    public static PlayerListEntry getPlayerListEntry() {
        if (playerListEntry == null || needsNewEntry) {
            playerListEntry = new PlayerListEntry(PlayerListEntryFactory.create(Envy.mc.getSession().getProfile(), 0, GameMode.SURVIVAL, Text.of(Envy.mc.getSession().getProfile().getName()), null), null);
            needsNewEntry = false;
        }

        return playerListEntry;
    }
}
