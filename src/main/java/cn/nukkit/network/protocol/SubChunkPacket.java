package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString(exclude = {"data", "heightMap"})
public class SubChunkPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.SUB_CHUNK_PACKET;

    public static final int REQUEST_RESULT_SUCCESS = 1;
    public static final int REQUEST_RESULT_NO_SUCH_CHUNK = 2;
    public static final int REQUEST_RESULT_WRONG_DIMENSION = 3;
    public static final int REQUEST_RESULT_PLAYER_NOT_FOUND = 4;
    public static final int REQUEST_RESULT_Y_INDEX_OUT_OF_BOUNDS = 5;
    /**
     * @since 1.18.10
     */
    public static final int REQUEST_RESULT_SUCCESS_ALL_AIR = 6;

    public static final byte HEIGHT_MAP_TYPE_NO_DATA = 0;
    public static final byte HEIGHT_MAP_TYPE_HAS_DATA = 1;
    public static final byte HEIGHT_MAP_TYPE_ALL_TOO_HIGH = 2;
    public static final byte HEIGHT_MAP_TYPE_ALL_TOO_LOW = 3;

    private static final byte[] EMPTY = new byte[0];

    public int dimension;
    public int subChunkX;
    public int subChunkY;
    public int subChunkZ;
    public byte[] data = EMPTY;
    public int requestResult = REQUEST_RESULT_SUCCESS;
    public byte heightMapType = HEIGHT_MAP_TYPE_NO_DATA;
    /**
     * ZZZZXXXX key bit order.
     */
    public byte[] heightMap;
    public boolean cacheEnabled;
    public long blobId;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        super.superReset();
        this.putUnsignedVarInt(this.pid());

        this.putVarInt(this.dimension);
        this.putVarInt(this.subChunkX);
        this.putVarInt(this.subChunkY);
        this.putVarInt(this.subChunkZ);
        this.putByteArray(this.data);
        this.putVarInt(this.requestResult);
        this.putByte(this.heightMapType);
        if (this.heightMapType == HEIGHT_MAP_TYPE_HAS_DATA) {
            assert heightMap.length == 16 * 16;
            this.put(this.heightMap);
        }
        this.putBoolean(this.cacheEnabled);
        if (this.cacheEnabled) {
            this.putLLong(this.blobId);
        }
    }
}
