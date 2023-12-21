package cn.nukkit.network.protocol;

import cn.nukkit.Server;
import cn.nukkit.network.Network;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.Zlib;
import com.nukkitx.network.raknet.RakNetReliability;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class DataPacket extends BinaryStream implements Cloneable {

    public static final int CHANNEL_BASE = 0;
    public static final int CHANNEL_PLAYER_MOVING = 1;
    public static final int CHANNEL_MOVING = 2;
    public static final int CHANNEL_TEXT = 3;
    public static final int CHANNEL_BATCH = 4;

    public volatile boolean isEncoded = false;
    private int channel = CHANNEL_BASE;

    public RakNetReliability reliability = RakNetReliability.RELIABLE_ORDERED;

    public Throwable stack = null;

    public abstract int pid();

    public abstract void decode();

    protected abstract void encode();

    public void tryEncode() {
        if (!this.isEncoded) {
            this.isEncoded = true;
            this.encode();
        }
    }

    @Override
    public void reset() {
        super.reset();
        this.putByte((byte) this.pid());
        this.putShort(0);
    }

    public DataPacket setChannel(int channel) {
        this.channel = channel;
        return this;
    }

    public int getChannel() {
        return channel;
    }

    public DataPacket setReliability(RakNetReliability reliability) {
        this.reliability = reliability;
        return this;
    }

    public DataPacket clean() {
        this.setBuffer(null);
        this.setOffset(0);
        this.isEncoded = false;
        return this;
    }

    @Override
    public DataPacket clone() {
        try {
            return (DataPacket) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public BatchPacket compress() {
        return compress(Server.getInstance().networkCompressionLevel);
    }

    public BatchPacket compress(int level) {
        return compress(level, false);
    }

    public BatchPacket compress(boolean zlibRaw) {
        return compress(Server.getInstance().networkCompressionLevel, zlibRaw);
    }

    public BatchPacket compress(int level, boolean zlibRaw) {
        BatchPacket batch = new BatchPacket();
        byte[][] batchPayload = new byte[2][];
        byte[] buf = getBuffer();
        batchPayload[0] = Binary.writeUnsignedVarInt(buf.length);
        batchPayload[1] = buf;
        try {
            batch.payload = zlibRaw ? Network.deflateRaw(batchPayload, level) : Zlib.deflate(batchPayload, level);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return batch;
    }
}
