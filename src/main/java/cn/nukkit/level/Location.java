package cn.nukkit.level;

import cn.nukkit.math.Mth;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.LevelException;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Location extends Position {

    public double yaw;
    public double pitch;

    public Location() {
        this(0, 0, 0);
    }

    public Location(double x, double y, double z, Level level) {
        this(x, y, z, 0, 0, level);
    }

    public Location(double x, double y, double z) {
        this(x, y, z, 0);
    }

    public Location(double x, double y, double z, double yaw) {
        this(x, y, z, yaw, 0);
    }

    public Location(double x, double y, double z, double yaw, double pitch) {
        this(x, y, z, yaw, pitch, null);
    }

    public Location(double x, double y, double z, double yaw, double pitch, Level level) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.level = level;
    }

    public static Location fromObject(Vector3 pos) {
        return fromObject(pos, null, 0, 0);
    }

    public static Location fromObject(Vector3 pos, double yaw) {
        return fromObject(pos, null, yaw);
    }

    public static Location fromObject(Vector3 pos, double yaw, double pitch) {
        return fromObject(pos, null, yaw, pitch);
    }

    public static Location fromObject(Vector3 pos, Level level) {
        return fromObject(pos, level, 0, 0);
    }

    public static Location fromObject(Location pos, Level level) {
        return fromObject(pos, level, pos.yaw, pos.pitch);
    }

    public static Location fromObject(Vector3 pos, Level level, double yaw) {
        return fromObject(pos, level, yaw, 0);
    }

    public static Location fromObject(Vector3 pos, Level level, double yaw, double pitch) {
        return new Location(pos.x, pos.y, pos.z, yaw, pitch, level == null ? pos instanceof Position ? ((Position) pos).level : null : level);
    }

    public double getYaw() {
        return this.yaw;
    }

    public double getPitch() {
        return this.pitch;
    }

    public Location setYaw(double yaw) {
        this.yaw = yaw;
        return this;
    }

    public Location setPitch(double pitch) {
        this.pitch = pitch;
        return this;
    }

    @Override
    public String toString() {
        return "Location (level=" + (this.isValid() ? this.getLevel().getName() : "null") + ", x=" + this.x + ", y=" + this.y + ", z=" + this.z + ", yaw=" + this.yaw + ", pitch=" + this.pitch + ")";
    }

    public String debugTextWithRot() {
        return "(" + NukkitMath.round(x, 2) + "," + NukkitMath.round(y, 2) + "," + NukkitMath.round(z, 2) + " " + NukkitMath.round(pitch, 2) + "," + NukkitMath.round(yaw, 2) + ")";
    }

    @Override
    public Location getLocation() {
        if (this.isValid()) return new Location(this.x, this.y, this.z, this.yaw, this.pitch, this.level);
        else throw new LevelException("Undefined Level reference");
    }

    @Override
    public Location add(double n) {
        return this.add(n, n, n);
    }

    @Override
    public Location add(double x, double y, double z) {
        return new Location(this.x + x, this.y + y, this.z + z, this.yaw, this.pitch, this.level);
    }

    @Override
    public Location add(Vector3 x) {
        return new Location(this.x + x.getX(), this.y + x.getY(), this.z + x.getZ(), this.yaw, this.pitch, this.level);
    }

    @Override
    public Location subtract(double n) {
        return this.subtract(n, n, n);
    }

    @Override
    public Location subtract(double x, double y, double z) {
        return this.add(-x, -y, -z);
    }

    @Override
    public Location subtract(Vector3 x) {
        return this.add(-x.getX(), -x.getY(), -x.getZ());
    }

    @Override
    public Location multiply(double number) {
        return new Location(this.x * number, this.y * number, this.z * number, this.yaw, this.pitch, this.level);
    }

    @Override
    public Location multiply(double x, double y, double z) {
        return new Location(this.x * x, this.y * y, this.z * z, this.yaw, this.pitch, this.level);
    }

    @Override
    public Location multiply(Vector3 vec) {
        return new Location(this.x * vec.x, this.y * vec.y, this.z * vec.z, this.yaw, this.pitch, this.level);
    }

    @Override
    public Location divide(double number) {
        return new Location(this.x / number, this.y / number, this.z / number, this.yaw, this.pitch, this.level);
    }

    @Override
    public Location divide(double x, double y, double z) {
        return new Location(this.x / x, this.y / y, this.z / z, this.yaw, this.pitch, this.level);
    }

    @Override
    public Location divide(Vector3 vec) {
        return new Location(this.x / vec.x, this.y / vec.y, this.z / vec.z, this.yaw, this.pitch, this.level);
    }

    @Override
    public Location ceil() {
        return new Location(Mth.ceil(this.x), Mth.ceil(this.y), Mth.ceil(this.z), this.yaw, this.pitch, this.level);
    }

    @Override
    public Location floor() {
        return new Location(this.getFloorX(), this.getFloorY(), this.getFloorZ(), this.yaw, this.pitch, this.level);
    }

    @Override
    public Location round() {
        return new Location(Math.round(this.x), Math.round(this.y), Math.round(this.z), this.yaw, this.pitch, this.level);
    }

    @Override
    public Location abs() {
        return new Location(Math.abs(this.x), Math.abs(this.y), Math.abs(this.z), this.yaw, this.pitch, this.level);
    }

    public Vector3 getDirectionVector() {
        double pitch = ((getPitch() + 90) * Math.PI) / 180;
        double yaw = ((getYaw() + 90) * Math.PI) / 180;
        double sp = Mth.sin(pitch);
        double x = sp * Mth.cos(yaw);
        double z = sp * Mth.sin(yaw);
        double y = Mth.cos(pitch);
        return new Vector3(x, y, z).normalize();
    }

    @Override
    public Location clone() {
        return (Location) super.clone();
    }

    public final Location copyLocation() {
        return new Location(x, y, z, yaw, pitch, level);
    }

    public final boolean equalsLocation(Location that) {
        if (that == null) {
            return false;
        }
        return that.level == this.level
                && Double.compare(that.x, this.x) == 0
                && Double.compare(that.y, this.y) == 0
                && Double.compare(that.z, this.z) == 0
                && Double.compare(that.yaw, this.yaw) == 0
                && Double.compare(that.pitch, this.pitch) == 0;
    }

    public final boolean equalsRot(Location that) {
        if (that == null) {
            return false;
        }
        return Double.compare(that.yaw, this.yaw) == 0
                && Double.compare(that.pitch, this.pitch) == 0;
    }

    public final boolean equalsVecAndRot(Location that) {
        if (that == null) {
            return false;
        }
        return Double.compare(that.x, this.x) == 0
                && Double.compare(that.y, this.y) == 0
                && Double.compare(that.z, this.z) == 0
                && Double.compare(that.yaw, this.yaw) == 0
                && Double.compare(that.pitch, this.pitch) == 0;
    }
}
