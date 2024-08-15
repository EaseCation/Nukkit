package cn.nukkit.level;

import cn.nukkit.math.Mth;
import lombok.ToString;

@ToString
public final class HeightRange {
    private final int minChunkY;
    private final int maxChunkY;
    private final int subChunkCount;
    private final int chunkYIndexOffset;

    private final int minY;
    private final int maxY;
    private final int blockCount;
    private final int yIndexOffset;

    /**
     * @param minY minimum world Y (inclusive)
     * @param maxY maximum world Y (exclusive)
     */
    public static HeightRange blockY(int minY, int maxY) {
        return new HeightRange(minY >> 4, maxY >> 4);
    }

    /**
     * @param minChunkY minimum chunk Y (inclusive)
     * @param maxChunkY maximum chunk Y (exclusive)
     */
    public static HeightRange chunkY(int minChunkY, int maxChunkY) {
        return new HeightRange(minChunkY, maxChunkY);
    }

    private HeightRange(int minChunkY, int maxChunkY) {
        this.minChunkY = minChunkY;
        this.maxChunkY = maxChunkY;
        subChunkCount = maxChunkY - minChunkY;
        chunkYIndexOffset = minChunkY < 0 ? -minChunkY : 0;

        minY = minChunkY << 4;
        maxY = maxChunkY << 4;
        blockCount = maxY - minY;
        yIndexOffset = minY < 0 ? -minY : 0;
    }

    /**
     * Minimum chunk Y (inclusive)
     */
    public int getMinChunkY() {
        return minChunkY;
    }

    /**
     * Maximum chunk Y (exclusive)
     */
    public int getMaxChunkY() {
        return maxChunkY;
    }

    public int getSubChunkCount() {
        return subChunkCount;
    }

    /**
     * @return array index offset
     */
    public int getChunkYIndexOffset() {
        return chunkYIndexOffset;
    }

    public boolean isValidSubChunkY(int chunkY) {
        return chunkY < maxChunkY && chunkY >= minChunkY;
    }

    /**
     * Minimum Y (inclusive)
     */
    public int getMinY() {
        return minY;
    }

    /**
     * Maximum Y (exclusive)
     */
    public int getMaxY() {
        return maxY;
    }

    public int getBlockCount() {
        return blockCount;
    }

    /**
     * @return array index offset
     */
    public int getYIndexOffset() {
        return yIndexOffset;
    }

    public boolean isValidBlockY(int y) {
        return y < maxY && y >= minY;
    }

    public boolean isValidBlockY(float y) {
        return y < maxY && y >= minY;
    }

    public boolean isValidBlockY(double y) {
        return y < maxY && y >= minY;
    }

    public int clampValidBlockY(int y) {
        return Mth.clamp(y, minY, maxY - 1);
    }

    public float clampValidBlockY(float y) {
        return Mth.clamp(y, minY, maxY - 1);
    }

    public double clampValidBlockY(double y) {
        return Mth.clamp(y, minY, maxY - 1);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof HeightRange o)) {
            return false;
        }
        return minChunkY == o.minChunkY && maxChunkY == o.maxChunkY;
    }

    @Override
    public int hashCode() {
        return minChunkY ^ maxChunkY;
    }
}
