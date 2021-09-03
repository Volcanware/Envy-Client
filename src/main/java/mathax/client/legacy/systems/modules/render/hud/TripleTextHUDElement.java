package mathax.client.legacy.systems.modules.render.hud;

import mathax.client.legacy.utils.render.color.Color;

public abstract class TripleTextHUDElement extends HUDElement {
    protected Color rightColor;
    protected boolean visible = true;

    private String left;
    private String right;
    private String end;

    private double leftWidth;
    private double rightWidth;

    public TripleTextHUDElement(HUD hud, String name, String description, boolean defaultActive) {
        super(hud, name, description, defaultActive);
        this.rightColor = hud.secondaryColor.get();
    }

    @Override
    public void update(HUDRenderer renderer) {
        left = getLeft();
        right = getRight();
        end = getEnd();
        leftWidth = renderer.textWidth(left);
        rightWidth = renderer.textWidth(right);

        double textWidth = leftWidth + renderer.textWidth(right);

        box.setSize(textWidth + renderer.textWidth(end), renderer.textHeight());
    }

    @Override
    public void render(HUDRenderer renderer) {
        if (!visible) return;

        double x = box.getX();
        double y = box.getY();

        renderer.text(left, x, y, hud.primaryColor.get());
        renderer.text(right, x + leftWidth, y, rightColor);
        renderer.text(end, x + leftWidth + rightWidth, y, hud.primaryColor.get());
    }

    protected void setLeft(String left) {
        this.left = left;
        this.leftWidth = 0;
    }

    protected abstract String getLeft();
    protected abstract String getRight();
    protected abstract String getEnd();
}
