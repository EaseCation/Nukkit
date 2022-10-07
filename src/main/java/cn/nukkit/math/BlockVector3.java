package cn.nukkit.math;

import cn.nukkit.level.ChunkPosition;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;

public class BlockVector3 implements Cloneable {
    public int x;
    public int y;
    public int z;

    public BlockVector3(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockVector3() {
    }

    public BlockVector3 setComponents(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public int getChunkX() {
        return this.x >> 4;
    }

    public int getSubChunkY() {
        return this.y >> 4;
    }

    public int getChunkZ() {
        return this.z >> 4;
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

    public BlockVector3 add(int n) {
        return this.add(n, n, n);
    }

    public BlockVector3 add(int x, int y, int z) {
        return new BlockVector3(this.x + x, this.y + y, this.z + z);
    }

    public BlockVector3 add(BlockVector3 x) {
        return new BlockVector3(this.x + x.getX(), this.y + x.getY(), this.z + x.getZ());
    }

    public BlockVector3 subtract(int n) {
        return this.subtract(n, n, n);
    }

    public BlockVector3 subtract(int x, int y, int z) {
        return this.add(-x, -y, -z);
    }

    public BlockVector3 subtract(BlockVector3 x) {
        return this.add(-x.getX(), -x.getY(), -x.getZ());
    }

    public BlockVector3 multiply(int number) {
        return new BlockVector3(this.x * number, this.y * number, this.z * number);
    }

    public BlockVector3 multiply(int x, int y, int z) {
        return new BlockVector3(this.x * x, this.y * y, this.z * z);
    }

    public BlockVector3 multiply(BlockVector3 vec) {
        return new BlockVector3(this.x * vec.x, this.y * vec.y, this.z * vec.z);
    }

    public BlockVector3 divide(int number) {
        return new BlockVector3(this.x / number, this.y / number, this.z / number);
    }

    public BlockVector3 divide(int x, int y, int z) {
        return new BlockVector3(this.x / x, this.y / y, this.z / z);
    }

    public BlockVector3 divide(BlockVector3 vec) {
        return new BlockVector3(this.x / vec.x, this.y / vec.y, this.z / vec.z);
    }

    public BlockVector3 abs() {
        return new BlockVector3(Math.abs(this.x), Math.abs(this.y), Math.abs(this.z));
    }

    public BlockVector3 getSide(BlockFace face) {
        return this.getSide(face, 1);
    }

    public BlockVector3 getSide(BlockFace face, int step) {
        return new BlockVector3(this.getX() + face.getXOffset() * step, this.getY() + face.getYOffset() * step, this.getZ() + face.getZOffset() * step);
    }

    public BlockVector3 up() {
        return up(1);
    }

    public BlockVector3 up(int step) {
        return getSide(BlockFace.UP, step);
    }

    public BlockVector3 down() {
        return down(1);
    }

    public BlockVector3 down(int step) {
        return getSide(BlockFace.DOWN, step);
    }

    public BlockVector3 north() {
        return north(1);
    }

    public BlockVector3 north(int step) {
        return getSide(BlockFace.NORTH, step);
    }

    public BlockVector3 south() {
        return south(1);
    }

    public BlockVector3 south(int step) {
        return getSide(BlockFace.SOUTH, step);
    }

    public BlockVector3 east() {
        return east(1);
    }

    public BlockVector3 east(int step) {
        return getSide(BlockFace.EAST, step);
    }

    public BlockVector3 west() {
        return west(1);
    }

    public BlockVector3 west(int step) {
        return getSide(BlockFace.WEST, step);
    }

    public double distance(Vector3 pos) {
        return Math.sqrt(this.distanceSquared(pos));
    }

    public double distance(BlockVector3 pos) {
        return Math.sqrt(this.distanceSquared(pos));
    }

    public double distanceSquared(Vector3 pos) {
        return distanceSquared(pos.x, pos.y, pos.z);
    }

    public double distanceSquared(BlockVector3 pos) {
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

    public int distanceManhattan(BlockVector3 pos) {
        return distanceManhattan(pos.x, pos.y, pos.z);
    }

    public int distanceManhattan(int x, int y, int z) {
        return Math.abs(x - this.x) + Math.abs(y - this.y) + Math.abs(z - this.z);
    }

    public double length() {
        return Math.sqrt(this.lengthSquared());
    }

    public int lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public int dot(BlockVector3 v) {
        return this.x * v.x + this.y * v.y + this.z * v.z;
    }

    public BlockVector3 cross(BlockVector3 v) {
        return new BlockVector3(
                this.y * v.z - this.z * v.y,
                this.z * v.x - this.x * v.z,
                this.x * v.y - this.y * v.x
        );
    }

    public BlockVector3 min(BlockVector3 vec) {
        return new BlockVector3(
                Math.min(this.x, vec.x),
                Math.min(this.y, vec.y),
                Math.min(this.z, vec.z)
        );
    }

    public BlockVector3 max(BlockVector3 vec) {
        return new BlockVector3(
                Math.max(this.x, vec.x),
                Math.max(this.y, vec.y),
                Math.max(this.z, vec.z)
        );
    }

    public BlockVector3 clamp(BlockVector3 min, BlockVector3 max) {
        return new BlockVector3(
                Mth.clamp(this.x, min.x, max.x),
                Mth.clamp(this.y, min.y, max.y),
                Mth.clamp(this.z, min.z, max.z)
        );
    }

    public Vector3 lerp(BlockVector3 to, double t) {
        return new Vector3(
                Mth.lerp(t, this.x, to.x),
                Mth.lerp(t, this.y, to.y),
                Mth.lerp(t, this.z, to.z)
        );
    }

    public Vector3 towards(BlockVector3 target, int maxDistanceDelta) {
        int diffX = target.x - this.x;
        int diffY = target.y - this.y;
        int diffZ = target.z - this.z;
        int distanceSquared = diffX * diffX + diffY * diffY + diffZ * diffZ;

        if (distanceSquared == 0 || maxDistanceDelta >= 0 && distanceSquared <= maxDistanceDelta * maxDistanceDelta) {
            return target.asVector3();
        }

        double distance = Math.sqrt(distanceSquared);
        return new Vector3(this.x + diffX / distance * maxDistanceDelta,
                this.y + diffY / distance * maxDistanceDelta,
                this.z + diffZ / distance * maxDistanceDelta);
    }

    public double angle(BlockVector3 to) {
        double denominator = Math.sqrt(this.lengthSquared() * to.lengthSquared());
        if (denominator < Mth.EPSILON_NORMAL_SQRT) {
            return 0;
        }

        double dot = Mth.clamp(this.dot(to) / denominator, -1, 1);
        return Math.acos(dot) * Mth.RAD_TO_DEG;
    }

    public double angleSigned(BlockVector3 to, BlockVector3 axis) {
        int crossX = this.y * to.z - this.z * to.y;
        int crossY = this.z * to.x - this.x * to.z;
        int crossZ = this.x * to.y - this.y * to.x;
        int sign = axis.x * crossX + axis.y * crossY + axis.z * crossZ >= 0 ? 1 : -1;
        return this.angle(to) * sign;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;

        if (!(o instanceof BlockVector3)) return false;
        BlockVector3 that = (BlockVector3) o;

        return this.x == that.x &&
                this.y == that.y &&
                this.z == that.z;
    }

    public final boolean equalsVec(BlockVector3 vec) {
        if (vec == null) {
            return false;
        }
        return this.x == vec.x && this.y == vec.y && this.z == vec.z;
    }

    @Override
    public final int hashCode() {
        return (x ^ (z << 12)) ^ (y << 24);
    }

    public int rawHashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "BlockVector3(x=" + this.x + ",y=" + this.y + ",z=" + this.z + ")";
    }

    @Override
    public BlockVector3 clone() {
        try {
            return (BlockVector3) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public final BlockVector3 copyVec() {
        return new BlockVector3(x, y, z);
    }

    public Vector3 asVector3() {
        return new Vector3(this.x, this.y, this.z);
    }

    public Vector3f asVector3f() {
        return new Vector3f(this.x, this.y, this.z);
    }

    public ChunkPosition toChunkPosition() {
        return new ChunkPosition(this);
    }

    public Vector3 blockCenter() {
        return this.add(0.5, 0.5, 0.5);
    }

    public ListTag<IntTag> toNbt() {
        ListTag<IntTag> list = new ListTag<>();
        list.add(new IntTag("", x));
        list.add(new IntTag("", y));
        list.add(new IntTag("", z));
        return list;
    }

    public static BlockVector3 fromNbt(ListTag<IntTag> list) {
        return new BlockVector3(list.get(0).data, list.get(1).data, list.get(2).data);
    }
}
