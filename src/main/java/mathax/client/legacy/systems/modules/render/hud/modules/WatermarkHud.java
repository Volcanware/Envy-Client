package mathax.client.legacy.systems.modules.render.hud.modules;

import mathax.client.legacy.MatHaxClientLegacy;
import mathax.client.legacy.bus.EventHandler;
import mathax.client.legacy.events.game.GameJoinedEvent;
import mathax.client.legacy.systems.modules.render.hud.HUD;
import mathax.client.legacy.utils.Utils;
import mathax.client.legacy.utils.misc.Version;
import mathax.client.legacy.utils.network.Http;

import java.util.Arrays;

public class WatermarkHud extends TripleTextHudElement {
    private String newUpdateString = "";

    public WatermarkHud(HUD hud) {
        super(hud, "watermark", "Displays a MatHax Client Legacy watermark.");
    }

    @Override
    protected String getLeft() {
        return "MatHax Client Legacy ";
    }

    @Override
    protected String getRight() {
        return MatHaxClientLegacy.clientVersionWithV + getNewUpdate();
    }

    @Override
    public String getEnd() {
        return "";
    }

    public String getNewUpdate() {
        if (Utils.didntCheckForLatestVersion) {
            Utils.didntCheckForLatestVersion = false;
            String apiLatestVer = Http.get(MatHaxClientLegacy.URL + "Version/Legacy/1-17-1").sendString();
            String processedApiLatestVer = apiLatestVer.replace("\n", "");
            if (processedApiLatestVer == null) {
                newUpdateString = " [Could not get Latest Version]";
            } else {
                Version latestVer = new Version(processedApiLatestVer);
                Version currentVer = new Version(MatHaxClientLegacy.versionNumber);
                if (latestVer.isHigherThan(currentVer)) {
                    newUpdateString = " [Outdated | Latest Version: v" + latestVer + "]";
                } else {
                    newUpdateString = "";
                }
            }
        }
        return newUpdateString;
    }
}
