package mathax.client.systems.modules.movement.speed;

import mathax.client.systems.modules.movement.speed.modes.LBL_SlowHop;
import mathax.client.systems.modules.movement.speed.modes.Weird;
import mathax.client.systems.modules.movement.speed.modes.VelocityHop;

public enum SpeedModes {
    Vanilla("Vanilla"),
    Strafe("BunnyHop"),
    MineBerry("Mineberry"),
    VelocityHop("Yport"),
    TimerHop("TimerHop"),
    Weird("Please Don't Crash me :("),
    LBL_SlowHop("SlowHop"),
    Vulcan("Vulcan"),
    OnGround("RubberBand"),
    LegitHop("LegitHop"),
    EnvyAnarchy("EnvyAnarchy || Legacy"),
    Test3("FastHop"),
    dumbspeed("dumbspeed"),
    NONONOFUCK("GlideHop"),
    Chinese("Chinese"),
    EnvyHop("EnvyHop"),
    ChonkyChineseSped("experimental-speed"),

    Custom("Custom");



    private final String title;

    SpeedModes(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
