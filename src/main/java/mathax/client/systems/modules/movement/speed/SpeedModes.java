package mathax.client.systems.modules.movement.speed;

import mathax.client.systems.modules.movement.speed.modes.VelocityHop;

public enum SpeedModes {
    Vanilla("Vanilla"),
    Strafe("Strafe"),
    MineBerry("MineBerry"),
    VelocityHop("VelocityHop");

    private final String title;

    SpeedModes(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
