package cn.nukkit.network.protocol;

import cn.nukkit.utils.BinaryStream;
import lombok.ToString;

@ToString
public class ResourcePackClientResponsePacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.RESOURCE_PACK_CLIENT_RESPONSE_PACKET;

    public static final byte STATUS_REFUSED = 1;
    public static final byte STATUS_SEND_PACKS = 2;
    public static final byte STATUS_HAVE_ALL_PACKS = 3;
    public static final byte STATUS_COMPLETED = 4;

    public byte responseStatus;
    public String[] packIds;

    @Override
    public void decode() {
        this.responseStatus = (byte) this.getByte();
        this.packIds = this.getArrayLShort(String.class, BinaryStream::getString);
    }

    @Override
    public void encode() {
    }

    @Override
    public int pid() {
        return NETWORK_ID;
    }
}
