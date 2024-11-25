package cn.nukkit.network.protocol;

import cn.nukkit.network.protocol.types.BlockChangeEntry;
import lombok.ToString;

@ToString
public class UpdateSubChunkBlocksPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.UPDATE_SUB_CHUNK_BLOCKS_PACKET;

    public int subChunkBlockX;
    public int subChunkBlockY;
    public int subChunkBlockZ;
    public BlockChangeEntry[] layer0 = new BlockChangeEntry[0];
    public BlockChangeEntry[] layer1 = new BlockChangeEntry[0];

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
        this.putBlockVector3(this.subChunkBlockX, this.subChunkBlockY, this.subChunkBlockZ);

        this.putUnsignedVarInt(this.layer0.length);
        for (BlockChangeEntry entry : this.layer0) {
            writeEntry(entry);
        }

        this.putUnsignedVarInt(this.layer1.length);
        for (BlockChangeEntry entry : this.layer1) {
            writeEntry(entry);
        }
    }

    private void writeEntry(BlockChangeEntry entry) {
        this.putBlockVector3(entry.x(), entry.y(), entry.z());
        this.putUnsignedVarInt(entry.block());
        this.putUnsignedVarInt(entry.flags());
        this.putUnsignedVarLong(entry.syncedUpdateEntityUniqueId());
        this.putUnsignedVarInt(entry.syncedUpdateType());
    }
}
