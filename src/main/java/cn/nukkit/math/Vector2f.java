package cn.nukkit.math;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Vector2f implements Cloneable {
    public static final Vector2f ZERO = new Vector2f(0, 0);

    public final float x;
    public final float y;

    public Vector2f() {
        this(0, 0);
    }

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public int getFloorX() {
        return Mth.floor(this.x);
    }

    public int getFloorY() {
        return Mth.floor(this.y);
    }

    public Vector2f add(float n) {
        return this.add(n, n);
    }

    public Vector2f add(float x, float y) {
        return new Vector2f(this.x + x, this.y + y);
    }

    public Vector2f add(Vector2f x) {
        return this.add(x.getX(), x.getY());
    }

    public Vector2f subtract(float n) {
        return this.subtract(n, n);
    }

    public Vector2f subtract(float x, float y) {
        return this.add(-x, -y);
    }

    public Vector2f subtract(Vector2f x) {
        return this.add(-x.getX(), -x.getY());
    }

    public Vector2f ceil() {
        return new Vector2f((int) (this.x + 1), (int) (this.y + 1));
    }

    public Vector2f floor() {
        return new Vector2f(this.getFloorX(), this.getFloorY());
    }

    public Vector2f round() {
        return new Vector2f(Math.round(this.x), Math.round(this.y));
    }

    public Vector2f abs() {
        return new Vector2f(Math.abs(this.x), Math.abs(this.y));
    }

    public Vector2f multiply(float number) {
        return new Vector2f(this.x * number, this.y * number);
    }

    public Vector2f multiply(float x, float y) {
        return new Vector2f(this.x * x, this.y * y);
    }

    public Vector2f multiply(Vector2f vec) {
        return new Vector2f(this.x * vec.getX(), this.y * vec.getY());
    }

    public Vector2f divide(float number) {
        return new Vector2f(this.x / number, this.y / number);
    }

    public Vector2f divide(Vector2f vec) {
        return new Vector2f(this.x / vec.getX(), this.y / vec.getY());
    }

    public double distance(float x, float y) {
        return Math.sqrt(this.distanceSquared(x, y));
    }

    public double distance(Vector2f vector) {
        return Math.sqrt(this.distanceSquared(vector.getX(), vector.getY()));
    }

    public double distanceSquared(float x, float y) {
        double xDiff = this.x - x;
        double yDiff = this.y - y;
        return xDiff * xDiff + yDiff * yDiff;
    }

    public double distanceSquared(Vector2f vector) {
        return this.distanceSquared(vector.getX(), vector.getY());
    }

    public double length() {
        return Math.sqrt(this.lengthSquared());
    }

    public float lengthSquared() {
        return this.x * this.x + this.y * this.y;
    }

    public Vector2f normalize() {
        float len = this.lengthSquared();
        if (len != 0) {
            return this.divide((float) Math.sqrt(len));
        }
        return new Vector2f(0, 0);
    }

    public float dot(Vector2f v) {
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

    public final Vector2f copyVec() {
        return new Vector2f(x, y);
    }

    public final boolean equalsVec(Vector2f vec) {
        if (vec == null) {
            return false;
        }
        return Float.compare(vec.x, this.x) == 0
                && Float.compare(vec.y, this.y) == 0;
    }

    public Vector2 asVector2() {
        return new Vector2(this.x, this.y);
    }

    @Override
    public Vector2f clone() {
        try {
            return (Vector2f) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Vector2f that = (Vector2f) obj;
        return Float.compare(that.x, x) == 0
                && Float.compare(that.y, y) == 0;
    }

    @Override
    public int hashCode() {
        int hash = Float.hashCode(this.x);
        hash = 31 * hash + Float.hashCode(this.y);
        return hash;
    }
}