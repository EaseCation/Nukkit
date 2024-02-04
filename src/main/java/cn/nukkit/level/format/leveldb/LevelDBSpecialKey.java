package cn.nukkit.level.format.leveldb;

import java.nio.charset.StandardCharsets;

public final class LevelDBSpecialKey {
    public static final byte[] OVERWORLD = "Overworld".getBytes(StandardCharsets.UTF_8);
    public static final byte[] NETHER = "Nether".getBytes(StandardCharsets.UTF_8);
    public static final byte[] THE_END = "TheEnd".getBytes(StandardCharsets.UTF_8);
    public static final byte[] BIOME_DATA = "BiomeData".getBytes(StandardCharsets.UTF_8);
    public static final byte[] PORTALS = "portals".getBytes(StandardCharsets.UTF_8);
    public static final byte[] SCOREBOARD = "scoreboard".getBytes(StandardCharsets.UTF_8);
    public static final byte[] MOB_EVENTS = "mobevents".getBytes(StandardCharsets.UTF_8);
    public static final byte[] LEVEL_CHUNK_META_DATA_DICTIONARY = "LevelChunkMetaDataDictionary".getBytes(StandardCharsets.UTF_8);
    public static final byte[] SCHEDULER_WT = "schedulerWT".getBytes(StandardCharsets.UTF_8);

//    public static final byte[] LOCAL_PLAYER = "~local_player".getBytes(StandardCharsets.UTF_8);

    private LevelDBSpecialKey() {
        throw new IllegalStateException();
    }
}
