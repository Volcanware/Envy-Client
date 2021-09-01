package mathax.client.legacy.utils.entity.fakeplayer;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static mathax.client.legacy.utils.Utils.mc;

public class FakePlayerEntity extends OtherClientPlayerEntity {
    public FakePlayerEntity(String name, float health, boolean copyInv) {
        super(mc.world, new GameProfile(UUID.randomUUID(), name));

        copyPositionAndRotation(mc.player);

        headYaw = mc.player.headYaw;
        bodyYaw = mc.player.bodyYaw;

        Byte playerModel = mc.player.getDataTracker().get(PlayerEntity.PLAYER_MODEL_PARTS);
        dataTracker.set(PlayerEntity.PLAYER_MODEL_PARTS, playerModel);

        getAttributes().setFrom(mc.player.getAttributes());

        capeX = getX();
        capeY = getY();
        capeZ = getZ();

        if (health <= 20) {
            setHealth(health);
        } else {
            setHealth(health);
            setAbsorptionAmount(health - 20);
        }

        if (copyInv) getInventory().clone(mc.player.getInventory());

        spawn();
    }
    private void spawn() {
        unsetRemoved();
        mc.world.addEntity(getId(), this);
    }

    public void despawn() {
        mc.world.removeEntity(getId(), RemovalReason.DISCARDED);
        setRemoved(RemovalReason.DISCARDED);
    }

    @Nullable
    @Override
    protected PlayerListEntry getPlayerListEntry() {
        if (cachedScoreboardEntry == null) {
            cachedScoreboardEntry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
        }

        return cachedScoreboardEntry;
    }
}
