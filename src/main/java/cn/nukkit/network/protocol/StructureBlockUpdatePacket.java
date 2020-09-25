package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class StructureBlockUpdatePacket extends DataPacket {

    @Override
    public int pid() {
        return ProtocolInfo.STRUCTURE_BLOCK_UPDATE_PACKET;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        //TODO
    }
}
