package cn.nukkit.level;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;

/**
 * Author: Adam Matthew
 * <p>
 * Nukkit Project
 */
public class ChunkPosition implements Cloneable {
    public static final long INVALID_CHUNK_POSITION = 0x80000000_80000000L;

    private int x;
    private int z;

    public ChunkPosition(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public ChunkPosition(ChunkPosition other) {
        this.x = other.x;
        this.z = other.z;
    }

    public ChunkPosition(BlockVector3 pos) {
        this.x = pos.getChunkX();
        this.z = pos.getChunkZ();
    }

    public ChunkPosition(Vector3 pos) {
        this.x = pos.getChunkX();
        this.z = pos.getChunkZ();
    }

    public ChunkPosition(Vector3f pos) {
        this.x = pos.getChunkX();
        this.z = pos.getChunkZ();
    }

    public ChunkPosition(long hash) {
        this.x = (int) (hash >> 32);
        this.z = (int) hash;
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getMinBlockX() {
        return this.x << 4;
    }

    public int getMinBlockZ() {
        return this.z << 4;
    }

    public int getMaxBlockX() {
        return this.getBlockX(15);
    }

    public int getMaxBlockZ() {
        return this.getBlockZ(15);
    }

    public int getMiddleBlockX() {
        return this.getBlockX(7);
    }

    public int getMiddleBlockZ() {
        return this.getBlockZ(7);
    }

    public int getBlockX(int localX) {
        return (this.x << 4) + localX;
    }

    public int getBlockZ(int localZ) {
        return (this.z << 4) + localZ;
    }

    public BlockVector3 getBlockPosition(int localX, int y, int localZ) {
        return new BlockVector3(this.getBlockX(localX), y, this.getBlockZ(localZ));
    }

    public BlockVector3 getMiddleBlockPosition(int y) {
        return new BlockVector3(this.getMiddleBlockX(), y, this.getMiddleBlockZ());
    }

    public BlockVector3 getWorldPosition() {
        return new BlockVector3(this.getMinBlockX(), 0, this.getMinBlockZ());
    }

    public int getChessboardDistance(ChunkPosition other) {
        return Math.max(Math.abs(this.x - other.x), Math.abs(this.z - other.z));
    }

    public long toHash() {
        return asHash(this.x, this.z);
    }

    @Override
    public int hashCode() {
        return (0x19660D * this.x + 0x3C6EF35F) ^ (0x19660D * (this.z ^ -0x21524111) + 0x3C6EF35F);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ChunkPosition)) return false;
        ChunkPosition o = (ChunkPosition) obj;
        return this.x == o.x && this.z == o.z;
    }

    @Override
    public String toString() {
        return "[" + this.x + ", " + this.z + "]";
    }

    @Override
    public ChunkPosition clone() {
        try {
            return (ChunkPosition) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public static long asHash(int x, int z) {
        return ((long) x << 32) | (z & 0xFFFFFFFFL);
    }

    public static int getX(long hash) {
        return (int) (hash >> 32);
    }

    public static int getZ(long hash) {
        return (int) hash;
    }
}
