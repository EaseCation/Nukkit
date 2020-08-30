package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ContainerClosePacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.CONTAINER_CLOSE_PACKET;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    public int windowId;

    @Override
    public void decode() {
        this.windowId = (byte) this.getByte();
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte((byte) this.windowId);
    }
}
