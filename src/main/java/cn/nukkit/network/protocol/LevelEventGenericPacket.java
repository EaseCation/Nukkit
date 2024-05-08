package cn.nukkit.network.protocol;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.ToString;

import java.io.IOException;
import java.nio.ByteOrder;

@ToString
public class LevelEventGenericPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.LEVEL_EVENT_GENERIC_PACKET;

    public int eventId;
    public CompoundTag tag;

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
        this.putVarInt(eventId);

        if (this.tag != null) {
            this.tag.getTags().forEach((name, tag) -> {
                byte[] nbt;
                try {
                    nbt = NBTIO.write(tag, ByteOrder.LITTLE_ENDIAN, true);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                this.put(nbt);
            });
        }
        this.putByte((byte) 0); // End tag
    }
}
