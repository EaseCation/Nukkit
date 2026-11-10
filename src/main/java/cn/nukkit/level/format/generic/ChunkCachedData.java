package cn.nukkit.level.format.generic;

import javax.annotation.Nullable;

public class ChunkCachedData {
    private final int subChunkCount;
    private final byte[] heightMapType;
    private final byte[][] heightMapData;
    private final boolean[] emptySection;
    private final ChunkBlobCache blobCache;
    @Nullable
    private final ChunkPacketCache packetCache;
    private final long networkRevision;

    public ChunkCachedData(int subChunkCount, byte[] heightMapType, byte[][] heightMapData, boolean[] emptySection, ChunkBlobCache blobCache, @Nullable ChunkPacketCache packetCache, long networkRevision) {
        this.subChunkCount = subChunkCount;
        this.heightMapType = heightMapType;
        this.heightMapData = heightMapData;
        this.emptySection = emptySection;
        this.blobCache = blobCache;
        this.packetCache = packetCache;
        this.networkRevision = networkRevision;
    }

    public int getSubChunkCount() {
        return subChunkCount;
    }

    public byte[] getHeightMapType() {
        return heightMapType;
    }

    public byte[][] getHeightMapData() {
        return heightMapData;
    }

    public boolean[] getEmptySection() {
        return emptySection;
    }

    public ChunkBlobCache getBlobCache() {
        return blobCache;
    }

    @Nullable
    public ChunkPacketCache getPacketCache() {
        return packetCache;
    }

    /**
     * 生成这份数据时区块的网络版本号。
     * 与区块当前的 {@link BaseFullChunk#getNetworkRevision()} 不一致时，说明这份数据已经过期。
     */
    public long getNetworkRevision() {
        return networkRevision;
    }
}
