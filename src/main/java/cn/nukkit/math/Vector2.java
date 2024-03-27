package cn.nukkit.math;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Vector2 implements Cloneable {
    public static final Vector2 ZERO = new Vector2(0, 0);

    public final double x;
    public final double y;

    public Vector2() {
        this(0, 0);
    }

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public int getFloorX() {
        return Mth.floor(this.x);
    }

    public int getFloorY() {
        return Mth.floor(this.y);
    }

    public Vector2 add(double n) {
        return this.add(n, n);
    }

    public Vector2 add(double x, double y) {
        return new Vector2(this.x + x, this.y + y);
    }

    public Vector2 add(Vector2 x) {
        return this.add(x.getX(), x.getY());
    }

    public Vector2 subtract(double n) {
        return this.subtract(n, n);
    }

    public Vector2 subtract(double x, double y) {
        return this.add(-x, -y);
    }

    public Vector2 subtract(Vector2 x) {
        return this.add(-x.getX(), -x.getY());
    }

    public Vector2 ceil() {
        return new Vector2((int) (this.x + 1), (int) (this.y + 1));
    }

    public Vector2 floor() {
        return new Vector2(Mth.floor(this.x), Mth.floor(this.y));
    }

    public Vector2 round() {
        return new Vector2(Math.round(this.x), Math.round(this.y));
    }

    public Vector2 abs() {
        return new Vector2(Math.abs(this.x), Math.abs(this.y));
    }

    public Vector2 multiply(double number) {
        return new Vector2(this.x * number, this.y * number);
    }

    public Vector2 multiply(double x, double y) {
        return new Vector2(this.x * x, this.y * y);
    }

    public Vector2 multiply(Vector2 vec) {
        return new Vector2(this.x * vec.getX(), this.y * vec.getY());
    }

    public Vector2 divide(double number) {
        return new Vector2(this.x / number, this.y / number);
    }

    public Vector2 divide(Vector2 vec) {
        return new Vector2(this.x / vec.getX(), this.y / vec.getY());
    }

    public double distance(double x, double y) {
        return Math.sqrt(this.distanceSquared(x, y));
    }

    public double distance(Vector2 vector) {
        return Math.sqrt(this.distanceSquared(vector.getX(), vector.getY()));
    }

    public double distanceSquared(double x, double y) {
        double xDiff = this.x - x;
        double yDiff = this.y - y;
        return xDiff * xDiff + yDiff * yDiff;
    }

    public double distanceSquared(Vector2 vector) {
        return this.distanceSquared(vector.getX(), vector.getY());
    }

    public double length() {
        return Math.sqrt(this.lengthSquared());
    }

    public double lengthSquared() {
        return this.x * this.x + this.y * this.y;
    }

    public Vector2 normalize() {
        double len = this.lengthSquared();
        if (len != 0) {
            return this.divide(Math.sqrt(len));
        }
        return new Vector2(0, 0);
    }

    public double dot(Vector2 v) {
        return this.x * v.x + this.y * v.y;
    }

    @Override
    public String toString() {
        return "Vector2(x=" + this.x + ",y=" + this.y + ")";
    }

    public String toShortString() {
        return this.x + "," + this.y;
    }

    public String debugText() {
        return "(" + NukkitMath.round(x, 2) + "," + NukkitMath.round(y, 2) + ")";
    }

    public final Vector2 copyVec() {
        return new Vector2(x, y);
    }

    public final boolean equalsVec(Vector2 vec) {
        if (vec == null) {
            return false;
        }
        return Double.compare(vec.x, this.x) == 0
                && Double.compare(vec.y, this.y) == 0;
    }

    public Vector2f asVector2f() {
        return new Vector2f((float) this.x, (float) this.y);
    }

    @Override
    public Vector2 clone() {
        try {
            return (Vector2) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector2 vector2 = (Vector2) o;
        return Double.compare(vector2.x, x) == 0 &&
                Double.compare(vector2.y, y) == 0;
    }

    @Override
    public int hashCode() {
        int hash = Double.hashCode(this.x);
        hash = 31 * hash + Double.hashCode(this.y);
        return hash;
    }
}