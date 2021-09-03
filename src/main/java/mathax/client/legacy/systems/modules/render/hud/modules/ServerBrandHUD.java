package mathax.client.legacy.systems.modules.render.hud.modules;

import mathax.client.legacy.systems.modules.render.hud.HUD;
import mathax.client.legacy.systems.modules.render.hud.TripleTextHUDElement;
import mathax.client.legacy.utils.Utils;

public class ServerBrandHUD extends TripleTextHUDElement {
    public ServerBrandHUD(HUD hud) {
        super(hud, "server-brand", "Displays the brand of the server you're currently in.", true);
    }

    @Override
    protected String getLeft() {
        return "Server Brand: ";
    }

    @Override
    protected String getRight() {
        if (!Utils.canUpdate()) return "None";

        return mc.player.getServerBrand().replace("§a", "").replace("§b", "").replace("§c", "").replace("§d", "").replace("§e", "").replace("§1", "").replace("§2", "").replace("§3", "").replace("§4", "").replace("§5", "").replace("§6", "").replace("§7", "").replace("§8", "").replace("§9", "").replace("§k", "").replace("§l", "").replace("§m", "").replace("§n", "").replace("§o", "").replace("§r", "").replace("&a", "").replace("&b", "").replace("&c", "").replace("&d", "").replace("&e", "").replace("&1", "").replace("&2", "").replace("&3", "").replace("&4", "").replace("&5", "").replace("&6", "").replace("&7", "").replace("&8", "").replace("&9", "").replace("&k", "").replace("&l", "").replace("&m", "").replace("&n", "").replace("&o", "").replace("&r", "");
    }

    @Override
    public String getEnd() {
        return "";
    }
}
