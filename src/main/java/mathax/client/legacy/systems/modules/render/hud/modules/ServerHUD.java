package mathax.client.legacy.systems.modules.render.hud.modules;

import mathax.client.legacy.systems.modules.render.hud.HUD;
import mathax.client.legacy.systems.modules.render.hud.TripleTextHUDElement;
import mathax.client.legacy.utils.Utils;

public class ServerHUD extends TripleTextHUDElement {
    public ServerHUD(HUD hud) {
        super(hud, "server", "Displays the server you're currently in.", true);
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
