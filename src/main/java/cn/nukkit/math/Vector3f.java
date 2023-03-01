package cn.nukkit.math;

import cn.nukkit.level.ChunkPosition;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;

public class Vector3f implements Cloneable {

    public float x;
    public float y;
    public float z;

    public Vector3f() {
        this(0, 0, 0);
    }

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getZ() {
        return this.z;
    }

    public int getFloorX() {
        return Mth.floor(this.x);
    }

    public int getFloorY() {
        return Mth.floor(this.y);
    }

    public int getFloorZ() {
        return Mth.floor(this.z);
    }

    public int getChunkX() {
        return this.getFloorX() >> 4;
    }

    public int getSubChunkY() {
        return this.getFloorY() >> 4;
    }

    public int getChunkZ() {
        return this.getFloorZ() >> 4;
    }

    public Vector3f add(float n) {
        return this.add(n, n, n);
    }

    public Vector3f add(float x, float y, float z) {
        return new Vector3f(this.x + x, this.y + y, this.z + z);
    }

    public Vector3f add(Vector3f x) {
        return new Vector3f(this.x + x.getX(), this.y + x.getY(), this.z + x.getZ());
    }

    public Vector3f subtract(float n) {
        return this.subtract(n, n, n);
    }

    public Vector3f subtract(float x, float y, float z) {
        return this.add(-x, -y, -z);
    }

    public Vector3f subtract(Vector3f x) {
        return this.add(-x.getX(), -x.getY(), -x.getZ());
    }

    public Vector3f multiply(float number) {
        return new Vector3f(this.x * number, this.y * number, this.z * number);
    }

    public Vector3f multiply(float x, float y, float z) {
        return new Vector3f(this.x * x, this.y * y, this.z * z);
    }

    public Vector3f multiply(Vector3f vec) {
        return new Vector3f(this.x * vec.x, this.y * vec.y, this.z * vec.z);
    }

    public Vector3f divide(float number) {
        return new Vector3f(this.x / number, this.y / number, this.z / number);
    }

    public Vector3f divide(float x, float y, float z) {
        return new Vector3f(this.x / x, this.y / y, this.z / z);
    }

    public Vector3f divide(Vector3f vec) {
        return new Vector3f(this.x / vec.x, this.y / vec.y, this.z / vec.z);
    }

    public Vector3f ceil() {
        return new Vector3f(Mth.ceil(this.x), Mth.ceil(this.y), Mth.ceil(this.z));
    }

    public Vector3f floor() {
        return new Vector3f(this.getFloorX(), this.getFloorY(), this.getFloorZ());
    }

    public Vector3f round() {
        return new Vector3f(Math.round(this.x), Math.round(this.y), Math.round(this.z));
    }

    public Vector3f abs() {
        return new Vector3f(Math.abs(this.x), Math.abs(this.y), Math.abs(this.z));
    }

    public Vector3f getSide(BlockFace face) {
        return this.getSide(face, 1);
    }

    public Vector3f getSide(BlockFace face, int step) {
        return new Vector3f(this.getX() + face.getXOffset() * step, this.getY() + face.getYOffset() * step, this.getZ() + face.getZOffset() * step);
    }

    public Vector3f up() {
        return up(1);
    }

    public Vector3f up(int step) {
        return getSide(BlockFace.UP, step);
    }

    public Vector3f down() {
        return down(1);
    }

    public Vector3f down(int step) {
        return getSide(BlockFace.DOWN, step);
    }

    public Vector3f north() {
        return north(1);
    }

    public Vector3f north(int step) {
        return getSide(BlockFace.NORTH, step);
    }

    public Vector3f south() {
        return south(1);
    }

    public Vector3f south(int step) {
        return getSide(BlockFace.SOUTH, step);
    }

    public Vector3f east() {
        return east(1);
    }

    public Vector3f east(int step) {
        return getSide(BlockFace.EAST, step);
    }

    public Vector3f west() {
        return west(1);
    }

    public Vector3f west(int step) {
        return getSide(BlockFace.WEST, step);
    }

    public double distance(Vector3f pos) {
        return Math.sqrt(this.distanceSquared(pos));
    }

    public float distanceSquared(Vector3f pos) {
        float x = this.x - pos.x;
        float y = this.y - pos.y;
        float z = this.z - pos.z;
        return x * x + y * y + z * z;
    }

    public float distanceSquared(float x, float y, float z) {
        float xDiff = this.x - x;
        float yDiff = this.y - y;
        float zDiff = this.z - z;
        return xDiff * xDiff + yDiff * yDiff + zDiff * zDiff;
    }

    public int distanceManhattan(Vector3f pos) {
        return distanceManhattan(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ());
    }

    public int distanceManhattan(int x, int y, int z) {
        return Math.abs(x - getFloorX()) + Math.abs(y - getFloorY()) + Math.abs(z - getFloorZ());
    }

    public float maxPlainDistance() {
        return this.maxPlainDistance(0, 0);
    }

    public float maxPlainDistance(float x) {
        return this.maxPlainDistance(x, 0);
    }

    public float maxPlainDistance(float x, float z) {
        return Math.max(Math.abs(this.x - x), Math.abs(this.z - z));
    }

    public float maxPlainDistance(Vector2f vector) {
        return this.maxPlainDistance(vector.x, vector.y);
    }

    public float maxPlainDistance(Vector3f x) {
        return this.maxPlainDistance(x.x, x.z);
    }

    public double length() {
        return Math.sqrt(this.lengthSquared());
    }

    public float lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public double horizontalDistance() {
        return Math.sqrt(this.horizontalDistanceSquared());
    }

    public float horizontalDistanceSquared() {
        return this.x * this.x + this.z * this.z;
    }

    public Vector3f normalize() {
        float len = this.lengthSquared();
        if (len > 0) {
            return this.divide((float) Math.sqrt(len));
        }
        return new Vector3f(0, 0, 0);
    }

    public float dot(Vector3f v) {
        return this.x * v.x + this.y * v.y + this.z * v.z;
    }

    public Vector3f cross(Vector3f v) {
        return new Vector3f(
                this.y * v.z - this.z * v.y,
                this.z * v.x - this.x * v.z,
                this.x * v.y - this.y * v.x
        );
    }

    public Vector3f min(Vector3f vec) {
        return new Vector3f(
                Math.min(this.x, vec.x),
                Math.min(this.y, vec.y),
                Math.min(this.z, vec.z)
        );
    }

    public Vector3f max(Vector3f vec) {
        return new Vector3f(
                Math.max(this.x, vec.x),
                Math.max(this.y, vec.y),
                Math.max(this.z, vec.z)
        );
    }

    public Vector3f clamp(Vector3f min, Vector3f max) {
        return new Vector3f(
                Mth.clamp(this.x, min.x, max.x),
                Mth.clamp(this.y, min.y, max.y),
                Mth.clamp(this.z, min.z, max.z)
        );
    }

    public Vector3f lerp(Vector3f to, float t) {
        return new Vector3f(
                Mth.lerp(t, this.x, to.x),
                Mth.lerp(t, this.y, to.y),
                Mth.lerp(t, this.z, to.z)
        );
    }

    public Vector3f towards(Vector3f target, float maxDistanceDelta) {
        float diffX = target.x - this.x;
        float diffY = target.y - this.y;
        float diffZ = target.z - this.z;
        float distanceSquared = diffX * diffX + diffY * diffY + diffZ * diffZ;

        if (distanceSquared == 0 || maxDistanceDelta >= 0 && distanceSquared <= maxDistanceDelta * maxDistanceDelta) {
            return target.copyVec();
        }

        float distance = (float) Math.sqrt(distanceSquared);
        return new Vector3f(this.x + diffX / distance * maxDistanceDelta,
                this.y + diffY / distance * maxDistanceDelta,
                this.z + diffZ / distance * maxDistanceDelta);
    }

    public double angle(Vector3f to) {
        double denominator = Math.sqrt(this.lengthSquared() * to.lengthSquared());
        if (denominator < Mth.EPSILON_NORMAL_SQRT) {
            return 0;
        }

        double dot = Mth.clamp(this.dot(to) / denominator, -1, 1);
        return Math.acos(dot) * Mth.RAD_TO_DEG;
    }

    public double angleSigned(Vector3f to, Vector3f axis) {
        float crossX = this.y * to.z - this.z * to.y;
        float crossY = this.z * to.x - this.x * to.z;
        float crossZ = this.x * to.y - this.y * to.x;
        int sign = axis.x * crossX + axis.y * crossY + axis.z * crossZ >= 0 ? 1 : -1;
        return this.angle(to) * sign;
    }

    /*
     * Returns a new vector with x value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    public Vector3f getIntermediateWithXValue(Vector3f v, float x) {
        float xDiff = v.x - this.x;
        float yDiff = v.y - this.y;
        float zDiff = v.z - this.z;
        if (xDiff * xDiff < 0.0000001) {
            return null;
        }
        float f = (x - this.x) / xDiff;
        if (f < 0 || f > 1) {
            return null;
        } else {
            return new Vector3f(this.x + xDiff * f, this.y + yDiff * f, this.z + zDiff * f);
        }
    }

    /*
     * Returns a new vector with y value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    public Vector3f getIntermediateWithYValue(Vector3f v, float y) {
        float xDiff = v.x - this.x;
        float yDiff = v.y - this.y;
        float zDiff = v.z - this.z;
        if (yDiff * yDiff < 0.0000001) {
            return null;
        }
        float f = (y - this.y) / yDiff;
        if (f < 0 || f > 1) {
            return null;
        } else {
            return new Vector3f(this.x + xDiff * f, this.y + yDiff * f, this.z + zDiff * f);
        }
    }

    /*
     * Returns a new vector with z value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    public Vector3f getIntermediateWithZValue(Vector3f v, float z) {
        float xDiff = v.x - this.x;
        float yDiff = v.y - this.y;
        float zDiff = v.z - this.z;
        if (zDiff * zDiff < 0.0000001) {
            return null;
        }
        float f = (z - this.z) / zDiff;
        if (f < 0 || f > 1) {
            return null;
        } else {
            return new Vector3f(this.x + xDiff * f, this.y + yDiff * f, this.z + zDiff * f);
        }
    }

    public Vector3f setComponents(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    @Override
    public String toString() {
        return "Vector3(x=" + this.x + ",y=" + this.y + ",z=" + this.z + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vector3f)) {
            return false;
        }

        Vector3f other = (Vector3f) obj;

        return this.x == other.x && this.y == other.y && this.z == other.z;
    }

    public final boolean equalsVec(Vector3f vec) {
        if (vec == null) {
            return false;
        }
        return this.x == vec.x && this.y == vec.y && this.z == vec.z;
    }

    public int rawHashCode() {
        return super.hashCode();
    }

    @Override
    public Vector3f clone() {
        try {
            return (Vector3f) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public final Vector3f copyVec() {
        return new Vector3f(x, y, z);
    }

    public Vector3 asVector3() {
        return new Vector3(this.x, this.y, this.z);
    }

    public BlockVector3 asBlockVector3() {
        return new BlockVector3(getFloorX(), getFloorY(), getFloorZ());
    }

    public ChunkPosition toChunkPosition() {
        return new ChunkPosition(this);
    }

    public Vector3f blockCenter() {
        return this.add(0.5f, 0.5f, 0.5f);
    }

    public ListTag<FloatTag> toNbt() {
        ListTag<FloatTag> list = new ListTag<>();
        list.add(new FloatTag("", x));
        list.add(new FloatTag("", y));
        list.add(new FloatTag("", z));
        return list;
    }

    public static Vector3f fromNbt(ListTag<FloatTag> list) {
        return new Vector3f(list.get(0).data, list.get(1).data, list.get(2).data);
    }
}
