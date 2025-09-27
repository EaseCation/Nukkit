package cn.nukkit.network.protocol;

import cn.nukkit.Server;
import cn.nukkit.network.Compressor;
import cn.nukkit.utils.Binary;
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

    public static BatchPacket compress(Compressor compressor, DataPacket... packets) {
        return compress(compressor, Server.getInstance().networkCompressionLevel, packets);
    }

    public static BatchPacket compress(Compressor compressor, int compressionLevel, DataPacket... packets) {
        int count = packets.length;
        Track[] tracks = new Track[count];

        byte[][] payload = new byte[2 * count][];
        for (int i = 0; i < count; i++) {
            DataPacket packet = packets[i];
            packet.tryEncode();
            byte[] buffer = packet.getBuffer();
            int index = 2 * i;
            payload[index] = Binary.writeUnsignedVarInt(buffer.length);
            payload[index + 1] = buffer;

            tracks[i] = new Track(packet.pid(), packet.getCount());
        }

        BatchPacket batch = new BatchPacket();
        try {
            batch.payload = compressor.compress(payload, compressionLevel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        batch.tracks = tracks;
        return batch;
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
