package cn.nukkit.network.protocol;

public class EntityPickRequestPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.ENTITY_PICK_REQUEST_PACKET;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        //TODO
    }
}
