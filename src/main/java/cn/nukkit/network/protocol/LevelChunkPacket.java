package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString(exclude = "data")
public class LevelChunkPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.LEVEL_CHUNK_PACKET;

    /**
     * Client will request all sub-chunks as needed up to the top of the world.
     */
    public static final int CLIENT_REQUEST_FULL_COLUMN_FAKE_COUNT = -1;
    /**
     * Client will request sub-chunks as needed up to the height written in the packet, and assume that anything above that height is air.
     */
    public static final int CLIENT_REQUEST_TRUNCATED_COLUMN_FAKE_COUNT = -2;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    public int chunkX;
    public int chunkZ;
    public int subChunkCount;
    public boolean cacheEnabled;
    public long[] blobIds;
    public byte[] data;

    /**
     * @since 1.18.10
     */
    public int subChunkRequestLimit;

    @Override
    public void decode() {

    }

    @Override
    public void reset() {
        super.superReset();
        this.putUnsignedVarInt(this.pid());
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.chunkX);
        this.putVarInt(this.chunkZ);
        this.putUnsignedVarInt(this.subChunkCount);
        if (this.subChunkCount == CLIENT_REQUEST_TRUNCATED_COLUMN_FAKE_COUNT) {
            this.putLShort(this.subChunkRequestLimit);
        }
        this.putBoolean(cacheEnabled);
        if (this.cacheEnabled) {
            this.putUnsignedVarInt(blobIds.length);

            for (long blobId : blobIds) {
                this.putLLong(blobId);
            }
        }
        this.putByteArray(this.data);
    }
}
