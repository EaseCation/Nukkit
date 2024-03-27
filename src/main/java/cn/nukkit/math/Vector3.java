package cn.nukkit.math;

import cn.nukkit.level.ChunkPosition;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;

import javax.annotation.Nullable;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Vector3 implements Cloneable {
    public static final Vector3 ZERO = new Vector3(0, 0, 0);

    public double x;
    public double y;
    public double z;

    public Vector3() {
        this(0, 0, 0);
    }

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(Vector3 vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    public Vector3(Vector3f vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    public Vector3(BlockVector3 vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
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
        return getFloorX() >> 4;
    }

    public int getSubChunkY() {
        return this.getFloorY() >> 4;
    }

    public int getChunkZ() {
        return getFloorZ() >> 4;
    }

    public Vector3 add(double n) {
        return this.add(n, n, n);
    }

    public Vector3 add(double x, double y, double z) {
        return new Vector3(this.x + x, this.y + y, this.z + z);
    }

    public Vector3 add(Vector3 x) {
        return new Vector3(this.x + x.getX(), this.y + x.getY(), this.z + x.getZ());
    }

    public Vector3 subtract(double n) {
        return this.subtract(n, n, n);
    }

    public Vector3 subtract(double x, double y, double z) {
        return this.add(-x, -y, -z);
    }

    public Vector3 subtract(Vector3 x) {
        return this.add(-x.getX(), -x.getY(), -x.getZ());
    }

    public Vector3 multiply(double number) {
        return new Vector3(this.x * number, this.y * number, this.z * number);
    }

    public Vector3 multiply(double x, double y, double z) {
        return new Vector3(this.x * x, this.y * y, this.z * z);
    }

    public Vector3 multiply(Vector3 vec) {
        return new Vector3(this.x * vec.x, this.y * vec.y, this.z * vec.z);
    }

    public Vector3 divide(double number) {
        return new Vector3(this.x / number, this.y / number, this.z / number);
    }

    public Vector3 divide(double x, double y, double z) {
        return new Vector3(this.x / x, this.y / y, this.z / z);
    }

    public Vector3 divide(Vector3 vec) {
        return new Vector3(this.x / vec.x, this.y / vec.y, this.z / vec.z);
    }

    public Vector3 ceil() {
        return new Vector3(Mth.ceil(this.x), Mth.ceil(this.y), Mth.ceil(this.z));
    }

    public Vector3 floor() {
        return new Vector3(this.getFloorX(), this.getFloorY(), this.getFloorZ());
    }

    public Vector3 round() {
        return new Vector3(Math.round(this.x), Math.round(this.y), Math.round(this.z));
    }

    public Vector3 abs() {
        return new Vector3(Math.abs(this.x), Math.abs(this.y), Math.abs(this.z));
    }

    public Vector3 getSide(BlockFace face) {
        return this.getSide(face, 1);
    }

    public Vector3 getSide(BlockFace face, int step) {
        return new Vector3(this.getX() + face.getXOffset() * step, this.getY() + face.getYOffset() * step, this.getZ() + face.getZOffset() * step);
    }

    public final Vector3 getSideVec(BlockFace face) {
        return new Vector3(this.getX() + face.getXOffset(), this.getY() + face.getYOffset(), this.getZ() + face.getZOffset());
    }

    public final Vector3 getSideVec(BlockFace face, int step) {
        return new Vector3(this.getX() + face.getXOffset() * step, this.getY() + face.getYOffset() * step, this.getZ() + face.getZOffset() * step);
    }

    public Vector3 up() {
        return up(1);
    }

    public Vector3 up(int step) {
        return getSide(BlockFace.UP, step);
    }

    public final Vector3 upVec() {
        return this.getSideVec(BlockFace.UP);
    }

    public Vector3 down() {
        return down(1);
    }

    public Vector3 down(int step) {
        return getSide(BlockFace.DOWN, step);
    }

    public final Vector3 downVec() {
        return this.getSideVec(BlockFace.DOWN);
    }

    public Vector3 north() {
        return north(1);
    }

    public Vector3 north(int step) {
        return getSide(BlockFace.NORTH, step);
    }

    public final Vector3 northVec() {
        return this.getSideVec(BlockFace.NORTH);
    }

    public Vector3 south() {
        return south(1);
    }

    public Vector3 south(int step) {
        return getSide(BlockFace.SOUTH, step);
    }

    public final Vector3 southVec() {
        return this.getSideVec(BlockFace.SOUTH);
    }

    public Vector3 east() {
        return east(1);
    }

    public Vector3 east(int step) {
        return getSide(BlockFace.EAST, step);
    }

    public final Vector3 eastVec() {
        return this.getSideVec(BlockFace.EAST);
    }

    public Vector3 west() {
        return west(1);
    }

    public Vector3 west(int step) {
        return getSide(BlockFace.WEST, step);
    }

    public final Vector3 westVec() {
        return this.getSideVec(BlockFace.WEST);
    }

    public double distance(Vector3 pos) {
        return distance(pos.x, pos.y, pos.z);
    }

    public double distance(double x, double y, double z) {
        return Math.sqrt(this.distanceSquared(x, y, z));
    }

    public double distanceSquared(Vector3 pos) {
        return distanceSquared(pos.x, pos.y, pos.z);
    }

    public double distanceSquared(double x, double y, double z) {
        double xDiff = this.x - x;
        double yDiff = this.y - y;
        double zDiff = this.z - z;
        return xDiff * xDiff + yDiff * yDiff + zDiff * zDiff;
    }

    public int distanceManhattan(Vector3 pos) {
        return distanceManhattan(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ());
    }

    public int distanceManhattan(int x, int y, int z) {
        return Math.abs(x - getFloorX()) + Math.abs(y - getFloorY()) + Math.abs(z - getFloorZ());
    }

    public double distance2(Vector3 pos) {
        return distance2(pos.x, pos.z);
    }

    public double distance2(double x, double z) {
        return Math.sqrt(this.distanceSquared2(x, z));
    }

    public double distance2(Vector2 pos) {
        return distance2(pos.x, pos.y);
    }

    public double distanceSquared2(Vector3 pos) {
        return distanceSquared2(pos.getFloorX(), pos.getFloorZ());
    }

    public double distanceSquared2(double x, double z) {
        double xDiff = this.x - x;
        double zDiff = this.z - z;
        return xDiff * xDiff + zDiff * zDiff;
    }

    public double distanceSquared2(Vector2 pos) {
        return distanceSquared2(pos.getFloorX(), pos.getFloorY());
    }

    public int distanceManhattan2(Vector3 pos) {
        return distanceManhattan2(pos.getFloorX(), pos.getFloorZ());
    }

    public int distanceManhattan2(int x, int z) {
        return Math.abs(x - getFloorX()) + Math.abs(z - getFloorZ());
    }

    public double maxPlainDistance() {
        return this.maxPlainDistance(0, 0);
    }

    public double maxPlainDistance(double x) {
        return this.maxPlainDistance(x, 0);
    }

    public double maxPlainDistance(double x, double z) {
        return Math.max(Math.abs(this.x - x), Math.abs(this.z - z));
    }

    public double maxPlainDistance(Vector2 vector) {
        return this.maxPlainDistance(vector.x, vector.y);
    }

    public double maxPlainDistance(Vector3 x) {
        return this.maxPlainDistance(x.x, x.z);
    }

    public double length() {
        return Math.sqrt(this.lengthSquared());
    }

    public double lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public double horizontalDistance() {
        return Math.sqrt(this.horizontalDistanceSquared());
    }

    public double horizontalDistanceSquared() {
        return this.x * this.x + this.z * this.z;
    }

    public Vector3 normalize() {
        double len = this.lengthSquared();
        if (len > 0) {
            return this.divide(Math.sqrt(len));
        }
        return new Vector3(0, 0, 0);
    }

    public double dot(Vector3 v) {
        return this.dot(v.x, v.y, v.z);
    }

    public double dot(double x, double y, double z) {
        return this.x * x + this.y * y + this.z * z;
    }

    public double dot2(Vector3 v) {
        return this.dot2(v.x, v.z);
    }

    public double dot2(double x, double z) {
        return this.x * x + this.z * z;
    }

    public double dot2(Vector2 v) {
        return this.dot2(v.x, v.y);
    }

    public Vector3 cross(Vector3 v) {
        return new Vector3(
                this.y * v.z - this.z * v.y,
                this.z * v.x - this.x * v.z,
                this.x * v.y - this.y * v.x
        );
    }

    public Vector3 min(Vector3 vec) {
        return new Vector3(
                Math.min(this.x, vec.x),
                Math.min(this.y, vec.y),
                Math.min(this.z, vec.z)
        );
    }

    public Vector3 max(Vector3 vec) {
        return new Vector3(
                Math.max(this.x, vec.x),
                Math.max(this.y, vec.y),
                Math.max(this.z, vec.z)
        );
    }

    public Vector3 clamp(Vector3 min, Vector3 max) {
        return new Vector3(
                Mth.clamp(this.x, min.x, max.x),
                Mth.clamp(this.y, min.y, max.y),
                Mth.clamp(this.z, min.z, max.z)
        );
    }

    public Vector3 lerp(Vector3 to, double t) {
        return new Vector3(
                Mth.lerp(t, this.x, to.x),
                Mth.lerp(t, this.y, to.y),
                Mth.lerp(t, this.z, to.z)
        );
    }

    public Vector3 towards(Vector3 target, double maxDistanceDelta) {
        double diffX = target.x - this.x;
        double diffY = target.y - this.y;
        double diffZ = target.z - this.z;
        double distanceSquared = diffX * diffX + diffY * diffY + diffZ * diffZ;

        if (distanceSquared == 0 || maxDistanceDelta >= 0 && distanceSquared <= maxDistanceDelta * maxDistanceDelta) {
            return target.copyVec();
        }

        double distance = Math.sqrt(distanceSquared);
        return new Vector3(this.x + diffX / distance * maxDistanceDelta,
                this.y + diffY / distance * maxDistanceDelta,
                this.z + diffZ / distance * maxDistanceDelta);
    }

    public double angle(Vector3 to) {
        double denominator = Math.sqrt(this.lengthSquared() * to.lengthSquared());
        if (denominator < Mth.EPSILON_NORMAL_SQRT) {
            return 0;
        }

        double dot = Mth.clamp(this.dot(to) / denominator, -1, 1);
        return Math.acos(dot) * Mth.RAD_TO_DEG;
    }

    public double angleSigned(Vector3 to, Vector3 axis) {
        double crossX = this.y * to.z - this.z * to.y;
        double crossY = this.z * to.x - this.x * to.z;
        double crossZ = this.x * to.y - this.y * to.x;
        int sign = axis.x * crossX + axis.y * crossY + axis.z * crossZ >= 0 ? 1 : -1;
        return this.angle(to) * sign;
    }

    /**
     * Returns a new vector with x value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     *
     * @param v vector
     * @param x x value
     * @return intermediate vector
     */
    @Nullable
    public Vector3 getIntermediateWithXValue(Vector3 v, double x) {
        double xDiff = v.x - this.x;
        double yDiff = v.y - this.y;
        double zDiff = v.z - this.z;
        if (xDiff * xDiff < 0.0000001) {
            return null;
        }
        double f = (x - this.x) / xDiff;
        if (f < 0 || f > 1) {
            return null;
        } else {
            return new Vector3(this.x + xDiff * f, this.y + yDiff * f, this.z + zDiff * f);
        }
    }

    /**
     * Returns a new vector with y value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     *
     * @param v vector
     * @param y y value
     * @return intermediate vector
     */
    @Nullable
    public Vector3 getIntermediateWithYValue(Vector3 v, double y) {
        double xDiff = v.x - this.x;
        double yDiff = v.y - this.y;
        double zDiff = v.z - this.z;
        if (yDiff * yDiff < 0.0000001) {
            return null;
        }
        double f = (y - this.y) / yDiff;
        if (f < 0 || f > 1) {
            return null;
        } else {
            return new Vector3(this.x + xDiff * f, this.y + yDiff * f, this.z + zDiff * f);
        }
    }

    /**
     * Returns a new vector with z value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     *
     * @param v vector
     * @param z z value
     * @return intermediate vector
     */
    @Nullable
    public Vector3 getIntermediateWithZValue(Vector3 v, double z) {
        double xDiff = v.x - this.x;
        double yDiff = v.y - this.y;
        double zDiff = v.z - this.z;
        if (zDiff * zDiff < 0.0000001) {
            return null;
        }
        double f = (z - this.z) / zDiff;
        if (f < 0 || f > 1) {
            return null;
        } else {
            return new Vector3(this.x + xDiff * f, this.y + yDiff * f, this.z + zDiff * f);
        }
    }

    public Vector3 setComponents(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Vector3 setComponents(Vector3 other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        return this;
    }

    public Vector3 setComponents(Vector3f other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        return this;
    }

    public Vector3 setComponents(BlockVector3 other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        return this;
    }

    public Vector3 xz() {
        return new Vector3(this.x, 0, this.z);
    }

    public Vector3 xRot(double rads) {
        double cos = Mth.cos(rads);
        double sin = Mth.sin(rads);
        return new Vector3(this.x, this.y * cos + this.z * sin, this.z * cos - this.y * sin);
    }

    public Vector3 yRot(double rads) {
        double cos = Mth.cos(rads);
        double sin = Mth.sin(rads);
        return new Vector3(this.x * cos + this.z * sin, this.y, this.z * cos - this.x * sin);
    }

    public Vector3 zRot(double rads) {
        double cos = Mth.cos(rads);
        double sin = Mth.sin(rads);
        return new Vector3(this.x * cos + this.y * sin, this.y * cos - this.x * sin, this.z);
    }

    /**
     * @return pitch
     */
    public double xRotFromDirection() {
        return Mth.atan2(this.y, this.horizontalDistance()) * Mth.RAD_TO_DEG;
    }

    /**
     * @return yaw
     */
    public double yRotFromDirection() {
        return Mth.atan2(this.x, this.z) * Mth.RAD_TO_DEG;
    }

    /**
     * @param xRot pitch
     * @param yRot yaw
     */
    public static Vector3 directionFromRotation(double xRot, double yRot) {
        double xCos = -Mth.cos(-xRot * Mth.DEG_TO_RAD);
        return new Vector3(Mth.sin(-yRot * Mth.DEG_TO_RAD - Mth.PI) * xCos, Mth.sin(-xRot * Mth.DEG_TO_RAD), Mth.cos(-yRot * Mth.DEG_TO_RAD - Mth.PI) * xCos);
    }

    @Override
    public String toString() {
        return "Vector3(x=" + this.x + ",y=" + this.y + ",z=" + this.z + ")";
    }

    public String toShortString() {
        return this.x + "," + this.y + "," + this.z;
    }

    public String debugText() {
        return "(" + NukkitMath.round(x, 2) + "," + NukkitMath.round(y, 2) + "," + NukkitMath.round(z, 2) + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Vector3)) {
            return false;
        }

        Vector3 other = (Vector3) obj;

        return Double.compare(other.x, this.x) == 0
                && Double.compare(other.y, this.y) == 0
                && Double.compare(other.z, this.z) == 0;
    }

    public final boolean equalsVec(Vector3 vec) {
        if (vec == null) {
            return false;
        }
        return Double.compare(vec.x, this.x) == 0
                && Double.compare(vec.y, this.y) == 0
                && Double.compare(vec.z, this.z) == 0;
    }

    @Override
    public int hashCode() {
        int hash = Double.hashCode(this.x);
        hash = 31 * hash + Double.hashCode(this.y);
        hash = 31 * hash + Double.hashCode(this.z);
        return hash;
    }

    @Override
    public Vector3 clone() {
        try {
            return (Vector3) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public final Vector3 copyVec() {
        return new Vector3(x, y, z);
    }

    public Vector3f asVector3f() {
        return new Vector3f((float) this.x, (float) this.y, (float) this.z);
    }

    public BlockVector3 asBlockVector3() {
        return new BlockVector3(this.getFloorX(), this.getFloorY(), this.getFloorZ());
    }

    public ChunkPosition toChunkPosition() {
        return new ChunkPosition(this);
    }

    public Vector3 blockCenter() {
        return this.add(0.5, 0.5, 0.5);
    }

    public ListTag<DoubleTag> toNbt() {
        return toNbt("");
    }

    public ListTag<DoubleTag> toNbt(String name) {
        ListTag<DoubleTag> list = new ListTag<>(name);
        list.add(new DoubleTag("", x));
        list.add(new DoubleTag("", y));
        list.add(new DoubleTag("", z));
        return list;
    }

    public static Vector3 fromNbt(ListTag<DoubleTag> list) {
        return new Vector3(list.get(0).data, list.get(1).data, list.get(2).data);
    }

    public ListTag<FloatTag> toNbtf() {
        return toNbtf("");
    }

    public ListTag<FloatTag> toNbtf(String name) {
        ListTag<FloatTag> list = new ListTag<>(name);
        list.add(new FloatTag("", (float) x));
        list.add(new FloatTag("", (float) y));
        list.add(new FloatTag("", (float) z));
        return list;
    }

    public static Vector3 fromNbtf(ListTag<FloatTag> list) {
        return new Vector3(list.get(0).data, list.get(1).data, list.get(2).data);
    }

    public boolean checkIncorrectIntegerRange() {
        return this.x >= Integer.MAX_VALUE || this.x <= Integer.MIN_VALUE || this.y >= Integer.MAX_VALUE || this.y <= Integer.MIN_VALUE || this.z >= Integer.MAX_VALUE || this.z <= Integer.MIN_VALUE;
    }
}
