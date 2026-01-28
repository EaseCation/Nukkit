package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.protocol.types.StructureEditorData;
import lombok.ToString;

@ToString
public class StructureBlockUpdatePacket extends DataPacket {
    public int x;
    public int y;
    public int z;
    public StructureEditorData data;
    public boolean powered;
    /**
     * @since 1.19.30
     */
    public boolean waterlogged;

    @Override
    public int pid() {
        return ProtocolInfo.STRUCTURE_BLOCK_UPDATE_PACKET;
    }

    @Override
    public void decode() {
        BlockVector3 pos = getBlockVector3();
        x = pos.getX();
        y = pos.getY();
        z = pos.getZ();
        data = getStructureEditorData();
        powered = getBoolean();
    }

    @Override
    public void encode() {
    }
}
