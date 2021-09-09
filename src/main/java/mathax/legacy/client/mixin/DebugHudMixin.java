package mathax.legacy.client.mixin;

import mathax.legacy.client.MatHaxLegacy;
import mathax.legacy.client.systems.config.Config;
import mathax.legacy.client.utils.Utils;
import mathax.legacy.client.Version;
import mathax.legacy.client.utils.network.Http;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugHud.class)
public class DebugHudMixin {
    @Inject(at = @At("RETURN"), method = "getLeftText")
    protected void getLeftText(CallbackInfoReturnable<List<String>> info) {
        LiteralText mathax = new LiteralText("MatHax Legacy");
        mathax.setStyle(mathax.getStyle().withColor(MatHaxLegacy.INSTANCE.MATHAX_COLOR.getPacked()));
        String line = mathax + " " + Formatting.GRAY + Version.getStylized() + getNewUpdate();
        info.getReturnValue().add(line);
    }

    public String getNewUpdate() {
        if (!Version.checkedForLatest) {
            Version.checkedForLatest = true;
            String apiLatestVer = Http.get(MatHaxLegacy.API_URL + "Version/Legacy/1-17-1").sendString().replace("\n", "");;
            if (apiLatestVer == null) {
                return " [Could not get Latest Version]";
            }

            Version latestVer = new Version(apiLatestVer);

            if (latestVer.isHigherThan(Config.get().version)) {
                return " [Outdated | Latest Version: " + latestVer + "]";
            } else {
                return "";
            }
        }
        return "";
    }
}
