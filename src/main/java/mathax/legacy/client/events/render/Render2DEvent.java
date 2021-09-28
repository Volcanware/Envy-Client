package mathax.legacy.client.events.render;

import net.minecraft.client.util.math.MatrixStack;

public class Render2DEvent {
    private static final Render2DEvent INSTANCE = new Render2DEvent();

    public MatrixStack matrixStack;
    public int screenWidth, screenHeight;
    public float tickDelta;

    public static Render2DEvent get(int screenWidth, int screenHeight, float tickDelta, MatrixStack matrixStack) {
        INSTANCE.screenWidth = screenWidth;
        INSTANCE.screenHeight = screenHeight;
        INSTANCE.tickDelta = tickDelta;
        INSTANCE.matrixStack = matrixStack;
        return INSTANCE;
    }
}
