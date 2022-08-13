package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString(exclude = "data")
public class ResourcePackChunkDataPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.RESOURCE_PACK_CHUNK_DATA_PACKET;

    public String packId;
    public int chunkIndex;
    public long progress;
    public byte[] data;

    @Override
    public void decode() {
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.packId);
        this.putLInt(this.chunkIndex);
        this.putLLong(this.progress);
        this.putLInt(this.data.length);
        this.put(this.data);
    }

    @Override
    public int pid() {
        return NETWORK_ID;
    }
}
