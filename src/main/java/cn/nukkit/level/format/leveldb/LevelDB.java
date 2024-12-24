package cn.nukkit.level.format.leveldb;

import cn.nukkit.GameVersion;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockSerializer;
import cn.nukkit.block.BlockUpgrader;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.GameRules;
import cn.nukkit.level.HeightRange;
import cn.nukkit.level.Level;
import cn.nukkit.level.LevelCreationOptions;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.LevelProviderManager;
import cn.nukkit.level.format.LevelProviderManager.LevelProviderHandle;
import cn.nukkit.level.format.anvil.util.NibbleArray;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.format.generic.ChunkRequestTask;
import cn.nukkit.level.generator.FlatGeneratorOptions;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.GeneratorOptions;
import cn.nukkit.level.generator.Generators;
import cn.nukkit.level.util.PalettedSubChunkStorage;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.*;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.mutable.MutableInt;
import org.iq80.leveldb.CompressionType;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.WriteBatch;
import org.iq80.leveldb.impl.Iq80DBFactory;

import javax.annotation.Nullable;
import java.io.*;
import java.lang.ref.WeakReference;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

import static cn.nukkit.SharedConstants.*;
import static cn.nukkit.level.format.leveldb.LevelDBKey.*;
import static cn.nukkit.level.format.leveldb.LevelDbConstants.*;
import static net.daporkchop.ldbjni.LevelDB.PROVIDER;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@Log4j2
public class LevelDB implements LevelProvider {
    public static boolean NATIVE_LEVELDB = Boolean.getBoolean("nukkit.nativeLevelDB") || USE_NATIVE_LEVELDB;

    static final HeightRange DEFAULT_HEIGHT_RANGE = HeightRange.blockY(-64, 320);

    protected final Long2ObjectMap<LevelDbChunk> chunks = new Long2ObjectOpenHashMap<>();
    protected final ThreadLocal<WeakReference<LevelDbChunk>> lastChunk = new ThreadLocal<>();

    protected final DB db;

    protected Level level;

    protected final String path;

    protected final CompoundTag levelData;
    private GeneratorOptions generatorOptions;

    protected volatile boolean closed;
    protected final Lock gcLock;

    protected boolean saveChunksOnClose = true;

    public LevelDB(Level level, String path) {
        this.level = level;
        this.path = path;
        Path dirPath = Paths.get(path);
        try {
            Files.createDirectories(dirPath);
        } catch (IOException e) {
            throw new RuntimeException("Unable to create directories " + path, e);
        }

        try (InputStream stream = Files.newInputStream(dirPath.resolve("level.dat"))) {
            stream.skip(8);
            this.levelData = NBTIO.read(stream, ByteOrder.LITTLE_ENDIAN);
        } catch (IOException e) {
            throw new LevelException("Invalid level.dat in " + path, e);
        }

        if (!this.levelData.contains("Generator")) {
//            this.levelData.putInt("Generator", Generator.TYPE_INFINITE);
            this.levelData.putInt("Generator", Generator.TYPE_FLAT);
        }

        if (!this.levelData.contains("generatorOptions")) {
            this.levelData.putString("generatorOptions", "");
        }

        try {
            this.db = openDB(dirPath.resolve("db").toFile());
        } catch (IOException e) {
            throw new RuntimeException("Unable to open LevelDB: " + path, e);
        }

        gcLock = new ReentrantLock();
        if (ENABLE_STORAGE_AUTO_COMPACTION && level.isAutoCompaction()) {
            int delay = level.getServer().getAutoCompactionTicks();
            level.getServer().getScheduler().scheduleDelayedTask(null, new AutoCompactionTask(), delay + ThreadLocalRandom.current().nextInt(delay), true);
        }
    }

    @SuppressWarnings("unused")
    public static String getProviderName() {
        return "leveldb";
    }

    @SuppressWarnings("unused")
    public static byte getProviderOrder() {
        return ORDER_ZXY;
    }

    @SuppressWarnings("unused")
    public static boolean usesChunkSection() {
        return true;
    }

    public static boolean isValid(String path) {
        return new File(path + "/level.dat").exists() && new File(path + "/db").isDirectory();
    }

    @SuppressWarnings("unused")
    public static void generate(String path, String name) throws IOException {
        generate(path, name, LevelCreationOptions.builder().build());
    }

    public static void generate(String path, String name, LevelCreationOptions options) throws IOException {
        Path dirPath = Paths.get(path);
        Path dbPath = dirPath.resolve("db");
        Files.createDirectories(dbPath);

        int generatorType = Generators.getGeneratorType(options.getGenerator());
        Vector3 spawnPosition = options.getSpawnPosition();

        CompoundTag levelData = new CompoundTag();
        updateLevelData(levelData);
        levelData
                .putInt("Generator", generatorType)
                .putString("LevelName", name)
                .putLong("RandomSeed", options.getSeed())
                .putInt("SpawnX", spawnPosition.getFloorX())
                .putInt("SpawnY", spawnPosition.getFloorY())
                .putInt("SpawnZ", spawnPosition.getFloorZ())
//                .putString("baseGameVersion", generatorType != Generator.TYPE_OLD ? "*" : GameVersion.V1_17_40.toString())
                ;
        options.getGameRules().writeBedrockNBT(levelData);

        try (OutputStream stream = Files.newOutputStream(dirPath.resolve("level.dat"))) {
            byte[] data = NBTIO.write(levelData, ByteOrder.LITTLE_ENDIAN);
            stream.write(Binary.writeLInt(CURRENT_STORAGE_VERSION));
            stream.write(Binary.writeLInt(data.length));
            stream.write(data);
        }

        DB db = openDB(dbPath.toFile());
        db.close();
    }

    protected static DB openDB(File dir) throws IOException {
        Options options = new Options()
                .createIfMissing(true)
                .compressionType(CompressionType.ZLIB_RAW)
                .blockSize(64 * 1024);
        return NATIVE_LEVELDB ? PROVIDER.open(dir, options) : Iq80DBFactory.factory.open(dir, options);
    }

    public static void updateLevelData(CompoundTag levelData) {
        levelData.putLong("LastPlayed", System.currentTimeMillis() / 1000)
                .putString("baseGameVersion", "*")
                .putString("InventoryVersion", GameVersion.getFeatureVersion().toString())
                .putInt("NetworkVersion", GameVersion.getFeatureVersion().getProtocol())
                .putList(new ListTag<>("MinimumCompatibleClientVersion", CURRENT_COMPATIBLE_CLIENT_VERSION))
                .putList(new ListTag<>("lastOpenedWithVersion", CURRENT_COMPATIBLE_CLIENT_VERSION))
                .putInt("StorageVersion", CURRENT_STORAGE_VERSION)
                .putInt("WorldVersion", 1);

        levelData.putIntIfAbsent("DayCycleStopTime", -1);
        levelData.putIntIfAbsent("Difficulty", 0);
        levelData.putByteIfAbsent("ForceGameType", 0);
        levelData.putIntIfAbsent("GameType", 0);
        levelData.putIntIfAbsent("Platform", 2);
        levelData.putLongIfAbsent("Time", 0);
        levelData.putByteIfAbsent("eduLevel", 0);
        levelData.putByteIfAbsent("hasBeenLoadedInCreative", 1); // this actually determines whether achievements can be earned in this world
        levelData.putByteIfAbsent("immutableWorld", 0);
        levelData.putFloatIfAbsent("lightningLevel", 0);
        levelData.putIntIfAbsent("lightningTime", 1);
        levelData.putFloatIfAbsent("rainLevel", 0);
        levelData.putIntIfAbsent("rainTime", 1);
        levelData.putByteIfAbsent("spawnMobs", 1);
        levelData.putByteIfAbsent("texturePacksRequired", 0);
        levelData.putLongIfAbsent("currentTick", 1);
        levelData.putIntIfAbsent("LimitedWorldOriginX", 128);
        levelData.putIntIfAbsent("LimitedWorldOriginY", Short.MAX_VALUE);
        levelData.putIntIfAbsent("LimitedWorldOriginZ", 128);
        levelData.putIntIfAbsent("limitedWorldWidth", 16);
        levelData.putIntIfAbsent("limitedWorldDepth", 16);
        levelData.putLongIfAbsent("worldStartCount", ((long) Integer.MAX_VALUE) & 0xffffffffL);
        levelData.putByteIfAbsent("bonusChestEnabled", 0);
        levelData.putByteIfAbsent("bonusChestSpawned", 1);
        levelData.putByteIfAbsent("CenterMapsToOrigin", 0);
        levelData.putByteIfAbsent("commandsEnabled", 1);
        levelData.putByteIfAbsent("ConfirmedPlatformLockedContent", 0);
        levelData.putByteIfAbsent("educationFeaturesEnabled", 0);
        levelData.putIntIfAbsent("eduOffer", 0);
        levelData.putByteIfAbsent("hasLockedBehaviorPack", 0);
        levelData.putByteIfAbsent("hasLockedResourcePack", 0);
        levelData.putByteIfAbsent("isFromLockedTemplate", 0);
        levelData.putByteIfAbsent("isFromWorldTemplate", 0);
        levelData.putByteIfAbsent("isSingleUseWorld", 0);
        levelData.putByteIfAbsent("isWorldTemplateOptionLocked", 0);
        levelData.putByteIfAbsent("LANBroadcast", 1);
        levelData.putByteIfAbsent("LANBroadcastIntent", 1);
        levelData.putByteIfAbsent("MultiplayerGame", 1);
        levelData.putByteIfAbsent("MultiplayerGameIntent", 1);
        levelData.putIntIfAbsent("PlatformBroadcastIntent", 3);
        levelData.putIntIfAbsent("XBLBroadcastIntent", 3);
        levelData.putIntIfAbsent("NetherScale", 8);
        levelData.putStringIfAbsent("prid", "");
        levelData.putByteIfAbsent("requiresCopiedPackRemovalCheck", 0);
        levelData.putIntIfAbsent("serverChunkTickRange", 4);
        levelData.putByteIfAbsent("SpawnV1Villagers", 0);
        levelData.putByteIfAbsent("startWithMapEnabled", 0);
        levelData.putByteIfAbsent("useMsaGamertagsOnly", 0);
        levelData.putIntIfAbsent("permissionsLevel", 0);
        levelData.putIntIfAbsent("playerPermissionsLevel", 1);
        levelData.putByteIfAbsent("isCreatedInEditor", 0);
        levelData.putByteIfAbsent("isExportedFromEditor", 0);
        levelData.putStringIfAbsent("BiomeOverride", "");
        levelData.putStringIfAbsent("FlatWorldLayers", FlatGeneratorOptions.DEFAULT_FLAT_WORLD_LAYERS);
        levelData.putCompoundIfAbsent("world_policies", new CompoundTag());
        levelData.putCompoundIfAbsent("experiments", new CompoundTag()
                .putByte("experiments_ever_used", 0)
                .putByte("saved_with_toggled_experiments", 0));
    }

    @Override
    public void saveLevelData() {
        updateLevelData(levelData);

        try (OutputStream stream = Files.newOutputStream(Paths.get(path, "level.dat"))) {
            byte[] data = NBTIO.write(levelData, ByteOrder.LITTLE_ENDIAN);
            stream.write(Binary.writeLInt(CURRENT_STORAGE_VERSION));
            stream.write(Binary.writeLInt(data.length));
            stream.write(data);
        } catch (IOException e) {
            throw new RuntimeException("Unable to save level.dat: " + path, e);
        }
    }

    @Override
    public LevelProviderHandle getHandle() {
        return LevelProviderManager.LEVELDB;
    }

    @Override
    public LevelDbChunk getEmptyChunk(int chunkX, int chunkZ) {
        return LevelDbChunk.getEmptyChunk(chunkX, chunkZ, this);
    }

    @Override
    public AsyncTask requestChunkTask(int chunkX, int chunkZ) {
        LevelDbChunk chunk = this.getChunk(chunkX, chunkZ, false);
        if (chunk == null) {
//            throw new ChunkException("Invalid Chunk sent: " + chunkX + "," + chunkZ + " in " + path);
            return null;
        }

        return new ChunkRequestTask(chunk);
    }

    @Override
    public void unloadChunks(boolean save) {
        synchronized (this.chunks) {
            Iterator<LevelDbChunk> iter = this.chunks.values().iterator();
            while (iter.hasNext()) {
                iter.next().unload(save, false);
                iter.remove();
            }
        }
    }

    @Override
    public Class<? extends Generator> getGenerator() {
        return Generators.getGenerator(levelData.getInt("Generator"));
    }

    @Override
    public Map<String, Object> getGeneratorOptions() {
        Map<String, Object> options = new Object2ObjectOpenHashMap<>();
        options.put("preset", levelData.getString("generatorOptions"));
        return options;
    }

    @Override
    public BaseFullChunk getLoadedChunk(int chunkX, int chunkZ) {
        LevelDbChunk chunk;
        WeakReference<LevelDbChunk> lastChunk = this.lastChunk.get();
        if (lastChunk != null) {
            chunk = lastChunk.get();
            if (chunk != null && chunk.getProvider() != null && chunk.getX() == chunkX && chunk.getZ() == chunkZ) {
                return chunk;
            }
        }

        long index = Level.chunkHash(chunkX, chunkZ);
        synchronized (this.chunks) {
            chunk = this.chunks.get(index);
            if (chunk != null) {
                this.lastChunk.set(new WeakReference<>(chunk));
            }
        }
        return chunk;
    }

    @Override
    public BaseFullChunk getLoadedChunk(long hash) {
        LevelDbChunk chunk;
        WeakReference<LevelDbChunk> lastChunk = this.lastChunk.get();
        if (lastChunk != null) {
            chunk = lastChunk.get();
            if (chunk != null && chunk.getProvider() != null && chunk.getIndex() == hash) {
                return chunk;
            }
        }

        synchronized (chunks) {
            chunk = this.chunks.get(hash);
            if (chunk != null) {
                this.lastChunk.set(new WeakReference<>(chunk));
            }
        }
        return chunk;
    }

    @Override
    public Map<Long, BaseFullChunk> getLoadedChunks() {
        synchronized (this.chunks) {
            return ImmutableMap.copyOf(chunks);
        }
    }

    @Override
    public Long2ObjectMap<? extends FullChunk> getLoadedChunksUnsafe() {
        return chunks;
    }

    @Override
    public boolean isChunkLoaded(int chunkX, int chunkZ) {
        return this.isChunkLoaded(Level.chunkHash(chunkX, chunkZ));
    }

    @Override
    public boolean isChunkLoaded(long hash) {
        synchronized (this.chunks) {
            return this.chunks.containsKey(hash);
        }
    }

    @Override
    public void saveChunks() {
        synchronized (this.chunks) {
            for (LevelDbChunk chunk : this.chunks.values()) {
                if (chunk.getChanges() == 0) {
                    continue;
                }
                chunk.setChanged(false);
                this.saveChunk(chunk.getX(), chunk.getZ());
            }
        }
    }

    @Override
    public boolean loadChunk(int chunkX, int chunkZ) {
        return this.loadChunk(chunkX, chunkZ, false);
    }

    @Override
    public boolean loadChunk(int chunkX, int chunkZ, boolean create) {
        LevelDbChunk chunk;
        WeakReference<LevelDbChunk> lastChunk = this.lastChunk.get();
        if (lastChunk != null) {
            chunk = lastChunk.get();
            if (chunk != null && chunk.getProvider() != null && chunk.getX() == chunkX && chunk.getZ() == chunkZ) {
                return true;
            }
        }

        long index = Level.chunkHash(chunkX, chunkZ);
        synchronized (this.chunks) {
            chunk = this.chunks.get(index);
            if (chunk != null) {
                this.lastChunk.set(new WeakReference<>(chunk));
                return true;
            }

            try {
                chunk = this.readChunk(chunkX, chunkZ);
            } catch (Exception e) {
                throw new ChunkException("corrupted chunk: " + chunkX + "," + chunkZ + " in " + path, e);
            }

            if (chunk == null && create) {
                chunk = LevelDbChunk.getEmptyChunk(chunkX, chunkZ, this);
            }

            if (chunk != null) {
                this.chunks.put(index, chunk);
                this.lastChunk.set(new WeakReference<>(chunk));
                return true;
            }
        }
        return false;
    }

    @Nullable
    public LevelDbChunk readChunk(int chunkX, int chunkZ) {
        byte[] versionData = this.db.get(NEW_VERSION.getKey(chunkX, chunkZ));
        if (versionData == null || versionData.length == 0) {
            versionData = this.db.get(OLD_VERSION.getKey(chunkX, chunkZ));
            if (versionData == null || versionData.length == 0) {
                return null;
            }
        }
        byte chunkVersion = versionData[0];
        boolean hasBeenUpgraded = chunkVersion < CURRENT_LEVEL_CHUNK_VERSION;
        boolean biomeOrHeightmapUpgraded = false;

        LevelDbSubChunk[] subChunks = new LevelDbSubChunk[LevelDbChunk.SECTION_COUNT];
        short[] heightmap = null;
        PalettedSubChunkStorage[] biomes3d = null;

        int subChunkKeyOffset = chunkVersion >= 24 && chunkVersion <= 26 ? 4 : 0;

        switch (chunkVersion) {
//            case 41: // 1.21.40
                //TODO: BiomeStates became shorts instead of bytes
            case 40: // 1.18.30
            case 39: // 1.18.0.25 beta
            case 38: // 1.18.0.24 beta internal_experimental
            case 37: // 1.18.0.24 beta experimental
            case 36: // 1.18.0.22 beta internal_experimental
            case 35: // 1.18.0.22 beta experimental
            case 34: // 1.18.0.20 beta internal_experimental
            case 33: // 1.18.0.20 beta experimental
            case 32: // 1.17.40
            case 31: // 1.17.40.20 beta experimental
            case 30: // 1.17.30.25 beta internal_experimental
            case 29: // 1.17.30.25 beta experimental
            case 28: // 1.17.30.23 beta internal_experimental
            case 27: // 1.17.30.23 beta experimental
            case 26: // 1.16.230.50 beta internal_experimental
            case 25: // 1.16.230.50 beta experimental
            case 24: // 1.16.220.50 beta internal_experimental
            case 23: // 1.16.220.50 beta experimental
            case 22: // 1.16.210
            case 21: // 1.16.100.57 beta
            case 20: // 1.16.100.52 beta
            case 19: // 1.16.0
            case 18: // 1.16.0.51 beta
            //TODO: check walls
            case 17: // 1.12 hotfix
            case 16: // 1.12.0
            case 15: // 1.12.0.4 beta
            case 14: // 1.11.1.2
            case 13: // 1.11.0.4 beta
            case 12: // 1.11.0.3 beta
            case 11: // 1.11.0.1 beta
            case 10: // 1.9.0
            case 9: // 1.8.0
            case 8: // 1.2.13
            case 7: // 1.2.0
            case 6: // 1.2.0.2 beta
            case 5: // 1.1.0 converted_from_console
            case 4: // 1.1.0
                //TODO: check beds
            case 3: // 1.0.0
                PalettedSubChunkStorage[] convertedLegacyExtraData = this.deserializeLegacyExtraData(chunkX, chunkZ, chunkVersion);

                HeightRange heightRange = getHeightRange();
                int minChunkY = heightRange.getMinChunkY();
                int maxChunkY = heightRange.getMaxChunkY();
                for (int y = minChunkY; y < maxChunkY; ++y) {
                    byte[] subChunkValue = this.db.get(SUBCHUNK.getSubKey(chunkX, chunkZ, y + subChunkKeyOffset));
                    if (subChunkValue == null) {
                        continue;
                    }
                    if (subChunkValue.length == 0) {
                        throw new ChunkException("Unexpected empty data for subchunk " + y + " (" + chunkX + "," + chunkZ + ") in " + path);
                    }
                    BinaryStream stream = new BinaryStream(subChunkValue);

                    int subChunkVersion = stream.getByte();
                    if (subChunkVersion < CURRENT_LEVEL_SUBCHUNK_VERSION) {
                        hasBeenUpgraded = true;
                    }

                    switch (subChunkVersion) {
                        case 8:
                        case 9:
                            int storageCount = stream.getByte();

                            if (subChunkVersion >= 9) {
                                int indexY = stream.getSingedByte();
                                if (indexY != y) {
                                    throw new ChunkException("Unexpected Y index (" + indexY + ") for subchunk " + y + " (" + chunkX + "," + chunkZ + ") in " + path);
                                }
                            }

                            if (storageCount > 0) {
                                PalettedSubChunkStorage[] storages = new PalettedSubChunkStorage[storageCount];
                                for (int i = 0; i < storageCount; ++i) {
                                    storages[i] = PalettedSubChunkStorage.ofBlock(stream);
                                }

                                subChunks[Level.subChunkYtoIndex(y)] = new LevelDbSubChunk(y, storages);
                            }
                            break;
                        case 0:
                        case 2: //these are all identical to version 0, but vanilla respects these so we should also
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                            byte[] blocks = stream.get(4096);
                            NibbleArray blockData = new NibbleArray(stream.get(2048));

                            if (chunkVersion < 4) {
                                stream.setOffset(stream.getOffset() + 4096); //legacy light info, discard it
                            }

                            PalettedSubChunkStorage[] storages = new PalettedSubChunkStorage[2];
                            PalettedSubChunkStorage storage = PalettedSubChunkStorage.ofBlock();
                            for (int i = 0; i < SUB_CHUNK_SIZE; i++) {
                                storage.set(i, (blocks[i] & 0xff) << Block.BLOCK_META_BITS | blockData.get(i));
                            }
                            storages[0] = storage;

                            if (convertedLegacyExtraData != null && convertedLegacyExtraData.length > y) {
                                storages[1] = convertedLegacyExtraData[y];
                            }

                            subChunks[Level.subChunkYtoIndex(y)] = new LevelDbSubChunk(y, storages);
                            break;
                        case 1: //paletted v1, has a single block storage
                            storages = new PalettedSubChunkStorage[2];
                            storages[0] = PalettedSubChunkStorage.ofBlock(stream);

                            if (convertedLegacyExtraData != null && convertedLegacyExtraData.length > y) {
                                storages[1] = convertedLegacyExtraData[y];
                            }

                            subChunks[Level.subChunkYtoIndex(y)] = new LevelDbSubChunk(y, storages);
                            break;
                        default:
                            //TODO: set chunks read-only so the version on disk doesn't get overwritten
                            throw new ChunkException("don't know how to decode LevelDB subchunk format version " + subChunkVersion + " (" + chunkX + "," + chunkZ + ")[" + y + "] in " + path);
                    }
                }

                byte[] maps3d = this.db.get(HEIGHTMAP_AND_3D_BIOMES.getKey(chunkX, chunkZ));
                if (maps3d != null && maps3d.length >= SUB_CHUNK_2D_SIZE * 2) {
                    heightmap = new short[SUB_CHUNK_2D_SIZE];

                    ByteBuf buf = Unpooled.wrappedBuffer(maps3d);
                    try {
                        for (int i = 0; i < SUB_CHUNK_2D_SIZE; i++)  {
                            heightmap[i] = buf.readShortLE();
                        }

                        try {
                            byte[] biome = new byte[buf.readableBytes()];
                            buf.readBytes(biome);

                            BinaryStream stream = new BinaryStream(biome);
                            biomes3d = new PalettedSubChunkStorage[LevelDbChunk.SECTION_COUNT];

                            for (int chunkY = minChunkY; chunkY < maxChunkY && !stream.feof(); chunkY++) {
                                biomes3d[Level.subChunkYtoIndex(chunkY)] = PalettedSubChunkStorage.ofBiome(stream);
                            }
                        } catch (Exception e) {
                            log.debug("Failed to deserialize biome palette ({},{}) in {}", chunkX, chunkZ, path, e);
                        }
                    } catch (Exception e) {
                        heightmap = null;
                        log.debug("Failed to deserialize heightmap ({},{}) in {}", chunkX, chunkZ, path, e);
                    } finally {
                        buf.release();
                    }
                } else {
                    // Converts 2D biomes into a 3D biome palette
                    byte[] maps2d = this.db.get(HEIGHTMAP_AND_2D_BIOMES.getKey(chunkX, chunkZ));
                    if (maps2d != null && maps2d.length >= SUB_CHUNK_2D_SIZE * 2 + SUB_CHUNK_2D_SIZE) {
                        biomeOrHeightmapUpgraded = true;

                        int heightmapOffset = heightRange.getYIndexOffset();
                        heightmap = new short[SUB_CHUNK_2D_SIZE];
                        byte[] biome = new byte[SUB_CHUNK_2D_SIZE];

                        ByteBuf buf = Unpooled.wrappedBuffer(maps2d);
                        try {
                            for (int i = 0; i < SUB_CHUNK_2D_SIZE; i++)  {
                                heightmap[i] = (short) (buf.readShortLE() + heightmapOffset);
                            }

                            try {
                                buf.readBytes(biome);

                                // This palette can then be cloned for every subchunk.
                                PalettedSubChunkStorage storage = PalettedSubChunkStorage.ofBiome(biome[0]);
                                for (int x = 0; x < 16; x++) {
                                    for (int z = 0; z < 16; z++) {
                                        int biomeId = Biome.toValidBiome(biome[LevelDbChunk.index2d(x, z)] & 0xff);
                                        for (int y = 0; y < 16; y++) {
                                            storage.set(x, y, z, biomeId);
                                        }
                                    }
                                }

                                biomes3d = new PalettedSubChunkStorage[LevelDbChunk.SECTION_COUNT];
                                biomes3d[Level.subChunkYtoIndex(minChunkY)] = storage;
                            } catch (Exception e) {
                                log.debug("Failed to deserialize biome ({},{}) in {}", chunkX, chunkZ, path, e);
                            }
                        } catch (Exception e) {
                            heightmap = null;
                            log.debug("Failed to deserialize height map ({},{}) in {}", chunkX, chunkZ, path, e);
                        } finally {
                            buf.release();
                        }
                    }
                }

                break;
            case 2: // 0.9.5
            case 1: // 0.9.2
            case 0: // 0.9.0.1 beta (first version)
                convertedLegacyExtraData = this.deserializeLegacyExtraData(chunkX, chunkZ, chunkVersion);

                byte[] legacyTerrain = this.db.get(LEGACY_TERRAIN.getKey(chunkX, chunkZ));
                if (legacyTerrain == null || legacyTerrain.length == 0) {
                    throw new ChunkException("Missing expected legacy terrain data for format version " + chunkVersion + " (" + chunkX + "," + chunkZ + ") in " + path);
                }

                BinaryStream stream = new BinaryStream(legacyTerrain);
                // max height 128
                byte[] blocks = stream.get(8 * SUB_CHUNK_SIZE);
                NibbleArray blockData = new NibbleArray(stream.get(8 * SUB_CHUNK_SIZE / 2));

                for (int y = 0; y < 8; y++) {
                    PalettedSubChunkStorage[] storages = new PalettedSubChunkStorage[2];
                    PalettedSubChunkStorage storage = PalettedSubChunkStorage.ofBlock();
                    for (int i = 0; i < SUB_CHUNK_SIZE; i++) {
                        storage.set(i, (blocks[i] & 0xff) << 4 | blockData.get(i));
                    }

                    if (convertedLegacyExtraData != null && convertedLegacyExtraData.length > y) {
                        storages[1] = convertedLegacyExtraData[y];
                    }

                    subChunks[Level.subChunkYtoIndex(y)] = new LevelDbSubChunk(y, storages);
                }

                // Discard skyLight and blockLight
                stream.skip(8 * SUB_CHUNK_SIZE / 2 + 8 * SUB_CHUNK_SIZE / 2);

                /*heightmap = new short[SUB_CHUNK_2D_SIZE];
                for (int i = 0; i < SUB_CHUNK_2D_SIZE; i++) {
                    heightmap[i] = (short) (stream.getByte() & 0xff);
                }*/
                stream.skip(SUB_CHUNK_2D_SIZE); // recalculate heightmap

                byte[] biome = new byte[SUB_CHUNK_2D_SIZE];
                try {
                    // Converts pre-MCPE-1.0 biome color array to biome ID array.
                    for (int i = 0; i < SUB_CHUNK_2D_SIZE; i++) {
                        biome[i] = (byte) Biome.toValidBiome((stream.getInt() >> 24) & 0xff);
                    }

                    // converts 2D biomes into a 3D biome palette
                    PalettedSubChunkStorage storage = PalettedSubChunkStorage.ofBiome(biome[0]);
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            int biomeId = biome[LevelDbChunk.index2d(x, z)] & 0xff;
                            for (int y = 0; y < 16; y++) {
                                storage.set(x, y, z, biomeId);
                            }
                        }
                    }

                    biomes3d = new PalettedSubChunkStorage[LevelDbChunk.SECTION_COUNT];
                    biomes3d[Level.subChunkYtoIndex(getHeightRange().getMinChunkY())] = storage;
                } catch (Exception e) {
                    log.debug("Failed to deserialize biome color ({},{}) in {}", chunkX, chunkZ, path, e);
                }

                break;
            default:
                //TODO: set chunks read-only so the version on disk doesn't get overwritten
                throw new ChunkException("don't know how to decode chunk format version " + chunkVersion + " (" + chunkX + "," + chunkZ + ") in " + path);
        }

        List<CompoundTag> blockEntities = new ObjectArrayList<>();
        byte[] blockEntityData = this.db.get(BLOCK_ENTITIES.getKey(chunkX, chunkZ));
        if (blockEntityData != null && blockEntityData.length != 0) {
            try (NBTInputStream nbtStream = new NBTInputStream(new ByteArrayInputStream(blockEntityData), ByteOrder.LITTLE_ENDIAN, false)) {
                while (nbtStream.available() > 0) {
                    Tag tag = Tag.readNamedTag(nbtStream);
                    if (!(tag instanceof CompoundTag)) {
                        throw new IOException("Block entity root tag must be a compound tag (" + chunkX + "," + chunkZ + ") in " + path);
                    }
                    blockEntities.add((CompoundTag) tag);
                }
            } catch (IOException e) {
                throw new ChunkException("Corrupted block entity data (" + chunkX + "," + chunkZ + ") in " + path, e);
            }
        }

        List<CompoundTag> entities = new ObjectArrayList<>();
        byte[] entityData = this.db.get(ENTITIES.getKey(chunkX, chunkZ));
        if (entityData != null && entityData.length != 0) {
            try (NBTInputStream nbtStream = new NBTInputStream(new ByteArrayInputStream(entityData), ByteOrder.LITTLE_ENDIAN, false)) {
                while (nbtStream.available() > 0) {
                    Tag tag = Tag.readNamedTag(nbtStream);
                    if (!(tag instanceof CompoundTag)) {
                        throw new IOException("Entity root tag must be a compound tag (" + chunkX + "," + chunkZ + ") in " + path);
                    }
                    entities.add((CompoundTag) tag);
                }
            } catch (IOException e) {
                throw new ChunkException("Corrupted entity data (" + chunkX + "," + chunkZ + ") in " + path, e);
            }
        }

        byte[] tickingData = this.db.get(PENDING_SCHEDULED_TICKS.getKey(chunkX, chunkZ));
        if (tickingData != null && tickingData.length != 0) {
            loadBlockTickingQueue(tickingData, false);
        }

        byte[] randomTickingData = this.db.get(PENDING_RANDOM_TICKS.getKey(chunkX, chunkZ));
        if (randomTickingData != null && randomTickingData.length != 0) {
            loadBlockTickingQueue(randomTickingData, true);
        }

        int finalisation;
        byte[] finalisationData = this.db.get(FINALIZATION.getKey(chunkX, chunkZ));
        if (randomTickingData != null && randomTickingData.length != 0) {
            finalisation = Binary.readLInt(finalisationData);
        } else {
            finalisation = FINALISATION_DONE; //older versions didn't have this tag
        }

        LevelDbChunk chunk = new LevelDbChunk(this, chunkX, chunkZ, subChunks, heightmap, biomes3d, entities, blockEntities);

        if (finalisation == FINALISATION_DONE) {
            chunk.setGenerated();
            chunk.setPopulated();
        } else if (finalisation == FINALISATION_NEEDS_POPULATION) {
            chunk.setGenerated();
        }

        if (hasBeenUpgraded) {
            chunk.setAllSubChunksDirty(); //trigger rewriting chunk to disk if it was converted from an older format
            chunk.setChanged();
        }
        if (biomeOrHeightmapUpgraded || chunkVersion <= 2) {
            chunk.setHeightmapOrBiomesDirty();
            chunk.setChanged();
        }

        return chunk;
    }

    private void writeChunk(LevelDbChunk chunk, boolean convert, boolean background) {
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        BinaryStream stream = new BinaryStream();

        try (WriteBatch batch = this.db.createWriteBatch()) {
            batch.put(NEW_VERSION.getKey(chunkX, chunkZ), CHUNK_VERSION_SAVE_DATA);

            chunk.ioLock.lock();

            if (chunk.isSubChunksDirty()) {
                ChunkSection[] sections = chunk.getSections();
                if (sections != null) {
                    for (int i = 0; i < LevelDbChunk.SECTION_COUNT; i++) {
                        ChunkSection section = sections[i];
                        byte[] key = SUBCHUNK.getSubKey(chunkX, chunkZ, Level.subChunkIndexToY(i));

                        if (section == null || section.isEmpty()) {
                            batch.delete(key);
                            continue;
                        }

                        if (!section.isDirty()) {
                            continue;
                        }

                        section.writeToDisk(stream);
                        batch.put(key, stream.getBuffer());
                        stream.reuse();
                    }
                }
            }

            if (chunk.isHeightmapOrBiomesDirty()) {
                for (short height : chunk.getHeightmap()) {
                    stream.putLShort(height);
                }

                chunk.writeBiomeTo(stream, false);

                batch.put(HEIGHTMAP_AND_3D_BIOMES.getKey(chunkX, chunkZ), stream.getBuffer());
            }

            if (!background) {
                batch.put(FINALIZATION.getKey(chunkX, chunkZ), chunk.isPopulated() ? FINALISATION_DONE_SAVE_DATA
                        : chunk.isGenerated() ? FINALISATION_POPULATION_SAVE_DATA : FINALISATION_GENERATION_SAVE_DATA);

                //TODO: dirty?
                List<CompoundTag> blockEntities;
                if (!convert) {
                    blockEntities = new ObjectArrayList<>();
                    for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                        if (!blockEntity.isClosed()) {
                            blockEntity.saveNBT();
                            blockEntities.add(blockEntity.namedTag);
                        }
                    }
                } else {
                    blockEntities = chunk.getBlockEntityTags();
                    if (blockEntities == null) {
                        blockEntities = Collections.emptyList();
                    }
                }
                byte[] blockEntitiesKey = BLOCK_ENTITIES.getKey(chunkX, chunkZ);
                if (blockEntities.isEmpty()) {
                    batch.delete(blockEntitiesKey);
                } else {
                    try {
                        batch.put(blockEntitiesKey, NBTIO.write(blockEntities, ByteOrder.LITTLE_ENDIAN));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                //dirty?
                List<CompoundTag> entities;
                if (!convert) {
                    entities = new ObjectArrayList<>();
                    for (Entity entity : chunk.getEntities().values()) {
                        if (!(entity instanceof Player) && !entity.closed) {
                            entity.saveNBT();
                            entities.add(entity.namedTag);
                        }
                    }
                } else {
                    entities = chunk.getEntityTags();
                    if (entities == null) {
                        entities = Collections.emptyList();
                    }
                }
                byte[] entitiesKey = ENTITIES.getKey(chunkX, chunkZ);
                if (entities.isEmpty()) {
                    batch.delete(entitiesKey);
                } else {
                    try {
                        batch.put(entitiesKey, NBTIO.write(entities, ByteOrder.LITTLE_ENDIAN));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                Collection<BlockUpdateEntry> blockUpdateEntries = null;
                Collection<BlockUpdateEntry> randomBlockUpdateEntries = null;
                long currentTick = 0;

                LevelProvider provider;
                if (convert) {
                    blockUpdateEntries = chunk.getBlockUpdateEntries();
                } else if ((provider = chunk.getProvider()) != null) {
                    Level level = provider.getLevel();
                    currentTick = level.getCurrentTick();
                    //dirty?
                    blockUpdateEntries = level.getPendingBlockUpdates(chunk);
                    randomBlockUpdateEntries = level.getPendingRandomBlockUpdates(chunk);
                }

                byte[] pendingScheduledTicksKey = PENDING_SCHEDULED_TICKS.getKey(chunkX, chunkZ);
                if (blockUpdateEntries != null && !blockUpdateEntries.isEmpty()) {
                    CompoundTag ticks = saveBlockTickingQueue(blockUpdateEntries, currentTick);
                    if (ticks != null) {
                        try {
                            batch.put(pendingScheduledTicksKey, NBTIO.write(ticks, ByteOrder.LITTLE_ENDIAN));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        batch.delete(pendingScheduledTicksKey);
                    }
                } else {
                    batch.delete(pendingScheduledTicksKey);
                }

                byte[] pendingRandomTicksKey = PENDING_RANDOM_TICKS.getKey(chunkX, chunkZ);
                if (randomBlockUpdateEntries != null && !randomBlockUpdateEntries.isEmpty()) {
                    CompoundTag ticks = saveBlockTickingQueue(randomBlockUpdateEntries, currentTick);
                    if (ticks != null) {
                        try {
                            batch.put(pendingRandomTicksKey, NBTIO.write(ticks, ByteOrder.LITTLE_ENDIAN));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        batch.delete(pendingRandomTicksKey);
                    }
                } else {
                    batch.delete(pendingRandomTicksKey);
                }
            }

            batch.delete(HEIGHTMAP_AND_2D_BIOMES.getKey(chunkX, chunkZ));
            batch.delete(HEIGHTMAP_AND_2D_BIOME_COLORS.getKey(chunkX, chunkZ));
            batch.delete(LEGACY_TERRAIN.getKey(chunkX, chunkZ));
            batch.delete(LEGACY_BLOCK_EXTRA_DATA.getKey(chunkX, chunkZ));
            batch.delete(OLD_VERSION.getKey(chunkX, chunkZ));

            this.db.write(batch);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save chunk " + chunkX + "," + chunkZ + " in " + path, e);
        } finally {
            chunk.ioLock.unlock();
        }
    }

    @Override
    public boolean unloadChunk(int chunkX, int chunkZ) {
        return this.unloadChunk(chunkX, chunkZ, true);
    }

    @Override
    public boolean unloadChunk(int chunkX, int chunkZ, boolean safe) {
        long index = Level.chunkHash(chunkX, chunkZ);
        synchronized (this.chunks) {
            LevelDbChunk chunk = this.chunks.get(index);
            if (chunk != null && chunk.unload(false, safe)) {
                WeakReference<LevelDbChunk> lastChunk = this.lastChunk.get();
                if (lastChunk != null && lastChunk.get() == chunk) {
                    this.lastChunk.remove();
                }
                this.chunks.remove(index);
                return true;
            }
        }
        return false;
    }

    @Override
    public void saveChunk(int chunkX, int chunkZ) {
        if (this.isChunkLoaded(chunkX, chunkZ)) {
            this.writeChunk(this.getChunk(chunkX, chunkZ), false, false);
        }
    }

    @Override
    public void saveChunk(int chunkX, int chunkZ, FullChunk chunk) {
        if (!(chunk instanceof LevelDbChunk)) {
            throw new ChunkException("Invalid Chunk class: " + chunk.getClass() + " (" + chunkX + "," + chunkZ + ") in " + path);
        }
        LevelDbChunk dbChunk = (LevelDbChunk) chunk;
        this.writeChunk(dbChunk, true, false);
    }

    @Override
    public LevelDbChunk getChunk(int chunkX, int chunkZ) {
        return this.getChunk(chunkX, chunkZ, false);
    }

    @Override
    public LevelDbChunk getChunk(int chunkX, int chunkZ, boolean create) {
        LevelDbChunk chunk;
        WeakReference<LevelDbChunk> lastChunk = this.lastChunk.get();
        if (lastChunk != null) {
            chunk = lastChunk.get();
            if (chunk != null && chunk.getProvider() != null && chunk.getX() == chunkX && chunk.getZ() == chunkZ) {
                return chunk;
            }
        }

        long index = Level.chunkHash(chunkX, chunkZ);
        synchronized (this.chunks) {
            chunk = this.chunks.get(index);
            if (chunk != null) {
                this.lastChunk.set(new WeakReference<>(chunk));
                return chunk;
            }

            try {
                chunk = this.readChunk(chunkX, chunkZ);
            } catch (Exception e) {
                throw new ChunkException("corrupted chunk: " + chunkX + "," + chunkZ + " in " + path, e);
            }

            if (chunk == null && create) {
                chunk = LevelDbChunk.getEmptyChunk(chunkX, chunkZ, this);
            }

            if (chunk != null) {
                this.lastChunk.set(new WeakReference<>(chunk));
                this.chunks.put(index, chunk);
            }
        }
        return chunk;
    }

    public DB getDatabase() {
        return db;
    }

    @Override
    public void setChunk(int chunkX, int chunkZ, FullChunk chunk) {
        if (!(chunk instanceof LevelDbChunk)) {
            throw new ChunkException("Invalid Chunk class: " + chunk.getClass() + " (" + chunkX + "," + chunkZ + ") in " + path);
        }
        chunk.setProvider(this);
        chunk.setPosition(chunkX, chunkZ);
        long index = chunk.getIndex();

        synchronized (this.chunks) {
            LevelDbChunk oldChunk = this.chunks.get(index);
            if (!chunk.equals(oldChunk)) {
                if (oldChunk != null) {
                    oldChunk.unload(false, false);
                }

                LevelDbChunk newChunk = (LevelDbChunk) chunk;
                this.chunks.put(index, newChunk);
                this.lastChunk.set(new WeakReference<>(newChunk));
            }
        }
    }

    @SuppressWarnings("unused")
    public static LevelDbSubChunk createChunkSection(int chunkY) {
        return new LevelDbSubChunk(chunkY);
    }

    private boolean chunkExists(int chunkX, int chunkZ) {
        byte[] data = this.db.get(NEW_VERSION.getKey(chunkX, chunkZ));
        if (data != null && data.length != 0) {
            return true;
        }

        data = this.db.get(OLD_VERSION.getKey(chunkX, chunkZ));
        return data != null && data.length != 0;
    }

    @Override
    public boolean isChunkGenerated(int chunkX, int chunkZ) {
        if (!this.chunkExists(chunkX, chunkZ)) {
            return false;
        }
        LevelDbChunk chunk = this.getChunk(chunkX, chunkZ, false);
        if (chunk == null) {
            return false;
        }
        return chunk.isGenerated();
    }

    @Override
    public boolean isChunkPopulated(int chunkX, int chunkZ) {
        if (!this.chunkExists(chunkX, chunkZ)) {
            return false;
        }
        LevelDbChunk chunk = this.getChunk(chunkX, chunkZ, false);
        if (chunk == null) {
            return false;
        }
        return chunk.isPopulated();
    }

    @Override
    public synchronized void close() {
        if (closed) {
            return;
        }
        closed = true;

        try {
            gcLock.lock();

            this.unloadChunks(saveChunksOnClose);
            try {
                this.db.close();
            } catch (IOException e) {
                throw new RuntimeException("Unload LevelDB exception: " + path, e);
            }
            this.level = null;
        } finally {
            gcLock.unlock();
        }
    }

    @Override
    public String getPath() {
        return path;
    }

    public Server getServer() {
        return this.level.getServer();
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public String getName() {
        return this.levelData.getString("LevelName");
    }

    @Override
    public boolean isRaining() {
        return this.levelData.getFloat("rainLevel") > 0;
    }

    @Override
    public void setRaining(boolean raining) {
        this.levelData.putFloat("rainLevel", raining ? 1.0f : 0);
    }

    @Override
    public int getRainTime() {
        return this.levelData.getInt("rainTime");
    }

    @Override
    public void setRainTime(int rainTime) {
        this.levelData.putInt("rainTime", rainTime);
    }

    @Override
    public boolean isThundering() {
        return this.levelData.getFloat("lightningLevel") > 0;
    }

    @Override
    public void setThundering(boolean thundering) {
        this.levelData.putFloat("lightningLevel", thundering ? 1.0f : 0);
    }

    @Override
    public int getThunderTime() {
        return this.levelData.getInt("lightningTime");
    }

    @Override
    public void setThunderTime(int thunderTime) {
        this.levelData.putInt("lightningTime", thunderTime);
    }

    @Override
    public long getCurrentTick() {
        return this.levelData.getLong("currentTick");
    }

    @Override
    public void setCurrentTick(long currentTick) {
        this.levelData.putLong("currentTick", currentTick);
    }

    @Override
    public long getTime() {
        return this.levelData.getLong("Time");
    }

    @Override
    public void setTime(long value) {
        this.levelData.putLong("Time", value);
    }

    @Override
    public long getSeed() {
        return this.levelData.getLong("RandomSeed");
    }

    @Override
    public void setSeed(long value) {
        this.levelData.putLong("RandomSeed", value);
    }

    @Override
    public Vector3 getSpawn() {
        return new Vector3(this.levelData.getInt("SpawnX"), this.levelData.getInt("SpawnY"), this.levelData.getInt("SpawnZ"));
    }

    @Override
    public void setSpawn(Vector3 pos) {
        this.levelData.putInt("SpawnX", (int) pos.x);
        this.levelData.putInt("SpawnY", (int) pos.y);
        this.levelData.putInt("SpawnZ", (int) pos.z);
    }

    @Override
    public GameRules getGamerules() {
        GameRules rules = GameRules.getDefault();
        rules.readBedrockNBT(this.levelData);
        return rules;
    }

    @Override
    public void setGameRules(GameRules rules) {
        rules.writeBedrockNBT(this.levelData);
    }

    @Override
    public void doGarbageCollection() {
        //TODO: compress sub chunks
    }

    public CompoundTag getLevelData() {
        return levelData;
    }

    @Override
    public void updateLevelName(String name) {
        if (!this.getName().equals(name)) {
            this.levelData.putString("LevelName", name);
        }
    }

    protected static BlockVector3 deserializeExtraDataKey(int chunkVersion, int key) {
        return chunkVersion >= 3 ? new BlockVector3((key >> 12) & 0xf, key & 0xff, (key >> 8) & 0xf)
                // pre-1.0, 7 bits were used because the build height limit was lower
                : new BlockVector3((key >> 11) & 0xf, key & 0x7f, (key >> 7) & 0xf);
    }

    protected PalettedSubChunkStorage[] deserializeLegacyExtraData(int chunkX, int chunkZ, int chunkVersion) {
        byte[] extraRawData = this.db.get(LEGACY_BLOCK_EXTRA_DATA.getKey(chunkX, chunkZ));
        if (extraRawData == null || extraRawData.length == 0) {
            return null;
        }

        PalettedSubChunkStorage[] extraDataLayers = new PalettedSubChunkStorage[16];
        BinaryStream stream = new BinaryStream();
        int count = stream.getLInt();
        for (int i = 0; i < count; i++) {
            int posKey = stream.getLInt();
            int fullBlock = stream.getLShort();

            int blockId = fullBlock & 0xff;
            int blockData = (fullBlock >> 8) & 0xf;

            BlockVector3 pos = deserializeExtraDataKey(chunkVersion, posKey);
            int chunkY = pos.y >> 4;
            PalettedSubChunkStorage storage = extraDataLayers[chunkY];
            if (storage == null) {
                storage = PalettedSubChunkStorage.ofBlock();
                extraDataLayers[chunkY] = storage;
            }
            storage.set(pos, (blockId << Block.BLOCK_META_BITS) | blockData);
        }
        return extraDataLayers;
    }

    protected void loadBlockTickingQueue(byte[] data, boolean tickingQueueTypeIsRandom) {
        CompoundTag ticks;
        try {
            ticks = NBTIO.read(data, ByteOrder.LITTLE_ENDIAN);
        } catch (IOException e) {
            throw new ChunkException("Corrupted block ticking data in " + path, e);
        }

        int currentTick = ticks.getInt("currentTick");
        for (CompoundTag entry : ticks.getList("tickList", CompoundTag.class).getAllUnsafe()) {
            Block block = null;

            CompoundTag blockState = entry.getCompound("blockState");
            if (blockState.contains("name")) {
                BlockUpgrader.upgrade(blockState);
                int fullId = BlockSerializer.deserializeRuntime(blockState);
                block = Block.get(fullId >> Block.BLOCK_META_BITS, fullId & Block.BLOCK_META_MASK);
            } else if (entry.contains("tileID")) {
                block = Block.get(entry.getByte("tileID") & 0xff);
            }

            if (block == null) {
                log.debug("Unavailable block ticking entry skipped: {} in {}", entry, path);
                continue;
            }
            block.x = entry.getInt("x");
            block.y = entry.getInt("y");
            block.z = entry.getInt("z");
            block.level = level;

            int delay = (int) (entry.getLong("time") - currentTick);
            int priority = entry.getInt("p"); // Nukkit only

            if (!tickingQueueTypeIsRandom) {
                level.scheduleUpdate(block, block, delay, priority, false);
            } else {
                level.scheduleRandomUpdate(block, block, delay, priority, false);
            }
        }
    }

    @Nullable
    protected CompoundTag saveBlockTickingQueue(Collection<BlockUpdateEntry> entries, long currentTick) {
        ListTag<CompoundTag> tickList = new ListTag<>("tickList");
        for (BlockUpdateEntry entry : entries) {
            Block block = entry.block;
            CompoundTag blockTag = BlockSerializer.serializeRuntime(block.getFullId());

            Vector3 pos = entry.pos;
            int priority = entry.priority;

            CompoundTag tag = new CompoundTag()
                    .putInt("x", pos.getFloorX())
                    .putInt("y", pos.getFloorY())
                    .putInt("z", pos.getFloorZ())
                    .putCompound("blockState", blockTag)
                    .putLong("time", entry.delay - currentTick);

            if (priority != 0) {
                tag.putInt("p", priority); // Nukkit only
            }

            tickList.add(tag);
        }

        return tickList.isEmpty() ? null : new CompoundTag()
                .putInt("currentTick", 0)
                .putList(tickList);
    }

    @Override
    public void forEachChunks(Function<FullChunk, Boolean> action, boolean skipCorrupted) {
        try (DBIterator iter = db.iterator()) {
            while (iter.hasNext()) {
                Entry<byte[], byte[]> entry = iter.next();
                byte[] key = entry.getKey();
                if (key.length != 9) {
                    continue;
                }

                byte type = key[8];
                if (type != NEW_VERSION.getCode() && type != OLD_VERSION.getCode()) {
                    continue;
                }

                int chunkX = Binary.readLInt(key);
                int chunkZ = Binary.readLInt(key, 4);
                long index = Level.chunkHash(chunkX, chunkZ);
                LevelDbChunk chunk;
                synchronized (this.chunks) {
                    chunk = this.chunks.get(index);
                    if (chunk == null) {
                        try {
                            chunk = this.readChunk(chunkX, chunkZ);
                        } catch (Exception e) {
                            if (!skipCorrupted) {
                                throw e;
                            }
                            log.error("Skipped corrupted chunk {}, {} in {}", chunkX, chunkZ, path, e);
                            continue;
                        }

//                        if (chunk != null) {
//                            this.chunks.put(index, chunk);
//                        }
                    }
                }

                if (chunk == null) {
                    continue;
                }

                if (!action.apply(chunk)) {
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("iteration failed: " + path, e);
        }
    }

    @Override
    public void setSaveChunksOnClose(boolean save) {
        this.saveChunksOnClose = save;
    }

    @Override
    public HeightRange getHeightRange() {
        //TODO: data-driven
        return DEFAULT_HEIGHT_RANGE;
    }

    @Override
    public GeneratorOptions getWorldGeneratorOptions() {
        GeneratorOptions options = this.generatorOptions;
        if (options == null) {
            String biomeOverride = levelData.getString("BiomeOverride");
            int biome;
            if (biomeOverride.isEmpty()) {
                biome = -1;
            } else {
                biome = Biome.getIdByName(biomeOverride);
                if (biome != -1 && !Biome.isValidBiome(biome)) {
                    biome = -1;
                }
            }
            options = GeneratorOptions.builder()
                    .flatOptions(FlatGeneratorOptions.load(levelData.getString("FlatWorldLayers", FlatGeneratorOptions.DEFAULT_FLAT_WORLD_LAYERS)))
                    .bonusChest(levelData.getBoolean("bonusChestEnabled"))
                    .biomeOverride(biome)
                    .build();
            this.generatorOptions = options;
        }
        return options;
    }

    class AutoCompactionTask extends AsyncTask<Void> {
        @Override
        public void onRun() {
            if (!level.isAutoCompaction() || !canRun()) {
                return;
            }

            log.info("Running AutoCompaction... ({})", path);
            try {
                gcLock.lock();

                if (!canRun()) {
                    return;
                }

                MutableInt count = new MutableInt();
                forEachChunks(chunk -> {
                    //TODO: chunk.compressBiomes();
                    boolean next;
                    if (chunk.compress()) {
                        chunk.setChanged();

                        count.increment();

                        next = canRun();
                        if (next) {
//                            writeChunk((LevelDbChunk) chunk, true, true); //TODO: save subchunks
                        }
                    } else {
                        next = canRun();
                    }
                    return next;
                }, true);
                log.info("{} chunks have been compressed ({})", count, path);
            } finally {
                gcLock.unlock();
            }
        }

        @Override
        public void onCompletion(Server server) {
            if (closed || level == null || !level.isAutoCompaction()) {
                return;
            }
            server.getScheduler().scheduleDelayedTask(null, new AutoCompactionTask(), server.getAutoCompactionTicks(), true);
        }

        boolean canRun() {
            return !closed && level != null && level.getPlayers().isEmpty();
        }
    }

    static {
        log.info("native LevelDB provider: {}", NATIVE_LEVELDB && PROVIDER.isNative());
    }
}
