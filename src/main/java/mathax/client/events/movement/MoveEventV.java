package mathax.client.events.Volcan;

public class MoveEventV {
    private double x, y, z;

    public MoveEventV(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }


    public double getX() {
        return x;
    }


    public void setX(double x) {
        this.x = x;
    }


    public double getY() {
        return y;
    }


    public void setY(double y) {
        this.y = y;
    }


    public double getZ() {
        return z;
    }


    public void setZ(double z) {
        this.z = z;
    }

}
