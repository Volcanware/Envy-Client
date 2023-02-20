package mathax.client.systems.modules.movement.speed;

import mathax.client.systems.modules.movement.speed.modes.LBL_SlowHop;
import mathax.client.systems.modules.movement.speed.modes.Weird;
import mathax.client.systems.modules.movement.speed.modes.VelocityHop;

public enum SpeedModes {
    Vanilla("Vanilla"),
    Strafe("BunnyHop"),
    MineBerry("MineBerry"),
    VelocityHop("Yport"),
    Weird("Please Don't Crash me :("),
    LBL_SlowHop("SlowHop"),
    Vulcan("Vulcan");

    private final String title;

    SpeedModes(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
