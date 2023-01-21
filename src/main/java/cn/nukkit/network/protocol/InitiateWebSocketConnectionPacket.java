package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class InitiateWebSocketConnectionPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.AUTOMATION_CLIENT_CONNECT_PACKET;

    private String serverUri;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.serverUri);
    }
}
