package envy.client.events.game;

import envy.client.events.Event;

public class EventUpdate extends Event {

    private static final EventUpdate instance = new EventUpdate();

    public static EventUpdate get() {
        instance.setCancelled(false);
        return instance;
    }
}
