package cn.nukkit.level.format.generic;

import cn.nukkit.network.protocol.BatchPacket;

public class ChunkPacketCache {

    private final BatchPacket packet116;
    private final BatchPacket packet;
    private final BatchPacket packetOld;

    public ChunkPacketCache(BatchPacket packet116, BatchPacket packet, BatchPacket packetOld) {
        this.packet116 = packet116;
        this.packet = packet;
        this.packetOld = packetOld;
    }

    public BatchPacket getPacket116() {
        return packet116;
    }

    public BatchPacket getPacket() {
        return packet;
    }

    public BatchPacket getPacketOld() {
        return packetOld;
    }

    public void compress() {
        this.packet.trim();
        this.packetOld.trim();
    }
}
