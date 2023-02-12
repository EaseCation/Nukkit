package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * Created on 15-10-22.
 */
@ToString
public class SetEntityLinkPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.SET_ACTOR_LINK_PACKET;

    public static final byte TYPE_REMOVE = 0;
    public static final byte TYPE_RIDE = 1;
    public static final byte TYPE_PASSENGER = 2;

    //TODO: 使用EntityLink类以便在多版本助手中直接重写putEntityLink方法
    public long vehicleUniqueId; //from
    public long riderUniqueId; //to
    public byte type;
    public byte immediate;
    public boolean riderInitiated;

    @Override
    public void decode() {
        this.vehicleUniqueId = this.getEntityUniqueId();
        this.riderUniqueId = this.getEntityUniqueId();
        this.type = (byte) this.getByte();
        this.immediate = (byte) this.getByte();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityUniqueId(this.vehicleUniqueId);
        this.putEntityUniqueId(this.riderUniqueId);
        this.putByte(this.type);
        this.putByte(this.immediate);
    }

    @Override
    public int pid() {
        return NETWORK_ID;
    }
}
