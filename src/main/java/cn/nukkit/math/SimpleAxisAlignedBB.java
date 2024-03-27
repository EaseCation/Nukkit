package cn.nukkit.math;

/**
 * auth||: MagicDroidX
 * Nukkit Project
 */
public class SimpleAxisAlignedBB implements AxisAlignedBB {

    private double minX;
    private double minY;
    private double minZ;
    private double maxX;
    private double maxY;
    private double maxZ;

    public SimpleAxisAlignedBB(Vector3 pos1, Vector3 pos2) {
        this.minX = Math.min(pos1.x, pos2.x);
        this.minY = Math.min(pos1.y, pos2.y);
        this.minZ = Math.min(pos1.z, pos2.z);
        this.maxX = Math.max(pos1.x, pos2.x);
        this.maxY = Math.max(pos1.y, pos2.y);
        this.maxZ = Math.max(pos1.z, pos2.z);
    }

    public SimpleAxisAlignedBB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    @Override
    public String toString() {
        return "AxisAlignedBB(" + this.getMinX() + ", " + this.getMinY() + ", " + this.getMinZ() + ", " + this.getMaxX() + ", " + this.getMaxY() + ", " + this.getMaxZ() + ")";
    }

    public String toShortString() {
        return this.getMinX() + "," + this.getMinY() + "," + this.getMinZ() + "," + this.getMaxX() + "," + this.getMaxY() + "," + this.getMaxZ();
    }

    public String debugText() {
        return "(" + NukkitMath.round(getMinX(), 2) + "," + NukkitMath.round(getMinY(), 2) + "," + NukkitMath.round(getMinZ(), 2) + "," + NukkitMath.round(getMaxX(), 2) + "," + NukkitMath.round(getMaxY(), 2) + "," + NukkitMath.round(getMaxZ(), 2) + ")";
    }

    @Override
    public double getMinX() {
        return minX;
    }

    @Override
    public void setMinX(double minX) {
        this.minX = minX;
    }

    @Override
    public double getMinY() {
        return minY;
    }

    @Override
    public void setMinY(double minY) {
        this.minY = minY;
    }

    @Override
    public double getMinZ() {
        return minZ;
    }

    @Override
    public void setMinZ(double minZ) {
        this.minZ = minZ;
    }

    @Override
    public double getMaxX() {
        return maxX;
    }

    @Override
    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    @Override
    public double getMaxY() {
        return maxY;
    }

    @Override
    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    @Override
    public double getMaxZ() {
        return maxZ;
    }

    @Override
    public void setMaxZ(double maxZ) {
        this.maxZ = maxZ;
    }

    @Override
    public SimpleAxisAlignedBB clone() {
        try {
            return (SimpleAxisAlignedBB) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SimpleAxisAlignedBB that)) {
            return false;
        }
        return Double.compare(that.minX, this.minX) == 0
                && Double.compare(that.minY, this.minY) == 0
                && Double.compare(that.minZ, this.minZ) == 0
                && Double.compare(that.maxX, this.maxX) == 0
                && Double.compare(that.maxY, this.maxY) == 0
                && Double.compare(that.maxZ, this.maxZ) == 0;
    }

    @Override
    public int hashCode() {
        int hash = Double.hashCode(this.minX);
        hash = 31 * hash + Double.hashCode(this.minY);
        hash = 31 * hash + Double.hashCode(this.minZ);
        hash = 31 * hash + Double.hashCode(this.maxX);
        hash = 31 * hash + Double.hashCode(this.maxY);
        hash = 31 * hash + Double.hashCode(this.maxZ);
        return hash;
    }
}
