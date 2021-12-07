package cn.nukkit.level.format.generic;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

public class ChunkBlobCache {

    private final int subChunkCount;
    private final long[] blobIds;
    private final long[] extendedBlobIds; // 1.18+
    private final Long2ObjectOpenHashMap<byte[]> clientBlobs;
    private final Long2ObjectOpenHashMap<byte[]> extendedClientBlobs; // 1.18+
    private final byte[] clientBlobCachedPayload;

    public ChunkBlobCache(int subChunkCount, long[] blobIds, long[] extendedBlobIds, Long2ObjectOpenHashMap<byte[]> clientBlobs, Long2ObjectOpenHashMap<byte[]> extendedClientBlobs, byte[] clientBlobCachedPayload) {
        this.subChunkCount = subChunkCount;
        this.blobIds = blobIds;
        this.extendedBlobIds = extendedBlobIds;
        this.clientBlobs = clientBlobs;
        this.extendedClientBlobs = extendedClientBlobs;
        this.clientBlobCachedPayload = clientBlobCachedPayload;
    }

    public int getSubChunkCount() {
        return subChunkCount;
    }

    public long[] getBlobIds() {
        return blobIds;
    }

    public long[] getExtendedBlobIds() {
        return extendedBlobIds;
    }

    public Long2ObjectOpenHashMap<byte[]> getClientBlobs() {
        return clientBlobs;
    }

    public Long2ObjectOpenHashMap<byte[]> getExtendedClientBlobs() {
        return extendedClientBlobs;
    }

    public byte[] getClientBlobCachedPayload() {
        return clientBlobCachedPayload;
    }

    //TODO: add support for 1.18 sub-chunk requests later -- 11/27/2021
}
