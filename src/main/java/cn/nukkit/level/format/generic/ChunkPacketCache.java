package cn.nukkit.level.format.generic;

import cn.nukkit.level.GlobalBlockPaletteInterface.StaticVersion;
import cn.nukkit.network.protocol.BatchPacket;
import cn.nukkit.network.protocol.LevelChunkPacket12060;
import cn.nukkit.network.protocol.SubChunkPacket;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class ChunkPacketCache {

    private final Map<StaticVersion, BatchPacket> packets; //1.16.100+ static runtime block palette
    private final Map<StaticVersion, LevelChunkPacket12060> fullChunkPacketsUncompressed; // 1.20.60+
    private final Map<StaticVersion, BatchPacket[]> subChunkPackets; // 1.18+ sub chunk packet
    private final Map<StaticVersion, SubChunkPacket[]> subChunkPacketsUncompressed;

    private final BatchPacket subModePacketNew; // 1.18.30+
    /**
     * LevelChunkPacket in sub-chunk request mode.
     */
    private final BatchPacket subModePacket;
    private final BatchPacket subModePacketTruncatedNew; // 1.18.30+
    private final BatchPacket subModePacketTruncated;
    private final LevelChunkPacket12060 subModePacketUncompressed; // 1.20.60+
    private final BatchPacket packet116;
    private final BatchPacket packet;
    private final BatchPacket packetOld;

    private final Set<StaticVersion> requestedVersions;

    public ChunkPacketCache(Map<StaticVersion, BatchPacket> packets, Map<StaticVersion, LevelChunkPacket12060> fullChunkPacketsUncompressed, Map<StaticVersion, BatchPacket[]> subChunkPackets, Map<StaticVersion, SubChunkPacket[]> subChunkPacketsUncompressed, BatchPacket subModePacketNew, BatchPacket subModePacket, BatchPacket subModePacketTruncatedNew, BatchPacket subModePacketTruncated, LevelChunkPacket12060 subModePacketUncompressed, BatchPacket packet116, BatchPacket packet, BatchPacket packetOld, Set<StaticVersion> requestedVersions) {
        this.packets = packets;
        this.fullChunkPacketsUncompressed = fullChunkPacketsUncompressed;
        this.subChunkPackets = subChunkPackets;
        this.subChunkPacketsUncompressed = subChunkPacketsUncompressed;
        this.subModePacketNew = subModePacketNew;
        this.subModePacket = subModePacket;
        this.subModePacketTruncatedNew = subModePacketTruncatedNew;
        this.subModePacketTruncated = subModePacketTruncated;
        this.subModePacketUncompressed = subModePacketUncompressed;
        this.packet116 = packet116;
        this.packet = packet;
        this.packetOld = packetOld;
        this.requestedVersions = requestedVersions;
    }

    @Nullable
    public BatchPacket getPacket(StaticVersion version) {
        return this.packets.get(version);
    }

    @Nullable
    public LevelChunkPacket12060 getPacketUncompressed(StaticVersion version) {
        return this.fullChunkPacketsUncompressed.get(version);
    }

    @Nullable
    public BatchPacket[] getSubPackets(StaticVersion version) {
        return this.subChunkPackets.get(version);
    }

    @Nullable
    public SubChunkPacket[] getSubPacketsUncompressed(StaticVersion version) {
        return this.subChunkPacketsUncompressed.get(version);
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

    public LevelChunkPacket12060 getSubModePacketUncompressed() {
        return subModePacketUncompressed;
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

    public boolean hasRequested(StaticVersion blockVersion) {
        return requestedVersions.contains(blockVersion);
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
