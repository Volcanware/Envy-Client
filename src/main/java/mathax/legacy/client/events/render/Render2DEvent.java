package mathax.legacy.client.events.render;

import net.minecraft.client.util.math.MatrixStack;

public class Render2DEvent {
    private static final Render2DEvent INSTANCE = new Render2DEvent();

    public int screenWidth, screenHeight;
    public float tickDelta;
    public MatrixStack matrices;

    public static Render2DEvent get(int screenWidth, int screenHeight, float tickDelta, MatrixStack matrices) {
        INSTANCE.screenWidth = screenWidth;
        INSTANCE.screenHeight = screenHeight;
        INSTANCE.tickDelta = tickDelta;
        INSTANCE.matrices = matrices;
        return INSTANCE;
    }
}
