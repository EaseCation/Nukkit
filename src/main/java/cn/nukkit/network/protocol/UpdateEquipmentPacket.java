package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString(exclude = "namedtag")
public class UpdateEquipmentPacket extends DataPacket {

    public int windowId;
    public int windowType;
    public int size;
    public long eid;
    public byte[] namedtag;

    @Override
    public int pid() {
        return ProtocolInfo.UPDATE_EQUIPMENT_PACKET;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putByte((byte) this.windowId);
        this.putByte((byte) this.windowType);
        this.putVarInt(size);
        this.putEntityUniqueId(this.eid);
        this.put(this.namedtag);
    }
}
