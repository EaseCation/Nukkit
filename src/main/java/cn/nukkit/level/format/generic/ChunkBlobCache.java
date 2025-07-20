package cn.nukkit.level.format.generic;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;

public class ChunkBlobCache {

    private final long[] blobIds; // 1.21.40+
    private final Long2ObjectMap<byte[]> blobs; // 1.21.40+
    private final long[] blobIdsLegacy;
    private final Long2ObjectMap<byte[]> blobsLegacy;
    private final byte[] fullChunkPayload;
    private final byte[] subRequestModeFullChunkPayload;
    private final byte[][] subChunkPayloads;

    public ChunkBlobCache(long[] blobIds, Long2ObjectMap<byte[]> blobs, long[] blobIdsLegacy, Long2ObjectMap<byte[]> blobsLegacy, byte[] fullChunkPayload, byte[] subRequestModeFullChunkPayload, byte[][] subChunkPayloads) {
        this.blobIds = blobIds;
        this.blobs = blobs;
        this.blobIdsLegacy = blobIdsLegacy;
        this.blobsLegacy = blobsLegacy;
        this.fullChunkPayload = fullChunkPayload;
        this.subRequestModeFullChunkPayload = subRequestModeFullChunkPayload;
        this.subChunkPayloads = subChunkPayloads;
    }

    public long[] getBlobIds() {
        return blobIds;
    }

    public Long2ObjectMap<byte[]> getBlobs() {
        return blobs;
    }

    public long[] getBlobIdsLegacy() {
        return blobIdsLegacy;
    }

    public Long2ObjectMap<byte[]> getBlobsLegacy() {
        return blobsLegacy;
    }

    public byte[] getFullChunkPayload() {
        return fullChunkPayload;
    }

    public byte[] getSubRequestModeFullChunkPayload() {
        return subRequestModeFullChunkPayload;
    }

    public byte[][] getSubChunkPayloads() {
        return subChunkPayloads;
    }
}
