package cn.nukkit.network.protocol;

public class LevelChunkPacket12060 extends LevelChunkPacket {
    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.chunkX);
        this.putVarInt(this.chunkZ);
        this.putVarInt(this.dimension);
        this.putUnsignedVarInt(this.subChunkCount);
        if (this.subChunkCount == CLIENT_REQUEST_TRUNCATED_COLUMN_FAKE_COUNT) {
            this.putLShort(this.subChunkRequestLimit);
        }
        this.putBoolean(this.cacheEnabled);
        if (this.cacheEnabled) {
            this.putUnsignedVarInt(this.blobIds.length);

            for (long blobId : this.blobIds) {
                this.putLLong(blobId);
            }
        }
        this.putByteArray(this.data);
    }
}
