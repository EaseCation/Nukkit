package cn.nukkit.level.format.generic;

import cn.nukkit.level.GlobalBlockPaletteInterface.StaticVersion;
import cn.nukkit.network.protocol.BatchPacket;
import cn.nukkit.network.protocol.SubChunkPacket;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class ChunkPacketCache {

    private final Map<StaticVersion, BatchPacket> fullChunkPackets;

    private final BatchPacket subRequestModeFullChunkPacket;
    private final Map<StaticVersion, BatchPacket[]> subChunkPackets;
    private final Map<StaticVersion, SubChunkPacket[]> subChunkPacketsUncompressed;

    private final Set<StaticVersion> requestedVersions;

    public ChunkPacketCache(Map<StaticVersion, BatchPacket> fullChunkPackets, BatchPacket subRequestModeFullChunkPacket, Map<StaticVersion, BatchPacket[]> subChunkPackets, Map<StaticVersion, SubChunkPacket[]> subChunkPacketsUncompressed, Set<StaticVersion> requestedVersions) {
        this.fullChunkPackets = fullChunkPackets;
        this.subRequestModeFullChunkPacket = subRequestModeFullChunkPacket;
        this.subChunkPackets = subChunkPackets;
        this.subChunkPacketsUncompressed = subChunkPacketsUncompressed;
        this.requestedVersions = requestedVersions;
    }

    @Nullable
    public BatchPacket getFullChunkPacket(StaticVersion version) {
        return this.fullChunkPackets.get(version);
    }

    public BatchPacket getSubRequestModeFullChunkPacket() {
        return subRequestModeFullChunkPacket;
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
