package cn.nukkit.math;

import cn.nukkit.utils.Utils;

public class BlockVolume implements Cloneable {
    private int minX;
    private int minY;
    private int minZ;
    private int maxX;
    private int maxY;
    private int maxZ;

    public BlockVolume(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;

        if (maxX < minX || maxY < minY || maxZ < minZ) {
            Utils.pauseInIde(() -> "Invalid block volume data: " + this);

            this.minX = Math.min(minX, maxX);
            this.minY = Math.min(minY, maxY);
            this.minZ = Math.min(minZ, maxZ);
            this.maxX = Math.max(minX, maxX);
            this.maxY = Math.max(minY, maxY);
            this.maxZ = Math.max(minZ, maxZ);
        }
    }

    public BlockVolume(BlockVector3 pos) {
        this.minX = this.maxX = pos.getX();
        this.minY = this.maxY = pos.getY();
        this.minZ = this.maxZ = pos.getZ();
    }

    public static BlockVolume fromCorners(int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
        return new BlockVolume(
                Math.min(fromX, toX),
                Math.min(fromY, toY),
                Math.min(fromZ, toZ),
                Math.max(fromX, toX),
                Math.max(fromY, toY),
                Math.max(fromZ, toZ));
    }

    public static BlockVolume fromCorners(BlockVector3 from, BlockVector3 to) {
        return fromCorners(from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ());
    }

    public static BlockVolume infinite() {
        return new BlockVolume(
                Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE,
                Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    public static BlockVolume orientBox(int x, int y, int z, int xOffset, int yOffset, int zOffset, int xLength, int yLength, int zLength, BlockFace orientation) {
        return switch (orientation) {
            default -> new BlockVolume(
                    x + xOffset,
                    y + yOffset,
                    z + zOffset,
                    x + xLength - 1 + xOffset,
                    y + yLength - 1 + yOffset,
                    z + zLength - 1 + zOffset);
            case NORTH -> new BlockVolume(
                    x + xOffset,
                    y + yOffset,
                    z - zLength + 1 + zOffset,
                    x + xLength - 1 + xOffset,
                    y + yLength - 1 + yOffset,
                    z + zOffset);
            case WEST -> new BlockVolume(
                    x - zLength + 1 + zOffset,
                    y + yOffset,
                    z + xOffset,
                    x + zOffset,
                    y + yLength - 1 + yOffset,
                    z + xLength - 1 + xOffset);
            case EAST -> new BlockVolume(
                    x + zOffset,
                    y + yOffset,
                    z + xOffset,
                    x + zLength - 1 + zOffset,
                    y + yLength - 1 + yOffset,
                    z + xLength - 1 + xOffset);
        };
    }

    public boolean intersects(BlockVolume volume) {
        return this.maxX >= volume.minX && this.minX <= volume.maxX
                && this.maxZ >= volume.minZ && this.minZ <= volume.maxZ
                && this.maxY >= volume.minY && this.minY <= volume.maxY;
    }

    public boolean intersects(int minX, int minZ, int maxX, int maxZ) {
        return this.maxX >= minX && this.minX <= maxX
                && this.maxZ >= minZ && this.minZ <= maxZ;
    }

    public void encapsulate(BlockVolume volume) {
        this.minX = Math.min(this.minX, volume.minX);
        this.minY = Math.min(this.minY, volume.minY);
        this.minZ = Math.min(this.minZ, volume.minZ);
        this.maxX = Math.max(this.maxX, volume.maxX);
        this.maxY = Math.max(this.maxY, volume.maxY);
        this.maxZ = Math.max(this.maxZ, volume.maxZ);
    }

    public void encapsulate(BlockVector3 pos) {
        this.minX = Math.min(this.minX, pos.getX());
        this.minY = Math.min(this.minY, pos.getY());
        this.minZ = Math.min(this.minZ, pos.getZ());
        this.maxX = Math.max(this.maxX, pos.getX());
        this.maxY = Math.max(this.maxY, pos.getY());
        this.maxZ = Math.max(this.maxZ, pos.getZ());
    }

    public void move(int x, int y, int z) {
        this.minX += x;
        this.minY += y;
        this.minZ += z;
        this.maxX += x;
        this.maxY += y;
        this.maxZ += z;
    }

    public void move(BlockVector3 pos) {
        move(pos.getX(), pos.getY(), pos.getZ());
    }

    public BlockVolume moved(int x, int y, int z) {
        return new BlockVolume(
                this.minX + x,
                this.minY + y,
                this.minZ + z,
                this.maxX + x,
                this.maxY + y,
                this.maxZ + z);
    }

    public BlockVolume inflatedBy(int n) {
        return inflatedBy(n, n, n);
    }

    public BlockVolume inflatedBy(int x, int y, int z) {
        return new BlockVolume(
                this.minX - x,
                this.minY - y,
                this.minZ - z,
                this.maxX + x,
                this.maxY + y,
                this.maxZ + z);
    }

    public boolean isInside(BlockVector3 pos) {
        return isInside(pos.getX(), pos.getY(), pos.getZ());
    }

    public boolean isInside(int x, int y, int z) {
        return x >= this.minX && x <= this.maxX
                && z >= this.minZ && z <= this.maxZ
                && y >= this.minY && y <= this.maxY;
    }

    public BlockVector3 getLength() {
        return new BlockVector3(
                this.maxX - this.minX,
                this.maxY - this.minY,
                this.maxZ - this.minZ);
    }

    public BlockVector3 getSpan() {
        return new BlockVector3(
                getXSpan(),
                getYSpan(),
                getZSpan());
    }

    public int getXSpan() {
        return this.maxX - this.minX + 1;
    }

    public int getYSpan() {
        return this.maxY - this.minY + 1;
    }

    public int getZSpan() {
        return this.maxZ - this.minZ + 1;
    }

    public int getCapacity() {
        return getXSpan() * getYSpan() * getZSpan();
    }

    public BlockVector3 getCenter() {
        return new BlockVector3(
                this.minX + (this.maxX - this.minX + 1) / 2,
                this.minY + (this.maxY - this.minY + 1) / 2,
                this.minZ + (this.maxZ - this.minZ + 1) / 2);
    }

    public BlockVector3 getMin() {
        return new BlockVector3(this.minX, this.minY, this.minZ);
    }

    public BlockVector3 getMax() {
        return new BlockVector3(this.maxX, this.maxY, this.maxZ);
    }

    @Override
    public String toString() {
        return "BlockVolume(minX=" + minX + ",minY=" + minY + ",minZ=" + minZ + ",maxX=" + maxX + ",maxY=" + maxY + ",maxZ=" + maxZ + ")";
    }

    public String toShortString() {
        return this.minX + "," + this.minY + "," + this.minZ + "," + this.maxX + "," + this.maxY + "," + this.maxZ;
    }

    public String debugText() {
        return "(" + this.minX + "," + this.minY + "," + this.minZ + "," + this.maxX + "," + this.maxY + "," + this.maxZ + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BlockVolume other)) {
            return false;
        }
        return this.minX == other.minX
                && this.minY == other.minY
                && this.minZ == other.minZ
                && this.maxX == other.maxX
                && this.maxY == other.maxY
                && this.maxZ == other.maxZ;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = result * 31 + this.minX;
        result = result * 31 + this.minY;
        result = result * 31 + this.minZ;
        result = result * 31 + this.maxX;
        result = result * 31 + this.maxY;
        result = result * 31 + this.maxZ;
        return result;
    }

    @Override
    public BlockVolume clone() {
        try {
            return (BlockVolume) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public int getMinX() {
        return minX;
    }

    public int getMinY() {
        return minY;
    }

    public int getMinZ() {
        return minZ;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMaxZ() {
        return maxZ;
    }
}
