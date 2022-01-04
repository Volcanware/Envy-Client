package mathax.client.systems.modules.movement.speed;

public enum SpeedModes {
    Vanilla("Vanilla"),
    Strafe("Strafe");

    private final String title;

    SpeedModes(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
