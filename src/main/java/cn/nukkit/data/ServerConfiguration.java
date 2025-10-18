package cn.nukkit.data;

import cn.nukkit.network.CompressionAlgorithm;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Value;

@Builder
@Value
public class ServerConfiguration {
    String serverIp;
    int serverPort;
    boolean enableWhitelist;
    String motd;
    boolean forceResources;
    int gameMode;
    boolean forceGameMode;
    boolean hardcore;
    int difficulty;
    boolean pvp;
    int viewDistance;
    String levelType;
    String generatorSettings;
    @Default
    byte compressionAlgorithm = CompressionAlgorithm.SNAPPY;

    int chunkSpawnThreshold;
    int chunkSendingPerTick;
    int chunkTickingPerTick;
    int chunkTickRadius;
    int chunkGenerationQueueSize;
    int chunkPopulationQueueSize;
    boolean clearChunksOnTick;
    boolean cacheChunks;
    boolean lightUpdates;
    boolean savePlayerData;
}
