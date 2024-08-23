package cn.nukkit.network.protocol;

import lombok.ToString;

// A wild TransferPacket appeared!
@ToString
public class TransferPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.TRANSFER_PACKET;

    public String address; // Server address
    public int port = 19132; // Server port
    public boolean reloadWorld;

    @Override
    public void decode() {
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(address);
        this.putLShort(port);
    }

    @Override
    public int pid() {
        return ProtocolInfo.TRANSFER_PACKET;
    }
}
