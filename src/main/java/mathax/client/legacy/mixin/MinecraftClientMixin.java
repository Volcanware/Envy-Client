package mathax.client.legacy.mixin;

import mathax.client.legacy.MatHaxClientLegacy;
import mathax.client.legacy.events.entity.player.ItemUseCrosshairTargetEvent;
import mathax.client.legacy.events.game.GameLeftEvent;
import mathax.client.legacy.events.game.OpenScreenEvent;
import mathax.client.legacy.events.game.ResourcePacksReloadedEvent;
import mathax.client.legacy.events.game.WindowResizedEvent;
import mathax.client.legacy.events.world.TickEvent;
import mathax.client.legacy.gui.WidgetScreen;
import mathax.client.legacy.mixininterface.IMinecraftClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;

@Mixin(value = MinecraftClient.class, priority = 1001)
public abstract class MinecraftClientMixin implements IMinecraftClient {
    @Shadow public ClientWorld world;

    @Shadow protected abstract void doItemUse();

    @Shadow @Final public Mouse mouse;

    @Shadow @Final private Window window;

    @Shadow public Screen currentScreen;

    @Shadow public abstract Profiler getProfiler();

    @Unique private boolean doItemUseCalled;
    @Unique private boolean rightClick;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo info) {
        MatHaxClientLegacy.INSTANCE.onInitializeClient();
    }

    @Inject(method = "openScreen", at = @At("HEAD"), cancellable = true)
    public void setScreen(Screen screen, CallbackInfo info) {
        OpenScreenEvent.getOpenedScreen event = new OpenScreenEvent.getOpenedScreen(screen);
        MatHaxClientLegacy.EVENT_BUS.post(event);

        if (event.isCancelled()) {
            info.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "tick")
    private void onPreTick(CallbackInfo info) {
        doItemUseCalled = false;

        getProfiler().push("mathaxlegacy_pre_update");
        MatHaxClientLegacy.EVENT_BUS.post(TickEvent.Pre.get());
        getProfiler().pop();

        if (rightClick && !doItemUseCalled) doItemUse();
        rightClick = false;
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void onTick(CallbackInfo info) {
        getProfiler().push("mathaxlegacy_post_update");
        MatHaxClientLegacy.EVENT_BUS.post(TickEvent.Post.get());
        getProfiler().pop();
    }

    @Inject(method = "doItemUse", at = @At("HEAD"))
    private void onDoItemUse(CallbackInfo info) {
        doItemUseCalled = true;
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"))
    private void onDisconnect(Screen screen, CallbackInfo info) {
        if (world != null) {
            MatHaxClientLegacy.EVENT_BUS.post(GameLeftEvent.get());
        }
    }

    @Inject(method = "openScreen", at = @At("HEAD"), cancellable = true)
    private void onOpenScreen(Screen screen, CallbackInfo info) {
        if (screen instanceof WidgetScreen) screen.mouseMoved(mouse.getX() * window.getScaleFactor(), mouse.getY() * window.getScaleFactor());

        OpenScreenEvent event = OpenScreenEvent.get(screen);
        MatHaxClientLegacy.EVENT_BUS.post(event);

        if (event.isCancelled()) info.cancel();
    }

    @Redirect(method = "doItemUse", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;crosshairTarget:Lnet/minecraft/util/hit/HitResult;", ordinal = 1))
    private HitResult doItemUseMinecraftClientCrosshairTargetProxy(MinecraftClient client) {
        return MatHaxClientLegacy.EVENT_BUS.post(ItemUseCrosshairTargetEvent.get(client.crosshairTarget)).target;
    }

    @ModifyVariable(method = "reloadResources(Z)Ljava/util/concurrent/CompletableFuture;", at = @At("STORE"), ordinal = 0)
    private CompletableFuture<Void> onReloadResourcesNewCompletableFuture(CompletableFuture<Void> completableFuture) {
        completableFuture.thenRun(() -> MatHaxClientLegacy.EVENT_BUS.post(ResourcePacksReloadedEvent.get()));
        return completableFuture;
    }

    @Inject(method = "onResolutionChanged", at = @At("TAIL"))
    private void onResolutionChanged(CallbackInfo info) {
        MatHaxClientLegacy.EVENT_BUS.post(WindowResizedEvent.get());
    }

    // Interface

    @Override
    public void rightClick() {
        rightClick = true;
    }
}
