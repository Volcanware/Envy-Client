package mathax.legacy.client.mixin;

import mathax.legacy.client.MatHaxLegacy;
import mathax.legacy.client.Version;
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
    private String versionString = "";

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
            switch (Version.checkLatest()) {
                case 0:
                    versionString = " [Could not get Latest Version]";
                case 1:
                    versionString = " [Outdated | Latest Version: v" + Version.getLatest() + "]";
                case 2:
                    versionString = "";
            }
        }
        return versionString;
    }
}
