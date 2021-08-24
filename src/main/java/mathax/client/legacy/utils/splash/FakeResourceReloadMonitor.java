package mathax.client.legacy.utils.splash;

import net.minecraft.resource.ResourceReload;
import net.minecraft.util.Unit;
import net.minecraft.util.math.MathHelper;

import java.util.concurrent.CompletableFuture;

/** ReesourceReloadMonitor that automatically completes after some time */
public class FakeResourceReloadMonitor implements ResourceReload {

    protected final long start;
    protected final long duration;

    public FakeResourceReloadMonitor(long durationMs) {
        start = System.currentTimeMillis();
        duration = durationMs;
    }

    @Override
    public CompletableFuture<Unit> whenComplete() {
        return null;
    }

    @Override
    public float getProgress() {
        return MathHelper.clamp(
            (float) (System.currentTimeMillis() - start) / duration, 0, 1
        );
    }

    @Override
    public boolean isComplete() {
        return System.currentTimeMillis() - start >= duration;
    }

    @Override
    public void throwException() {
    }
}
