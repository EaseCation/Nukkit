package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
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
    }

    @Override
    public int pid() {
        return NETWORK_ID;
    }
}
