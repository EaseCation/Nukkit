package cn.nukkit.level.format.leveldb;

import cn.nukkit.GameVersion;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.GameRules;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.Level;
import cn.nukkit.level.LevelCreationOptions;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.anvil.util.NibbleArray;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.format.generic.ChunkRequestTask;
import cn.nukkit.level.generator.Generator;
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
import org.iq80.leveldb.CompressionType;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.WriteBatch;

import javax.annotation.Nullable;
import java.io.*;
import java.lang.ref.WeakReference;
import java.nio.ByteOrder;
import java.util.*;

import static cn.nukkit.level.format.leveldb.LevelDBKey.*;
import static cn.nukkit.level.format.leveldb.LevelDbConstants.*;
import static net.daporkchop.ldbjni.LevelDB.PROVIDER;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@Log4j2
public class LevelDB implements LevelProvider {

    protected final Long2ObjectMap<LevelDbChunk> chunks = new Long2ObjectOpenHashMap<>();
    protected final ThreadLocal<WeakReference<LevelDbChunk>> lastChunk = new ThreadLocal<>();

    protected DB db;

    protected Level level;

    protected final String path;

    protected CompoundTag levelData;

    public LevelDB(Level level, String path) {
        this.level = level;
        this.path = path;
        File filePath = new File(this.path);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }

        try (FileInputStream stream = new FileInputStream(this.getPath() + "level.dat")) {
            stream.skip(8);
            this.levelData = NBTIO.read(stream, ByteOrder.LITTLE_ENDIAN);
        } catch (IOException e) {
            throw new LevelException("Invalid level.dat", e);
        }

        if (!this.levelData.contains("generatorName")) {
            this.levelData.putString("generatorName", Generator.getGenerator("DEFAULT").getSimpleName().toLowerCase());
        }

        if (!this.levelData.contains("generatorOptions")) {
            this.levelData.putString("generatorOptions", "");
        }

        Options options = new Options()
                .createIfMissing(true)
                .compressionType(CompressionType.ZLIB_RAW)
                .blockSize(64 * 1024);
        try {
            this.db = PROVIDER.open(new File(this.getPath() + "/db"), options);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
        if (!new File(path + "/db").exists()) {
            new File(path + "/db").mkdirs();
        }

        int generatorType = Generator.getGeneratorType(options.getGenerator());
        Vector3 spawnPosition = options.getSpawnPosition();

        CompoundTag levelData = new CompoundTag()
                .putInt("DayCycleStopTime", -1)
                .putInt("Difficulty", 0)
                .putByte("ForceGameType", 0)
                .putInt("GameType", 0)
                .putInt("Generator", generatorType)
                .putLong("LastPlayed", System.currentTimeMillis() / 1000)
                .putString("LevelName", name)
                .putInt("NetworkVersion", GameVersion.getFeatureVersion().getProtocol())
                .putInt("Platform", 2)
                .putLong("RandomSeed", options.getSeed())
                .putInt("SpawnX", spawnPosition.getFloorX())
                .putInt("SpawnY", spawnPosition.getFloorY())
                .putInt("SpawnZ", spawnPosition.getFloorZ())
                .putInt("StorageVersion", CURRENT_STORAGE_VERSION)
                .putLong("Time", 0)
                .putByte("eduLevel", 0)
                .putByte("hasBeenLoadedInCreative", 1) // this actually determines whether achievements can be earned in this world
                .putByte("immutableWorld", 0)
                .putFloat("lightningLevel", 0)
                .putInt("lightningTime", 1)
                .putFloat("rainLevel", 0)
                .putInt("rainTime", 1)
                .putByte("spawnMobs", 1)
                .putByte("texturePacksRequired", 0)
                .putLong("currentTick", 1)
                .putInt("LimitedWorldOriginX", 128)
                .putInt("LimitedWorldOriginY", Short.MAX_VALUE)
                .putInt("LimitedWorldOriginZ", 128)
                .putInt("limitedWorldWidth", 16)
                .putInt("limitedWorldDepth", 16)
                .putLong("worldStartCount", ((long) Integer.MAX_VALUE) & 0xffffffffL)
//                .putString("baseGameVersion", generatorType != Generator.TYPE_OLD ? "*" : GameVersion.V1_17_40.toString())
                .putByte("bonusChestEnabled", 0)
                .putByte("bonusChestSpawned", 1)
                .putByte("CenterMapsToOrigin", 0)
                .putByte("commandsEnabled", 1)
                .putByte("ConfirmedPlatformLockedContent", 0)
                .putByte("educationFeaturesEnabled", 0)
                .putInt("eduOffer", 0)
                .putByte("hasLockedBehaviorPack", 0)
                .putByte("hasLockedResourcePack", 0)
                .putByte("isFromLockedTemplate", 0)
                .putByte("isFromWorldTemplate", 0)
                .putByte("isSingleUseWorld", 0)
                .putByte("isWorldTemplateOptionLocked", 0)
                .putByte("LANBroadcast", 1)
                .putByte("LANBroadcastIntent", 1)
                .putByte("MultiplayerGame", 1)
                .putByte("MultiplayerGameIntent", 1)
                .putInt("PlatformBroadcastIntent", 3)
                .putInt("XBLBroadcastIntent", 3)
                .putInt("NetherScale", 8)
                .putString("prid", "")
                .putByte("requiresCopiedPackRemovalCheck", 0)
                .putInt("serverChunkTickRange", 4)
                .putByte("SpawnV1Villagers", 0)
                .putByte("startWithMapEnabled", 0)
                .putByte("useMsaGamertagsOnly", 0);
        options.getGameRules().writeNBT(levelData, true);

        byte[] data = NBTIO.write(levelData, ByteOrder.LITTLE_ENDIAN);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(Binary.writeLInt(3));
        outputStream.write(Binary.writeLInt(data.length));
        outputStream.write(data);

        Utils.writeFile(path + "level.dat", new ByteArrayInputStream(outputStream.toByteArray()));

        DB db = PROVIDER.open(new File(path + "/db"), new Options()
                .createIfMissing(true)
                .compressionType(CompressionType.ZLIB_RAW)
                .blockSize(64 * 1024));
        db.close();
    }

    @Override
    public void saveLevelData() {
        try {
            byte[] data = NBTIO.write(levelData, ByteOrder.LITTLE_ENDIAN);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(Binary.writeLInt(CURRENT_STORAGE_VERSION));
            outputStream.write(Binary.writeLInt(data.length));
            outputStream.write(data);

            Utils.writeFile(path + "level.dat", new ByteArrayInputStream(outputStream.toByteArray()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AsyncTask requestChunkTask(int chunkX, int chunkZ) {
        LevelDbChunk chunk = this.getChunk(chunkX, chunkZ, false);
        if (chunk == null) {
            throw new ChunkException("Invalid Chunk sent");
        }

        return new ChunkRequestTask(chunk);
    }

    @Override
    public void unloadChunks() {
        Iterator<LevelDbChunk> iter = this.chunks.values().iterator();
        while (iter.hasNext()) {
            iter.next().unload(true, false);
            iter.remove();
        }
    }

    @Override
    public String getGenerator() {
        return this.levelData.getString("generatorName");
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
            this.lastChunk.set(new WeakReference<>(chunk));
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
            this.lastChunk.set(new WeakReference<>(chunk));
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
            this.level.timings.syncChunkLoadDataTimer.startTiming();
            chunk = this.chunks.get(index);
            if (chunk != null) {
                this.lastChunk.set(new WeakReference<>(chunk));
                this.level.timings.syncChunkLoadDataTimer.stopTiming();
                return true;
            }

            try {
                chunk = this.readChunk(chunkX, chunkZ);
            } catch (Exception e) {
                throw new ChunkException("corrupted chunk: " + chunkX + "," + chunkZ, e);
            }

            if (chunk == null && create) {
                chunk = LevelDbChunk.getEmptyChunk(chunkX, chunkZ, this);
            }

            if (chunk != null) {
                this.chunks.put(index, chunk);
                this.lastChunk.set(new WeakReference<>(chunk));
                this.level.timings.syncChunkLoadDataTimer.stopTiming();
                return true;
            }
            this.level.timings.syncChunkLoadDataTimer.stopTiming();
        }
        return false;
    }

    @Nullable
    public LevelDbChunk readChunk(int chunkX, int chunkZ) {
        byte[] versionData = this.db.get(OLD_VERSION.getKey(chunkX, chunkZ));
        if (versionData == null || versionData.length != 1) {
            return null;
        }
        byte chunkVersion = versionData[0];
        boolean hasBeenUpgraded = chunkVersion < CURRENT_LEVEL_CHUNK_VERSION;

        LevelDbSubChunk[] subChunks = new LevelDbSubChunk[16];
        short[] heightmap = null;
        byte[] biome = null;
        PalettedSubChunkStorage[] biomes3d = null;

        switch (chunkVersion) {
//            case 40: // 1.18.30
//            case 39: // 1.18.0.25 beta
//            case 38: // 1.18.0.24 beta internal_experimental
//            case 37: // 1.18.0.24 beta experimental
//            case 36: // 1.18.0.22 beta internal_experimental
//            case 35: // 1.18.0.22 beta experimental
//            case 34: // 1.18.0.20 beta internal_experimental
//            case 33: // 1.18.0.20 beta experimental
//            case 32: // 1.17.40
//            case 31: // 1.17.40.20 beta experimental
//            case 30: // 1.17.30.25 beta internal_experimental
//            case 29: // 1.17.30.25 beta experimental
//            case 28: // 1.17.30.23 beta internal_experimental
//            case 27: // 1.17.30.23 beta experimental
//            case 26: // 1.16.230.50 beta internal_experimental
//            case 25: // 1.16.230.50 beta experimental
//            case 24: // 1.16.220.50 beta internal_experimental
//            case 23: // 1.16.220.50 beta experimental
//            case 22: // 1.16.210
//            case 21: // 1.16.100.57 beta
//            case 20: // 1.16.100.52 beta
//            case 19: // 1.16.0
//            case 18: // 1.16.0.51 beta
            //TODO: check walls
//            case 17: // 1.12 hotfix
//            case 16: // 1.12.0
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

                for (int y = 0; y <= 15; ++y) {
                    byte[] subChunkValue = this.db.get(SUBCHUNK.getSubKey(chunkX, chunkZ, y));
                    if (subChunkValue == null) {
                        continue;
                    }
                    if (subChunkValue.length == 0) {
                        throw new ChunkException("Unexpected empty data for subchunk " + y);
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
                                int indexY = stream.getByte();
                                if (indexY != y) {
                                    throw new ChunkException("Unexpected Y index (" + indexY + ") for subchunk " + y);
                                }
                            }

                            if (storageCount > 0) {
                                PalettedSubChunkStorage[] storages = new PalettedSubChunkStorage[storageCount];
                                for (int i = 0; i < storageCount; ++i) {
                                    storages[i] = PalettedSubChunkStorage.ofBlock(stream);
                                }

                                subChunks[y] = new LevelDbSubChunk(y, storages);
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
                                hasBeenUpgraded = true;
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

                            subChunks[y] = new LevelDbSubChunk(y, storages);
                            break;
                        case 1: //paletted v1, has a single block storage
                            storages = new PalettedSubChunkStorage[2];
                            storages[0] = PalettedSubChunkStorage.ofBlock(stream);

                            if (convertedLegacyExtraData != null && convertedLegacyExtraData.length > y) {
                                storages[1] = convertedLegacyExtraData[y];
                            }

                            subChunks[y] = new LevelDbSubChunk(y, storages);
                            break;
                        default:
                            //TODO: set chunks read-only so the version on disk doesn't get overwritten
                            throw new ChunkException("don't know how to decode LevelDB subchunk format version " + subChunkVersion);
                    }
                }

                byte[] maps2d = this.db.get(HEIGHTMAP_AND_2D_BIOMES.getKey(chunkX, chunkZ));
                if (maps2d != null && maps2d.length >= SUB_CHUNK_2D_SIZE * 2 + SUB_CHUNK_2D_SIZE) {
                    heightmap = new short[SUB_CHUNK_2D_SIZE];
                    biome = new byte[SUB_CHUNK_2D_SIZE];

                    ByteBuf buf = Unpooled.wrappedBuffer(maps2d);
                    try {
                        for (int i = 0; i < SUB_CHUNK_2D_SIZE; i++)  {
                            heightmap[i] = buf.readShortLE();
                        }
                        buf.readBytes(biome);
                    } finally {
                        buf.release();
                    }
                }

                break;
            case 2: // 0.9.5
            case 1: // 0.9.2
            case 0: // 0.9.0.1 beta (first version)
                convertedLegacyExtraData = this.deserializeLegacyExtraData(chunkX, chunkZ, chunkVersion);

                byte[] legacyTerrain = this.db.get(LEGACY_TERRAIN.getKey(chunkX, chunkZ));
                if (legacyTerrain == null || legacyTerrain.length == 0) {
                    throw new ChunkException("Missing expected legacy terrain data for format version " + chunkVersion);
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

                    subChunks[y] = new LevelDbSubChunk(y, storages);
                }

                // Discard skyLight and blockLight
                stream.skip(8 * SUB_CHUNK_SIZE / 2 + 8 * SUB_CHUNK_SIZE / 2);

                /*heightmap = new short[SUB_CHUNK_2D_SIZE];
                for (int i = 0; i < SUB_CHUNK_2D_SIZE; i++) {
                    heightmap[i] = (short) (stream.getByte() & 0xff);
                }*/
                stream.skip(SUB_CHUNK_2D_SIZE); // recalculate heightmap

                biome = new byte[SUB_CHUNK_2D_SIZE];
                for (int i = 0; i < SUB_CHUNK_2D_SIZE; i++) {
                    biome[i] = (byte) (Biome.toValidBiome(stream.getInt() >> 24) & 0xff);
                }

                break;
            default:
                //TODO: set chunks read-only so the version on disk doesn't get overwritten
                throw new ChunkException("don't know how to decode chunk format version " + chunkVersion);
        }

        List<CompoundTag> blockEntities = new ObjectArrayList<>();
        byte[] blockEntityData = this.db.get(BLOCK_ENTITIES.getKey(chunkX, chunkZ));
        if (blockEntityData != null && blockEntityData.length != 0) {
            try (NBTInputStream nbtStream = new NBTInputStream(new ByteArrayInputStream(blockEntityData), ByteOrder.LITTLE_ENDIAN, false)) {
                while (nbtStream.available() > 0) {
                    Tag tag = Tag.readNamedTag(nbtStream);
                    if (!(tag instanceof CompoundTag)) {
                        throw new IOException("Root tag must be a compound tag");
                    }
                    blockEntities.add((CompoundTag) tag);
                }
            } catch (IOException e) {
                throw new ChunkException("Corrupted block entity data", e);
            }
        }

        List<CompoundTag> entities = new ObjectArrayList<>();
        byte[] entityData = this.db.get(ENTITIES.getKey(chunkX, chunkZ));
        if (entityData != null && entityData.length != 0) {
            try (NBTInputStream nbtStream = new NBTInputStream(new ByteArrayInputStream(entityData), ByteOrder.LITTLE_ENDIAN, false)) {
                while (nbtStream.available() > 0) {
                    Tag tag = Tag.readNamedTag(nbtStream);
                    if (!(tag instanceof CompoundTag)) {
                        throw new IOException("Root tag must be a compound tag");
                    }
                    entities.add((CompoundTag) tag);
                }
            } catch (IOException e) {
                throw new ChunkException("Corrupted entity data", e);
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
        byte[] finalisationData = this.db.get(FINALIZATION.getKey(chunkX, chunkZ)); // intLE
        if (randomTickingData != null && randomTickingData.length != 0) {
            finalisation = finalisationData[0];
        } else {
            finalisation = FINALISATION_DONE; //older versions didn't have this tag
        }

        LevelDbChunk chunk = new LevelDbChunk(this, chunkX, chunkZ, subChunks, heightmap, biome, biomes3d, entities, blockEntities);

        if (finalisation == FINALISATION_DONE) {
            chunk.setGenerated();
            chunk.setPopulated();
        } else if (finalisation == FINALISATION_NEEDS_POPULATION) {
            chunk.setGenerated();
        }

        if (hasBeenUpgraded) {
            chunk.setAllSubChunksDirty(); //trigger rewriting chunk to disk if it was converted from an older format
        }
        if (chunkVersion <= 2) {
            chunk.setHeightmapOrBiomesDirty();
        }

        return chunk;
    }

    private void writeChunk(LevelDbChunk chunk, boolean convert) {
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        BinaryStream stream = new BinaryStream();

        WriteBatch batch = this.db.createWriteBatch();
        batch.put(OLD_VERSION.getKey(chunkX, chunkZ), CHUNK_VERSION_SAVE_DATA);

        if (chunk.isSubChunksDirty()) {
            ChunkSection[] sections = chunk.getSections();
            if (sections != null) {
                for (int y = 0; y < 16; y++) {
                    ChunkSection section = sections[y];
                    byte[] key = SUBCHUNK.getSubKey(chunkX, chunkZ, y);

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
            int mark = stream.getCount();

            stream.put(chunk.getBiomeIdArray());
            batch.put(HEIGHTMAP_AND_2D_BIOMES.getKey(chunkX, chunkZ), stream.getBuffer());

            stream.setCount(mark);
            /*PalettedSubChunkStorage[] biomes3d = chunk.getBiomes();
            biomes3d[0].writeToDiskBiome(stream);
            for (int i = 1; i < 24; i++) {
                if (i >= biomes3d.length) {
                    stream.putByte((byte) 0xff);
                    continue;
                }
                PalettedSubChunkStorage biome3d = biomes3d[i];
                if (biome3d == null) {
                    stream.putByte((byte) 0xff);
                    continue;
                }
                biome3d.writeToDiskBiome(stream);
            }
            batch.put(HEIGHTMAP_AND_3D_BIOMES.getKey(chunkX, chunkZ), stream.getBuffer());*/
        }

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

        /*stream.reuse();
        stream.putInt(CURRENT_NUKKIT_DATA_VERSION);
        stream.putLLong(NUKKIT_DATA_MAGIC);
        CompoundTag nbt = new CompoundTag();
        // baked lighting
//        nbt.putByteArray("BlockLight", chunk.getBlockLightArray());
//        nbt.putByteArray("SkyLight", chunk.getBlockSkyLightArray());
        try {
            stream.put(NBTIO.write(nbt, ByteOrder.LITTLE_ENDIAN));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        batch.put(NUKKIT_DATA.getKey(chunkX, chunkZ), stream.getBuffer());*/

        batch.delete(HEIGHTMAP_AND_2D_BIOME_COLORS.getKey(chunkX, chunkZ));
        batch.delete(LEGACY_TERRAIN.getKey(chunkX, chunkZ));

        this.db.write(batch);
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
                    this.lastChunk.set(null);
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
            this.writeChunk(this.getChunk(chunkX, chunkZ), false);
        }
    }

    @Override
    public void saveChunk(int chunkX, int chunkZ, FullChunk chunk) {
        if (!(chunk instanceof LevelDbChunk)) {
            throw new ChunkException("Invalid Chunk class");
        }
        LevelDbChunk dbChunk = (LevelDbChunk) chunk;
        this.writeChunk(dbChunk, true);
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

            this.level.timings.syncChunkLoadDataTimer.startTiming();
            try {
                chunk = this.readChunk(chunkX, chunkZ);
            } catch (Exception e) {
                throw new ChunkException("corrupted chunk: " + chunkX + "," + chunkZ, e);
            }

            if (chunk == null && create) {
                chunk = LevelDbChunk.getEmptyChunk(chunkX, chunkZ, this);
            }
            this.level.timings.syncChunkLoadDataTimer.stopTiming();

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
            throw new ChunkException("Invalid Chunk class");
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
        byte[] data = this.db.get(OLD_VERSION.getKey(chunkX, chunkZ));
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
        this.unloadChunks();
        try {
            this.db.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.level = null;
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
        rules.readNBT(this.levelData);
        return rules;
    }

    @Override
    public void setGameRules(GameRules rules) {
        rules.writeNBT(this.levelData, true);
    }

    @Override
    public void doGarbageCollection() {
        //TODO: compress sub chunks
    }

    public CompoundTag getLevelData() {
        return levelData;
    }

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
            throw new ChunkException("Corrupted block ticking data", e);
        }

        int currentTick = ticks.getInt("currentTick");
        for (CompoundTag entry : ticks.getList("tickList", CompoundTag.class).getAllUnsafe()) {
            Block block = null;

            CompoundTag blockState = entry.getCompound("blockState");
            if (blockState.contains("name")) {
                String name = blockState.getString("name");
                int id = GlobalBlockPalette.getBlockIdByName(name);
                if (id == -1) {
                    log.warn("Unmapped block name: {}", name);
                    continue;
                }

                int meta;
                int version = blockState.getInt("version");
                if (version != 0) {
                    log.warn("Unsupported block state version: {}", version);
                    continue;
                    //TODO: block state
                } else {
                    meta = blockState.getShort("val");
                }
                block = Block.get(id, meta);
            } else if (entry.contains("tileID")) {
                block = Block.get(entry.getByte("tileID") & 0xff);
            }

            if (block == null) {
                log.debug("Unavailable block ticking entry skipped: {}", entry);
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
            int id = block.getId();
            String name = GlobalBlockPalette.getNameByBlockId(id);
            if (name == null) {
                log.warn("Unmapped block ID: {}", id);
                continue;
            }
            Vector3 pos = entry.pos;
            int priority = entry.priority;

            CompoundTag tag = new CompoundTag()
                    .putInt("x", pos.getFloorX())
                    .putInt("y", pos.getFloorY())
                    .putInt("z", pos.getFloorZ())
                    .putCompound("blockState", new CompoundTag()
                            .putString("name", name)
                            .putShort("val", block.getDamage()))
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
}
