package cn.nukkit.level.format.generic;

import cn.nukkit.level.GlobalBlockPaletteInterface.StaticVersion;
import cn.nukkit.network.protocol.BatchPacket;

import java.util.Map;

public class ChunkPacketCache {

    private final Map<StaticVersion, BatchPacket> packets; //1.16.100+ static runtime block palette

    private final BatchPacket packet116;
    private final BatchPacket packet;
    private final BatchPacket packetOld;

    public ChunkPacketCache(Map<StaticVersion, BatchPacket> packets, BatchPacket packet116, BatchPacket packet, BatchPacket packetOld) {
        this.packets = packets;
        this.packet116 = packet116;
        this.packet = packet;
        this.packetOld = packetOld;
    }

    public BatchPacket getPacket(StaticVersion version) {
        return this.packets.get(version);
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
        this.packets.clear();
        this.packet116.trim();
        this.packet.trim();
        this.packetOld.trim();
    }
}
