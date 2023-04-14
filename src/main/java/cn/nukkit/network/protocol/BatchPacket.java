package cn.nukkit.network.protocol;

import lombok.ToString;

import javax.annotation.Nullable;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString(exclude = "payload")
public class BatchPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.BATCH_PACKET;

    @Nullable
    public Track[] tracks;

    public byte[] payload;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.payload = this.get();
    }

    @Override
    public void encode() {

    }

    public void trim() {
        setBuffer(null);
    }

    @ToString
    public static class Track {
        public final int packetId;
        public final int size;

        public Track(int packetId, int size) {
            this.packetId = packetId;
            this.size = size;
        }
    }
}
