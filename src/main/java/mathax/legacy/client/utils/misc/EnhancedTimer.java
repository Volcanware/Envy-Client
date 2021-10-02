package mathax.legacy.client.utils.misc;

public class EnhancedTimer {
    private long nanoTime;

    public EnhancedTimer() {
        this.nanoTime = -1L;
    }

    public void reset() {
        this.nanoTime = System.nanoTime();
    }

    public void setTicks(final long ticks) {
        this.nanoTime = System.nanoTime() - this.convertTicksToNano(ticks);
    }

    public void setNano(final long time) {
        this.nanoTime = System.nanoTime() - time;
    }

    public void setMicro(final long time) {
        this.nanoTime = System.nanoTime() - this.convertMicroToNano(time);
    }

    public void setMillis(final long time) {
        this.nanoTime = System.nanoTime() - this.convertMillisToNano(time);
    }

    public void setSec(final long time) {
        this.nanoTime = System.nanoTime() - this.convertSecToNano(time);
    }

    public long getTicks() {
        return this.convertNanoToTicks(this.nanoTime);
    }

    public long getNano() {
        return this.nanoTime;
    }

    public long getMicro() {
        return this.convertNanoToMicro(this.nanoTime);
    }

    public long getMillis() {
        return this.convertNanoToMillis(this.nanoTime);
    }

    public long getSec() {
        return this.convertNanoToSec(this.nanoTime);
    }

    public boolean passedTicks(final long ticks) {
        return this.passedNano(this.convertTicksToNano(ticks));
    }

    public boolean passedNano(final long time) {
        return System.nanoTime() - this.nanoTime >= time;
    }

    public boolean passedMicro(final long time) {
        return this.passedNano(this.convertMicroToNano(time));
    }

    public boolean passedMillis(final long time) {
        return this.passedNano(this.convertMillisToNano(time));
    }

    public boolean passedSec(final long time) {
        return this.passedNano(this.convertSecToNano(time));
    }

    public long convertMillisToTicks(final long time) {
        return time / 50L;
    }

    public long convertTicksToMillis(final long ticks) {
        return ticks * 50L;
    }

    public long convertNanoToTicks(final long time) {
        return this.convertMillisToTicks(this.convertNanoToMillis(time));
    }

    public long convertTicksToNano(final long ticks) {
        return this.convertMillisToNano(this.convertTicksToMillis(ticks));
    }

    public long convertSecToMillis(final long time) {
        return time * 1000L;
    }

    public long convertSecToMicro(final long time) {
        return this.convertMillisToMicro(this.convertSecToMillis(time));
    }

    public long convertSecToNano(final long time) {
        return this.convertMicroToNano(this.convertMillisToMicro(this.convertSecToMillis(time)));
    }

    public long convertMillisToMicro(final long time) {
        return time * 1000L;
    }

    public long convertMillisToNano(final long time) {
        return this.convertMicroToNano(this.convertMillisToMicro(time));
    }

    public long convertMicroToNano(final long time) {
        return time * 1000L;
    }

    public long convertNanoToMicro(final long time) {
        return time / 1000L;
    }

    public long convertNanoToMillis(final long time) {
        return this.convertMicroToMillis(this.convertNanoToMicro(time));
    }

    public long convertNanoToSec(final long time) {
        return this.convertMillisToSec(this.convertMicroToMillis(this.convertNanoToMicro(time)));
    }

    public long convertMicroToMillis(final long time) {
        return time / 1000L;
    }

    public long convertMicroToSec(final long time) {
        return this.convertMillisToSec(this.convertMicroToMillis(time));
    }

    public long convertMillisToSec(final long time) {
        return time / 1000L;
    }
}
