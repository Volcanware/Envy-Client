package mathax.client.mixin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import mathax.client.systems.commands.Commands;
import mathax.client.systems.config.Config;
import mathax.client.systems.modules.Modules;
import mathax.client.systems.modules.render.NoRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.command.CommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.concurrent.CompletableFuture;

@Mixin(CommandSuggestor.class)
public abstract class CommandSuggestorMixin {
    @Shadow
    private ParseResults<CommandSource> parse;

    @Shadow
    @Final
    TextFieldWidget textField;

    @Shadow
    @Final
    MinecraftClient client;

    @Shadow
    boolean completingSuggestions;

    @Shadow
    private CompletableFuture<Suggestions> pendingSuggestions;

    @Shadow
    protected abstract void show();

    @Shadow
    CommandSuggestor.SuggestionWindow window;

    @Inject(method = "refresh", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/StringReader;canRead()Z", remap = false), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    public void onRefresh(CallbackInfo info, String string, StringReader reader) {
        String prefix = Config.get().prefix.get();
        int length = prefix.length();
        if (reader.canRead(length) && reader.getString().startsWith(prefix, reader.getCursor())) {
            reader.setCursor(reader.getCursor() + length);
            assert this.client.player != null;
            // Pretty much copy&paste from the refresh method
            CommandDispatcher<CommandSource> commandDispatcher = Commands.get().getDispatcher();
            if (this.parse == null) this.parse = commandDispatcher.parse(reader, Commands.get().getCommandSource());

            int cursor = textField.getCursor();
            if (cursor >= 1 && (this.window == null || !this.completingSuggestions)) {
                this.pendingSuggestions = commandDispatcher.getCompletionSuggestions(this.parse, cursor);
                this.pendingSuggestions.thenRun(() -> {
                    if (this.pendingSuggestions.isDone()) this.show();
                });
            }

            info.cancel();
        }
    }

    @Inject(method = "render", at = @At(value = "HEAD"), cancellable = true)
    public void onRenderCommandSuggestion(MatrixStack matrices, int mouseX, int mouseY, CallbackInfo info) {
        if (Modules.get().get(NoRender.class).noCommandSuggestions.get()) info.cancel();
    }
}
