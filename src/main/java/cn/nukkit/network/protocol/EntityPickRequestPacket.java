package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class EntityPickRequestPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.ENTITY_PICK_REQUEST_PACKET;

    public long entityId;
    public int hotbarSlot;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.entityId = this.getLLong();
        this.hotbarSlot = this.getByte();
    }

    @Override
    public void encode() {
    }
}
