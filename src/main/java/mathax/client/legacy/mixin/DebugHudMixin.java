package mathax.client.legacy.mixin;

import mathax.client.legacy.MatHaxClientLegacy;
import mathax.client.legacy.systems.config.Config;
import mathax.client.legacy.utils.Utils;
import mathax.client.legacy.utils.misc.Version;
import mathax.client.legacy.utils.network.Http;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugHud.class)
public class DebugHudMixin {
    @Inject(at = @At("RETURN"), method = "getLeftText")
    protected void getLeftText(CallbackInfoReturnable<List<String>> info) {
        String line = "§cMatHax Client Legacy §7" + MatHaxClientLegacy.clientVersionWithV + getNewUpdate();
        info.getReturnValue().add(line);
    }

    public String getNewUpdate() {
        if (Utils.didntCheckForLatestVersion) {
            Utils.didntCheckForLatestVersion = false;
            String apiLatestVer = Http.get(MatHaxClientLegacy.URL + "Version/Legacy/1-17-1").sendString();
            String processedApiLatestVer = apiLatestVer.replace("\n", "");
            if (processedApiLatestVer == null) {
                return " [Could not get Latest Version]";
            }

            Version latestVer = new Version(processedApiLatestVer);

            if (latestVer.isHigherThan(Config.get().version)) {
                return " [Outdated | Latest Version: " + latestVer + "]";
            } else {
                return "";
            }
        }
        return "";
    }
}
