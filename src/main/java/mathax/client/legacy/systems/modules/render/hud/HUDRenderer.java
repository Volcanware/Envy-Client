package mathax.client.legacy.systems.modules.render.hud;

import mathax.client.legacy.renderer.text.TextRenderer;
import mathax.client.legacy.utils.render.color.Color;

import java.util.ArrayList;
import java.util.List;

public class HUDRenderer {
    public double delta;
    private final List<Runnable> postTasks = new ArrayList<>();

    public void begin(double scale, double tickDelta, boolean scaleOnly) {
        TextRenderer.get().begin(scale, scaleOnly, false);

        this.delta = tickDelta;
    }

    public void end() {
        TextRenderer.get().end();

        for (Runnable runnable : postTasks) {
            runnable.run();
        }

        postTasks.clear();
    }

    public void text(String text, double x, double y, Color color) {
        TextRenderer.get().render(text, x, y, color, true);
    }

    public double textWidth(String text) {
        return TextRenderer.get().getWidth(text);
    }

    public double textHeight() {
        return TextRenderer.get().getHeight();
    }

    public void addPostTask(Runnable runnable) {
        postTasks.add(runnable);
    }
}
