package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class RiderJumpPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.PASSENGER_JUMP_PACKET;

    public int strength;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.strength = this.getVarInt();
    }

    @Override
    public void encode() {
    }
}
