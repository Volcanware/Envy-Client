package envy.client.systems.hud;

import envy.client.utils.Utils;
import envy.client.utils.misc.ISerializable;
import envy.client.utils.render.AlignmentX;
import envy.client.utils.render.AlignmentY;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtCompound;

public class BoundingBox implements ISerializable<BoundingBox> {
    public AlignmentX x = AlignmentX.Left;
    public AlignmentY y = AlignmentY.Top;

    public double xOffset, yOffset;
    public double width, height;

    public double alignX(double width) {
        return switch (this.x) {
            case Left -> 0;
            case Center -> this.width / 2.0 - width / 2.0;
            case Right -> this.width - width;
        };
    }

    public void addPos(double deltaX, double deltaY) {
        xOffset += (deltaX);
        yOffset += (deltaY);

        double xPos = getX();
        double yPos = getY();

        switch (x) {
            case Left -> {
                double c = Utils.getWindowWidth() / 3.0;

                if (xPos >= c - width / 2.0) {
                    x = AlignmentX.Center;
                    xOffset = (-c / 2.0 + xPos - c + width / 2.0);
                }
            }
            case Center -> {
                double c = Utils.getWindowWidth() / 3.0;
                double cRight = Utils.getWindowWidth() / 3.0 * 2;

                if (xPos > cRight - width / 2.0) {
                    x = AlignmentX.Right;
                    xOffset = (-(c - width) + (c - (Utils.getWindowWidth() - xPos)));
                } else if (xPos < c - width / 2.0) {
                    x = AlignmentX.Left;
                    xOffset = (xPos);
                }
            }
            case Right -> {
                double c = Utils.getWindowWidth() / 3.0;
                double cLeft = Utils.getWindowWidth() / 3.0 * 2;

                if (xPos <= cLeft - width / 2.0) {
                    x = AlignmentX.Center;
                    xOffset = (-c / 2.0 + xPos - c + width / 2.0);
                }
            }
        }

        if (x == AlignmentX.Left && xOffset < 0) xOffset = 0;
        else if (x == AlignmentX.Right && xOffset > 0) xOffset = 0;

        switch (y) {
            case Top -> {
                double c = Utils.getWindowHeight() / 3.0;

                if (yPos >= c - height / 2.0) {
                    y = AlignmentY.Center;
                    yOffset = (-c / 2.0 + yPos - c + height / 2.0);
                }
            }
            case Center -> {
                double c = Utils.getWindowHeight() / 3.0;
                double cBottom = Utils.getWindowHeight() / 3.0 * 2;

                if (yPos > cBottom - height / 2.0) {
                    y = AlignmentY.Bottom;
                    yOffset = (-(c - height) + (c - (Utils.getWindowHeight() - yPos)));
                } else if (yPos < c - height / 2.0) {
                    y = AlignmentY.Top;
                    yOffset = (yPos);
                }
            }
            case Bottom -> {
                double c = Utils.getWindowHeight() / 3.0;
                double cLeft = Utils.getWindowHeight() / 3.0 * 2;

                if (yPos <= cLeft - height / 2.0) {
                    y = AlignmentY.Center;
                    yOffset = (-c / 2.0 + yPos - c + height / 2.0);
                }
            }
        }

        if (y == AlignmentY.Top && yOffset < 0) yOffset = 0;
        else if (y == AlignmentY.Bottom && yOffset > 0) yOffset = 0;
    }

    public void setSize(double width, double height) {
        this.width = (width);
        this.height = (height);
    }

    public double getX() {
        return switch (x) {
            case Left -> xOffset;
            case Center -> (Utils.getWindowWidth() / 2.0 - width / 2.0 + xOffset);
            case Right -> Utils.getWindowWidth() - width + xOffset;
        };
    }

    public void setX(int x) {
        addPos(x - getX(), 0);
    }

    public double getY() {
        return switch (y) {
            case Top -> yOffset;
            case Center -> (Utils.getWindowHeight() / 2.0 - height / 2.0 + yOffset);
            case Bottom -> Utils.getWindowHeight() - height + yOffset;
        };
    }

    public void setY(int y) {
        addPos(0, y - getY());
    }

    public boolean isOver(double x, double y) {
        double sx = getX();
        double sy = getY();

        return x >= sx && x <= sx + width && y >= sy && y <= sy + height;
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = new NbtCompound();

        tag.putString("x", x.name());
        tag.putString("y", y.name());
        tag.putDouble("xOffset", xOffset);
        tag.putDouble("yOffset", yOffset);

        return tag;
    }

    @Override
    public BoundingBox fromTag(NbtCompound tag) {
        x = AlignmentX.valueOf(tag.getString("x"));
        y = AlignmentY.valueOf(tag.getString("y"));

        xOffset = ((AbstractNbtNumber) tag.get("xOffset")).doubleValue();
        yOffset = ((AbstractNbtNumber) tag.get("yOffset")).doubleValue();

        return this;
    }
}
