package mathax.legacy.client.utils.entity.blink;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

import static mathax.legacy.client.utils.Utils.mc;

public class BlinkPlayerCloneEntity extends OtherClientPlayerEntity {
    public BlinkPlayerCloneEntity() {
        super(mc.world, new GameProfile(mc.player.getUuid(), mc.player.getEntityName()));

        copyPositionAndRotation(mc.player);

        headYaw = mc.player.headYaw;
        bodyYaw = mc.player.bodyYaw;

        Byte playerModel = mc.player.getDataTracker().get(PlayerEntity.PLAYER_MODEL_PARTS);
        dataTracker.set(PlayerEntity.PLAYER_MODEL_PARTS, playerModel);

        getAttributes().setFrom(mc.player.getAttributes());

        capeX = getX();
        capeY = getY();
        capeZ = getZ();

        float health = Math.round(mc.player.getHealth() + mc.player.getAbsorptionAmount());
        if (health <= 20) {
            setHealth(health);
        } else {
            setHealth(health);
            setAbsorptionAmount(health - 20);
        }

        getInventory().clone(mc.player.getInventory());
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
