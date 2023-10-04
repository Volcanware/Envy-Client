package mathax.client.mixin;

import mathax.client.systems.modules.Modules;
import mathax.client.systems.modules.misc.InventoryTweaks;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ShulkerBoxScreen.class)
public abstract class ShulkerBoxScreenMixin extends HandledScreen<ShulkerBoxScreenHandler> {
    public ShulkerBoxScreenMixin(ShulkerBoxScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();

        InventoryTweaks invTweaks = Modules.get().get(InventoryTweaks.class);

        if (invTweaks.isActive() && invTweaks.showButtons()) {
            addDrawableChild(new ButtonWidget.Builder(
                    Text.literal("Steal"),
                    button -> invTweaks.steal(handler))
                .dimensions(
                    x + backgroundWidth - 88,
                    y + 3,
                    40,
                    12)
                .build()
            );

            addDrawableChild(new ButtonWidget.Builder(
                    Text.literal("Dump"),
                    button -> invTweaks.dump(handler))
                .dimensions(
                    x + backgroundWidth - 46,
                    y + 3,
                    40,
                    12)
                .build()
            );
        }

        if (invTweaks.autoSteal()) invTweaks.steal(handler);
        if (invTweaks.autoDump()) invTweaks.dump(handler);
    }
}
