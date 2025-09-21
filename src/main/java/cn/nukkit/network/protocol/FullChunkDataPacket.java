package cn.nukkit.network.protocol;

import cn.nukkit.level.util.IChunkPos;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString(exclude = "data")
public class FullChunkDataPacket extends DataPacket implements IChunkPos {
    public static final int NETWORK_ID = ProtocolInfo.LEVEL_CHUNK_PACKET;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    public int chunkX;
    public int chunkZ;
    public byte[] data;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.chunkX);
        this.putVarInt(this.chunkZ);
        this.putByteArray(this.data);
    }

    @Override
    public int getChunkX() {
        return chunkX;
    }

    @Override
    public int getChunkZ() {
        return chunkZ;
    }
}
