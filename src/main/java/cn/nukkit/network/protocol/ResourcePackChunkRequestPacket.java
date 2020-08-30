package cn.nukkit.network.protocol;

public class ResourcePackChunkRequestPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.RESOURCE_PACK_CHUNK_REQUEST_PACKET;

    public String packId;
    public int chunkIndex;

    @Override
    public void decode() {
        this.packId = this.getString();
        this.chunkIndex = this.getLInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.packId);
        this.putLInt(this.chunkIndex);
    }

    @Override
    public int pid() {
        return NETWORK_ID;
    }
}
