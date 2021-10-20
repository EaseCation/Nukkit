package cn.nukkit.network.protocol;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import java.io.IOException;
import java.io.UncheckedIOException;
import lombok.ToString;

@ToString(exclude = "namedtag")
public class UpdateEquipmentPacket extends DataPacket {

    public int windowId;
    public int windowType;
    public int size;
    public long eid;
    public CompoundTag namedtag;


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
        try {
            this.put(NBTIO.writeNetwork(namedtag));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
