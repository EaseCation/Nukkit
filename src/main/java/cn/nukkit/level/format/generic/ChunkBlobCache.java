package cn.nukkit.level.format.generic;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;

public class ChunkBlobCache {

    private static final byte[] BORDER_BLOCKS = new byte[1];

    private final int subChunkCount;
    private final byte[] heightMapType;
    private final byte[][] heightMapData;
    private final boolean[] emptySection;

    private final long[] blobIds;
    private final long[] extendedBlobIds; // 1.18+
    private final long[] extendedBlobIdsNew; // 1.18.30+
    private final Long2ObjectMap<byte[]> clientBlobs;
    private final Long2ObjectMap<byte[]> extendedClientBlobs; // 1.18+
    private final Long2ObjectMap<byte[]> extendedClientBlobsNew; // 1.18.30+
    private final byte[] fullChunkCachedPayload;
    private final byte[][] subChunkCachedPayload;

    public ChunkBlobCache(int subChunkCount, byte[] heightMapType, byte[][] heightMapData, boolean[] emptySection, long[] blobIds, long[] extendedBlobIds, long[] extendedBlobIdsNew, Long2ObjectMap<byte[]> clientBlobs, Long2ObjectMap<byte[]> extendedClientBlobs, Long2ObjectMap<byte[]> extendedClientBlobsNew, byte[] fullChunkCachedPayload, byte[][] subChunkCachedPayload) {
        this.subChunkCount = subChunkCount;
        this.heightMapType = heightMapType;
        this.heightMapData = heightMapData;
        this.emptySection = emptySection;
        this.blobIds = blobIds;
        this.extendedBlobIds = extendedBlobIds;
        this.extendedBlobIdsNew = extendedBlobIdsNew;
        this.clientBlobs = clientBlobs;
        this.extendedClientBlobs = extendedClientBlobs;
        this.extendedClientBlobsNew = extendedClientBlobsNew;
        this.fullChunkCachedPayload = fullChunkCachedPayload;
        this.subChunkCachedPayload = subChunkCachedPayload;
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

    public long[] getBlobIds() {
        return blobIds;
    }

    public long[] getExtendedBlobIds() {
        return extendedBlobIds;
    }

    public long[] getExtendedBlobIdsNew() {
        return extendedBlobIdsNew;
    }

    public Long2ObjectMap<byte[]> getClientBlobs() {
        return clientBlobs;
    }

    public Long2ObjectMap<byte[]> getExtendedClientBlobs() {
        return extendedClientBlobs;
    }

    public Long2ObjectMap<byte[]> getExtendedClientBlobsNew() {
        return extendedClientBlobsNew;
    }

    public byte[] getFullChunkCachedPayload() {
        return fullChunkCachedPayload;
    }

    public byte[] getSubModeCachedPayload() {
        return BORDER_BLOCKS;
    }

    public byte[][] getSubChunkCachedPayload() {
        return subChunkCachedPayload;
    }
}
