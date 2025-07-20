package cn.nukkit.level.format.generic;

import cn.nukkit.level.GlobalBlockPaletteInterface.StaticVersion;
import cn.nukkit.network.protocol.BatchPacket;
import cn.nukkit.network.protocol.LevelChunkPacket12060;
import cn.nukkit.network.protocol.SubChunkPacket;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class ChunkPacketCache {

    private final Map<StaticVersion, BatchPacket> fullChunkPackets;
    private final Map<StaticVersion, LevelChunkPacket12060> fullChunkPacketsUncompressed; // 1.20.60+

    private final BatchPacket subRequestModeFullChunkPacket; // 1.18.0-1.20.50
    private final LevelChunkPacket12060 subRequestModeFullChunkPacketUncompressed; // 1.21.40+
    private final LevelChunkPacket12060 subRequestModeFullChunkPacketUncompressedLegacy; // 1.20.60-1.21.30
    private final Map<StaticVersion, BatchPacket[]> subChunkPackets;
    private final Map<StaticVersion, SubChunkPacket[]> subChunkPacketsUncompressed;

    private final Set<StaticVersion> requestedVersions;

    public ChunkPacketCache(
            Map<StaticVersion, BatchPacket> fullChunkPackets,
            Map<StaticVersion, LevelChunkPacket12060> fullChunkPacketsUncompressed,
            BatchPacket subRequestModeFullChunkPacket,
            LevelChunkPacket12060 subRequestModeFullChunkPacketUncompressed,
            LevelChunkPacket12060 subRequestModeFullChunkPacketUncompressedLegacy,
            Map<StaticVersion, BatchPacket[]> subChunkPackets,
            Map<StaticVersion, SubChunkPacket[]> subChunkPacketsUncompressed,
            Set<StaticVersion> requestedVersions) {
        this.fullChunkPackets = fullChunkPackets;
        this.fullChunkPacketsUncompressed = fullChunkPacketsUncompressed;
        this.subRequestModeFullChunkPacket = subRequestModeFullChunkPacket;
        this.subRequestModeFullChunkPacketUncompressed = subRequestModeFullChunkPacketUncompressed;
        this.subRequestModeFullChunkPacketUncompressedLegacy = subRequestModeFullChunkPacketUncompressedLegacy;
        this.subChunkPackets = subChunkPackets;
        this.subChunkPacketsUncompressed = subChunkPacketsUncompressed;
        this.requestedVersions = requestedVersions;
    }

    @Nullable
    public BatchPacket getFullChunkPacket(StaticVersion version) {
        return this.fullChunkPackets.get(version);
    }

    @Nullable
    public LevelChunkPacket12060 getFullChunkPacketUncompressed(StaticVersion version) {
        return this.fullChunkPacketsUncompressed.get(version);
    }

    public BatchPacket getSubRequestModeFullChunkPacket() {
        return subRequestModeFullChunkPacket;
    }

    public LevelChunkPacket12060 getSubRequestModeFullChunkPacketUncompressed() {
        return subRequestModeFullChunkPacketUncompressed;
    }

    public LevelChunkPacket12060 getSubRequestModeFullChunkPacketUncompressedLegacy() {
        return subRequestModeFullChunkPacketUncompressedLegacy;
    }

    @Nullable
    public BatchPacket[] getSubChunkPackets(StaticVersion version) {
        return this.subChunkPackets.get(version);
    }

    @Nullable
    public SubChunkPacket[] getSubChunkPacketsUncompressed(StaticVersion version) {
        return this.subChunkPacketsUncompressed.get(version);
    }

    public boolean hasRequested(StaticVersion blockVersion) {
        return requestedVersions.contains(blockVersion);
    }
}
