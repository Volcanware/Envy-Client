package mathax.legacy.client.systems.modules.render.hud.modules;

import mathax.legacy.client.systems.modules.render.hud.HUD;
import mathax.legacy.client.systems.modules.render.hud.TripleTextHUDElement;
import net.minecraft.client.network.PlayerListEntry;

public class PingHUD extends TripleTextHUDElement {
    public PingHUD(HUD hud) {
        super(hud, "ping", "Displays your ping", true);
    }

    @Override
    protected String getLeft() {
        return "Ping: ";
    }

    @Override
    protected String getRight() {
        if (isInEditor()) return "0";

        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());

        if (playerListEntry != null) return Integer.toString(playerListEntry.getLatency());
        return "0";
    }

    @Override
    public String getEnd() {
        return "";
    }
}
