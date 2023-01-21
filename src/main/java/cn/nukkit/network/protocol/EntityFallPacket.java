package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class EntityFallPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.ACTOR_FALL_PACKET;

    public long eid;
    public float fallDistance;
    public boolean inVoid;

    @Override
    public void decode() {
        this.eid = this.getEntityRuntimeId();
        this.fallDistance = this.getLFloat();
        this.inVoid = this.getBoolean();
    }

    @Override
    public void encode() {

    }

    @Override
    public int pid() {
        return NETWORK_ID;
    }
}
