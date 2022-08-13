package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class SetLastHurtByPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.SET_LAST_HURT_BY_PACKET;

    public int entityType;

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
        this.putVarInt(this.entityType);
    }
}
