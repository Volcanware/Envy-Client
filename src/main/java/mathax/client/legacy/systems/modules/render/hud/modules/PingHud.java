package mathax.client.legacy.systems.modules.render.hud.modules;

import mathax.client.legacy.systems.modules.render.hud.HUD;
import net.minecraft.client.network.PlayerListEntry;

public class PingHud extends TripleTextHudElement {
    public PingHud(HUD hud) {
        super(hud, "ping", "Displays your ping.", "Ping: ");
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
