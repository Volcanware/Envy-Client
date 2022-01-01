package mathax.client.mixin;

import mathax.client.systems.modules.Modules;
import mathax.client.utils.Utils;
import mathax.client.utils.tooltip.MatHaxTooltipData;
import mathax.client.systems.modules.render.NoRender;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Inject(method = "renderBackground(Lnet/minecraft/client/util/math/MatrixStack;)V", at = @At("HEAD"), cancellable = true)
    private void onRenderBackground(CallbackInfo info) {
        if (Utils.canUpdate() && Modules.get().get(NoRender.class).noGUIBackground()) info.cancel();
    }

    @Inject(method = "method_32635", at = @At("HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private static void onComponentConstruct(List<TooltipComponent> list, TooltipData data, CallbackInfo info) {
        if (data instanceof MatHaxTooltipData) {
            list.add(((MatHaxTooltipData) data).getComponent());
            info.cancel();
        }
    }
}
