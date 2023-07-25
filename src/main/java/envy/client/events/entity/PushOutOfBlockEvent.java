package envy.client.events.entity;

import envy.client.events.Cancellable;

public class PushOutOfBlockEvent extends Cancellable {
    private static final PushOutOfBlockEvent INSTANCE = new PushOutOfBlockEvent();

    public double x;
    public double d;

    public static PushOutOfBlockEvent get(double x, double d) {
        INSTANCE.setCancelled(false);
        INSTANCE.x = x;
        INSTANCE.d = d;
        return INSTANCE;
    }
}
