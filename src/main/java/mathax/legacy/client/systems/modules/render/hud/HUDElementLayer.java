package mathax.legacy.client.systems.modules.render.hud;

import mathax.legacy.client.utils.render.AlignmentX;
import mathax.legacy.client.utils.render.AlignmentY;

import java.util.ArrayList;
import java.util.List;

public class HUDElementLayer {
    private final HUDRenderer renderer;
    private final List<HUDElement> allElements;
    private final List<HUDElement> elements;

    private final AlignmentX xAlign;
    private final AlignmentY yAlign;

    private final int xOffset, yOffset;

    public HUDElementLayer(HUDRenderer renderer, List<HUDElement> allElements, AlignmentX xAlign, AlignmentY yAlign, int xOffset, int yOffset) {
        this.renderer = renderer;
        this.allElements = allElements;
        this.elements = new ArrayList<>();
        this.xAlign = xAlign;
        this.yAlign = yAlign;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public void add(HUDElement element) {
        allElements.add(element);
        elements.add(element);

        element.settings.registerColorSettings(null);
    }

    public void align() {
        double x = xOffset * (xAlign == AlignmentX.Right ? -1 : 1);
        double y = yOffset * (yAlign == AlignmentY.Bottom ? -1 : 1);

        for (HUDElement element : elements) {
            element.update(renderer);

            element.box.x = xAlign;
            element.box.y = yAlign;
            element.box.xOffset = (int) Math.round(x);
            element.box.yOffset = (int) Math.round(y);

            if (yAlign == AlignmentY.Bottom) y -= element.box.height;
            else y += element.box.height;
        }
    }
}
