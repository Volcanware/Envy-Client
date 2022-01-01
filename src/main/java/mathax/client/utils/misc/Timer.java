package mathax.client.utils.misc;

public class Timer {
    private long nanoTime;

    public Timer() {
        nanoTime = -1L;
    }

    public void reset() {
        nanoTime = System.nanoTime();
    }

    public void setTicks(final long ticks) {
        nanoTime = System.nanoTime() - convertTicksToNano(ticks);
    }

    public void setNano(final long time) {
        nanoTime = System.nanoTime() - time;
    }

    public void setMicro(final long time) {
        nanoTime = System.nanoTime() - convertMicroToNano(time);
    }

    public void setMillis(final long time) {
        nanoTime = System.nanoTime() - convertMillisToNano(time);
    }

    public void setSec(final long time) {
        nanoTime = System.nanoTime() - convertSecToNano(time);
    }

    public long getTicks() {
        return convertNanoToTicks(nanoTime);
    }

    public long getNano() {
        return nanoTime;
    }

    public long getMicro() {
        return convertNanoToMicro(nanoTime);
    }

    public long getMillis() {
        return convertNanoToMillis(nanoTime);
    }

    public long getSec() {
        return convertNanoToSec(nanoTime);
    }

    public boolean passedTicks(final long ticks) {
        return passedNano(convertTicksToNano(ticks));
    }

    public boolean passedNano(final long time) {
        return System.nanoTime() - nanoTime >= time;
    }

    public boolean passedMicro(final long time) {
        return passedNano(convertMicroToNano(time));
    }

    public boolean passedMillis(final long time) {
        return passedNano(convertMillisToNano(time));
    }

    public boolean passedSec(final long time) {
        return passedNano(convertSecToNano(time));
    }

    public long convertMillisToTicks(final long time) {
        return time / 50L;
    }

    public long convertTicksToMillis(final long ticks) {
        return ticks * 50L;
    }

    public long convertNanoToTicks(final long time) {
        return convertMillisToTicks(convertNanoToMillis(time));
    }

    public long convertTicksToNano(final long ticks) {
        return convertMillisToNano(convertTicksToMillis(ticks));
    }

    public long convertSecToMillis(final long time) {
        return time * 1000L;
    }

    public long convertSecToMicro(final long time) {
        return convertMillisToMicro(convertSecToMillis(time));
    }

    public long convertSecToNano(final long time) {
        return convertMicroToNano(convertMillisToMicro(convertSecToMillis(time)));
    }

    public long convertMillisToMicro(final long time) {
        return time * 1000L;
    }

    public long convertMillisToNano(final long time) {
        return convertMicroToNano(convertMillisToMicro(time));
    }

    public long convertMicroToNano(final long time) {
        return time * 1000L;
    }

    public long convertNanoToMicro(final long time) {
        return time / 1000L;
    }

    public long convertNanoToMillis(final long time) {
        return convertMicroToMillis(convertNanoToMicro(time));
    }

    public long convertNanoToSec(final long time) {
        return convertMillisToSec(convertMicroToMillis(convertNanoToMicro(time)));
    }

    public long convertMicroToMillis(final long time) {
        return time / 1000L;
    }

    public long convertMicroToSec(final long time) {
        return convertMillisToSec(convertMicroToMillis(time));
    }

    public long convertMillisToSec(final long time) {
        return time / 1000L;
    }
}
