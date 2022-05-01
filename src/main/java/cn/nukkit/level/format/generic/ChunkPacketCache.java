package cn.nukkit.level.format.generic;

import cn.nukkit.level.GlobalBlockPaletteInterface.StaticVersion;
import cn.nukkit.network.protocol.BatchPacket;

import java.util.Map;

public class ChunkPacketCache {

    private final Map<StaticVersion, BatchPacket> packets; //1.16.100+ static runtime block palette
    private final Map<StaticVersion, BatchPacket[]> subChunkPackets; // 1.18+ sub chunk packet

    private final BatchPacket subModePacketNew; // 1.18.30+
    /**
     * LevelChunkPacket in sub-chunk request mode.
     */
    private final BatchPacket subModePacket;
    private final BatchPacket subModePacketTruncatedNew; // 1.18.30+
    private final BatchPacket subModePacketTruncated;
    private final BatchPacket packet116;
    private final BatchPacket packet;
    private final BatchPacket packetOld;

    public ChunkPacketCache(Map<StaticVersion, BatchPacket> packets, Map<StaticVersion, BatchPacket[]> subChunkPackets, BatchPacket subModePacketNew, BatchPacket subModePacket, BatchPacket subModePacketTruncatedNew, BatchPacket subModePacketTruncated, BatchPacket packet116, BatchPacket packet, BatchPacket packetOld) {
        this.packets = packets;
        this.subChunkPackets = subChunkPackets;
        this.subModePacketNew = subModePacketNew;
        this.subModePacket = subModePacket;
        this.subModePacketTruncatedNew = subModePacketTruncatedNew;
        this.subModePacketTruncated = subModePacketTruncated;
        this.packet116 = packet116;
        this.packet = packet;
        this.packetOld = packetOld;
    }

    public BatchPacket getPacket(StaticVersion version) {
        return this.packets.get(version);
    }

    public BatchPacket[] getSubPackets(StaticVersion version) {
        return this.subChunkPackets.get(version);
    }

    public BatchPacket getSubModePacketNew() {
        return subModePacketNew;
    }

    public BatchPacket getSubModePacket() {
        return subModePacket;
    }

    public BatchPacket getSubModePacketTruncatedNew() {
        return subModePacketTruncatedNew;
    }

    public BatchPacket getSubModePacketTruncated() {
        return subModePacketTruncated;
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
        this.subChunkPackets.clear();
        this.subModePacketNew.trim();
        this.subModePacket.trim();
        this.subModePacketTruncatedNew.trim();
        this.subModePacketTruncated.trim();
        this.packet116.trim();
        this.packet.trim();
        this.packetOld.trim();
    }
}
