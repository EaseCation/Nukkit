package cn.nukkit.network.protocol;

public class SubChunkPacket12190 extends SubChunkPacket {

    protected static final int HARDCODE_RESPONSE_COUNT = 1; //TODO

    @Override
    public void encode() {
        super.superReset();
        this.putUnsignedVarInt(this.pid());

        this.putBoolean(this.cacheEnabled);
        this.putVarInt(this.dimension);
        this.putVarInt(this.subChunkX);
        this.putVarInt(this.subChunkY);
        this.putVarInt(this.subChunkZ);

        this.putLInt(HARDCODE_RESPONSE_COUNT);
        for (int i = 0; i < HARDCODE_RESPONSE_COUNT; i++) {
            //TODO: position offset
            this.putByte((byte) 0);
            this.putByte((byte) 0);
            this.putByte((byte) 0);

            this.putByte((byte) this.requestResult);
            if (!this.cacheEnabled || this.requestResult != REQUEST_RESULT_SUCCESS_ALL_AIR) {
                this.putByteArray(this.data);
            }

            this.putByte(this.heightMapType);
            if (this.heightMapType == HEIGHT_MAP_TYPE_HAS_DATA) {
                assert heightMap.length == 16 * 16;
                this.put(this.heightMap);
            }

            this.putByte(this.renderHeightMapType);
            if (this.renderHeightMapType == HEIGHT_MAP_TYPE_HAS_DATA) {
                assert renderHeightMap.length == 16 * 16;
                this.put(this.renderHeightMap);
            }

            if (this.cacheEnabled) {
                this.putLLong(this.blobId);
            }
        }
    }
}
