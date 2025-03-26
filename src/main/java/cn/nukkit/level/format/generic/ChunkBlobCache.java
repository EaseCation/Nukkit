package cn.nukkit.level.format.generic;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;

public class ChunkBlobCache {

    private final long[] blobIds;
    private final Long2ObjectMap<byte[]> blobs;
    private final byte[] fullChunkPayload;
    private final byte[] subRequestModeFullChunkPayload;
    private final byte[][] subChunkPayloads;

    public ChunkBlobCache(long[] blobIds, Long2ObjectMap<byte[]> blobs, byte[] fullChunkPayload, byte[] subRequestModeFullChunkPayload, byte[][] subChunkPayloads) {
        this.blobIds = blobIds;
        this.blobs = blobs;
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
