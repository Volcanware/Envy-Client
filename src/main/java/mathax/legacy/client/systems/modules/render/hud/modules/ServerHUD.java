package mathax.legacy.client.systems.modules.render.hud.modules;

import mathax.legacy.client.systems.modules.render.hud.HUD;
import mathax.legacy.client.systems.modules.render.hud.TripleTextHUDElement;
import mathax.legacy.client.utils.Utils;

public class ServerHUD extends TripleTextHUDElement {
    public ServerHUD(HUD hud) {
        super(hud, "server", "Displays the server you're currently in", true);
    }

    @Override
    protected String getLeft() {
        return "Server: ";
    }

    @Override
    protected String getRight() {
        if (!Utils.canUpdate()) return "None";

        return Utils.getWorldName();
    }

    @Override
    public String getEnd() {
        return "";
    }
}
