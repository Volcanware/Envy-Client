package mathax.client.utils.entity.fakeplayer;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static mathax.client.MatHax.mc;

public class FakePlayerEntity extends OtherClientPlayerEntity {
    public boolean doNotPush, hideWhenInsideCamera;

    public FakePlayerEntity(PlayerEntity player, String name, float health, boolean copyInv) {
        super(mc.world, player.getGameProfile());

        copyPositionAndRotation(player);

        prevYaw = getYaw();
        prevPitch = getPitch();
        headYaw = player.headYaw;
        prevHeadYaw = headYaw;
        bodyYaw = player.bodyYaw;
        prevBodyYaw = bodyYaw;

        Byte playerModel = player.getDataTracker().get(PlayerEntity.PLAYER_MODEL_PARTS);
        dataTracker.set(PlayerEntity.PLAYER_MODEL_PARTS, playerModel);

        getAttributes().setFrom(player.getAttributes());
        setPose(player.getPose());

        capeX = getX();
        capeY = getY();
        capeZ = getZ();

        if (health <= 20) setHealth(health);
        else {
            setHealth(health);
            setAbsorptionAmount(health - 20);
        }

        if (copyInv) getInventory().clone(player.getInventory());
    }

    public void spawn() {
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
        if (playerListEntry == null) {
            playerListEntry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
        }

        return playerListEntry;
    }
}
