package mathax.legacy.client.mixin;

import mathax.legacy.client.MatHaxLegacy;
import mathax.legacy.client.events.world.PlaySoundEvent;
import mathax.legacy.client.mixininterface.ISoundManager;
import mathax.legacy.client.music.Music;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.sound.SoundCategory;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundSystem.class)
public abstract class SoundSystemMixin implements ISoundManager {
    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"), cancellable = true)
    private void onPlay(SoundInstance soundInstance, CallbackInfo info) {
        PlaySoundEvent event = MatHaxLegacy.EVENT_BUS.post(PlaySoundEvent.get(soundInstance));
        if (event.isCancelled()) info.cancel();
    }

    @Shadow
    protected abstract float getSoundVolume(@Nullable SoundCategory soundCategory);

    @Inject(at = @At("TAIL"), method = "updateSoundVolume(Lnet/minecraft/sound/SoundCategory;F)V")
    public void updateSoundVolume(SoundCategory category, float volume, CallbackInfo info) {
        if (category == SoundCategory.MUSIC) Music.trackScheduler.setVolume(volume);
    }

    @Override
    public float getMusicVolume() {
        return getSoundVolume(SoundCategory.MUSIC);
    }
}
