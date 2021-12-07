package cn.nukkit.level.format.anvil;

import cn.nukkit.Server;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.level.GlobalBlockPaletteInterface.StaticVersion;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.generic.*;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.util.PalettedSubChunkStorage;
import cn.nukkit.math.XXHash64;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.BatchPacket;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.ChunkException;
import cn.nukkit.utils.ThreadCache;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@Log4j2
public class Anvil extends BaseLevelProvider {
    public static final int VERSION = 19133;
    static private final byte[] PAD_256 = new byte[256];

    public static final int LOWER_PADDING_SUB_CHUNK_COUNT = 4;
    public static final byte[] LOWER_PADDING_SUB_CHUNK_BLOB = new byte[]{
            8, // sub chunk version
            0, // no layers - client will treat this as all-air
    };
    public static final long LOWER_PADDING_SUB_CHUNK_HASH = XXHash64.getHash(LOWER_PADDING_SUB_CHUNK_BLOB);

    public Anvil(Level level, String path) throws IOException {
        super(level, path);
    }

    public static String getProviderName() {
        return "anvil";
    }

    public static byte getProviderOrder() {
        return ORDER_YZX;
    }

    public static boolean usesChunkSection() {
        return true;
    }

    public static boolean isValid(String path) {
        boolean isValid = (new File(path, "level.dat").exists()) && new File(path, "region").isDirectory();
        if (isValid) {
            for (File file : new File(path, "region").listFiles((dir, name) -> Pattern.matches("^.+\\.mc[r|a]$", name))) {
                if (!file.getName().endsWith(".mca")) {
                    isValid = false;
                    break;
                }
            }
        }
        return isValid;
    }

    public static void generate(String path, String name, long seed, Class<? extends Generator> generator) throws IOException {
        generate(path, name, seed, generator, new HashMap<>());
    }

    public static void generate(String path, String name, long seed, Class<? extends Generator> generator, Map<String, String> options) throws IOException {
        if (!new File(path + "/region").exists()) {
            new File(path + "/region").mkdirs();
        }

        CompoundTag levelData = new CompoundTag("Data")
                .putCompound("GameRules", new CompoundTag())

                .putLong("DayTime", 0)
                .putInt("GameType", 0)
                .putString("generatorName", Generator.getGeneratorName(generator))
                .putString("generatorOptions", options.getOrDefault("preset", ""))
                .putInt("generatorVersion", 1)
                .putBoolean("hardcore", false)
                .putBoolean("initialized", true)
                .putLong("LastPlayed", System.currentTimeMillis() / 1000)
                .putString("LevelName", name)
                .putBoolean("raining", false)
                .putInt("rainTime", 0)
                .putLong("RandomSeed", seed)
                .putInt("SpawnX", 128)
                .putInt("SpawnY", 70)
                .putInt("SpawnZ", 128)
                .putBoolean("thundering", false)
                .putInt("thunderTime", 0)
                .putInt("version", VERSION)
                .putLong("Time", 0)
                .putLong("SizeOnDisk", 0);

        NBTIO.writeGZIPCompressed(new CompoundTag().putCompound("Data", levelData), new FileOutputStream(new File(path, "level.dat")), ByteOrder.BIG_ENDIAN);
    }

    public Chunk getEmptyChunk(int chunkX, int chunkZ) {
        return Chunk.getEmptyChunk(chunkX, chunkZ, this);
    }

    @Override
    public AsyncTask requestChunkTask(int x, int z) throws ChunkException {
        Chunk chunk = (Chunk) this.getChunk(x, z, false);
        if (chunk == null) {
            throw new ChunkException("Invalid Chunk Set");
        }

        return new AsyncTask() {

            boolean success = false;

            long timestamp = chunk.getChanges();
            int count = 0;

            ChunkBlobCache chunkBlobCache;

            final Map<StaticVersion, byte[]> payloads = new EnumMap<>(StaticVersion.class);
            byte[] payload;
            byte[] payloadOld;
            ChunkPacketCache chunkPacketCache;

            @Override
            public void onRun() {
                try {
                    BinaryStream stream = ThreadCache.binaryStream.get();
                    stream.reuse();

                    byte[] blockEntities = new byte[0];
                    if (!chunk.getBlockEntities().isEmpty()) {
                        List<CompoundTag> tagList = new ArrayList<>();

                        for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                            if (blockEntity instanceof BlockEntitySpawnable) {
                                tagList.add(((BlockEntitySpawnable) blockEntity).getSpawnCompound());
                            }
                        }

                        try {
                            blockEntities = NBTIO.write(tagList, ByteOrder.LITTLE_ENDIAN, true);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    byte[] biomePalette;
                    PalettedSubChunkStorage storage = PalettedSubChunkStorage.ofBiome(toValidBiome(chunk.getBiomeId(0, 0)));
                    for (int x = 0; x < 16; x++) {
                        int xi = x << 8;
                        for (int z = 0; z < 16; z++) {
                            int xzi = xi | (z << 4);
                            int biomeId = toValidBiome(chunk.getBiomeId(x, z));
                            for (int y = 0; y < 16; y++) {
                                storage.set(xzi | y, biomeId);
                            }
                        }
                    }
                    storage.writeTo(stream);
                    biomePalette = stream.getBuffer();
                    // right now we don't support 3D natively, so we just 3Dify our 2D biomes so they fill the column
                    for (int i = 0; i < 4 + 16 + 4; i++) {
                        stream.put(biomePalette);
                    }
                    biomePalette = stream.getBuffer();

                    Int2IntMap extra = chunk.getBlockExtraDataArray();
                    byte[] extraData;
                    if (!extra.isEmpty()) {
                        stream.reuse();
                        stream.putVarInt(extra.size());
                        for (Int2IntMap.Entry entry : extra.int2IntEntrySet()) {
                            stream.putVarInt(entry.getIntKey());
                            stream.putLShort(entry.getIntValue());
                        }
                        extraData = stream.getBuffer();
                    } else {
                        extraData = null;
                    }

                    cn.nukkit.level.format.ChunkSection[] sections = chunk.getSections();
                    for (int i = sections.length - 1; i >= 0; i--) {
                        if (!sections[i].isEmpty()) {
                            count = i + 1;
                            break;
                        }
                    }

                    Long2ObjectOpenHashMap<byte[]> clientBlobs = new Long2ObjectOpenHashMap<>(16 + 1); // 16 subChunks + 1 biome
                    Long2ObjectOpenHashMap<byte[]> extendedClientBlobs = new Long2ObjectOpenHashMap<>(4 + 16 + 4 + 1); // 4 + 16 + 4 subChunks + 1 biome

                    LongList blobIds = new LongArrayList(16 + 1);
                    LongList extendedBlobIds = new LongArrayList(4 + 16 + 4 + 1);

                    for (int i = 0; i < LOWER_PADDING_SUB_CHUNK_COUNT; i++) {
                        extendedBlobIds.add(LOWER_PADDING_SUB_CHUNK_HASH);
                    }
                    extendedClientBlobs.put(LOWER_PADDING_SUB_CHUNK_HASH, LOWER_PADDING_SUB_CHUNK_BLOB);

                    for (int i = 0; i < count; i++) {
                        //byte[] subChunk = new byte[6145]; // 1 subChunkVersion (always 0) + 4096 blockIds + 2048 blockMeta
                        stream.reuse();
                        //bs.putByte((byte) 0);
                        sections[i].writeToCache(stream);
                        //System.arraycopy(sections[i].getBytes(), 0, subChunk, 1, 6144); // skip subChunkVersion
                        byte[] subChunk = stream.getBuffer();
                        long hash = XXHash64.getHash(subChunk);
                        blobIds.add(hash);
                        extendedBlobIds.add(hash);
                        clientBlobs.put(hash, subChunk);
                        extendedClientBlobs.put(hash, subChunk);
                    }

                    byte[] biome = chunk.getBiomeIdArray();
                    long hash = XXHash64.getHash(biome);
                    blobIds.add(hash);
                    clientBlobs.put(hash, biome);

                    hash = XXHash64.getHash(biomePalette);
                    extendedBlobIds.add(hash);
                    extendedClientBlobs.put(hash, biome);

                    byte[] clientBlobCachedPayload = new byte[1 + blockEntities.length]; // borderBlocks + blockEntities
                    System.arraycopy(blockEntities, 0, clientBlobCachedPayload, 1, blockEntities.length); // borderBlocks array size is always 0, skip it

                    chunkBlobCache = new ChunkBlobCache(count, blobIds.toLongArray(), extendedBlobIds.toLongArray(), clientBlobs, extendedClientBlobs, clientBlobCachedPayload);

                    for (StaticVersion version : StaticVersion.getValues()) {
                        payloads.put(version, encodeChunk(chunk, sections, count, biomePalette, blockEntities, version));
                    }

                    payload = encodeChunk(false, chunk, sections, count, extraData, blockEntities);
                    payloadOld = encodeChunk(true, chunk, sections, count, extraData, blockEntities);

                    if (level.isCacheChunks()) {
                        Map<StaticVersion, BatchPacket> packets = new EnumMap<>(StaticVersion.class);
                        payloads.forEach((version, payload) -> {
                            int actualCount = count;
                            if (version.getProtocol() >= StaticVersion.V1_18.getProtocol()) {
                                actualCount += LOWER_PADDING_SUB_CHUNK_COUNT;
                            }
                            packets.put(version, Level.getChunkCacheFromData(x, z, actualCount, payload, false, true));
                        });

                        chunkPacketCache = new ChunkPacketCache(
                                packets,
                                Level.getChunkCacheFromData(x, z, count, payload, false, true),
                                Level.getChunkCacheFromData(x, z, count, payload, false, false),
                                Level.getChunkCacheFromData(x, z, count, payloadOld, true, false)
                        );
                    }
                    success = true;
                } catch (Exception e) {
                    log.warn("Chunk async load failed", e);
                }
            }

            @Override
            public void onCompletion(Server server) {
                if (success) {
                    getLevel().chunkRequestCallback(timestamp, x, z, count, chunkBlobCache, chunkPacketCache, payload, payloadOld, payloads);
                }
            }
        };
    }

    private byte[] encodeChunk(boolean isOld, Chunk chunk, cn.nukkit.level.format.ChunkSection[] sections, int count, byte[] extraData, byte[] blockEntities) {
        BinaryStream stream = ThreadCache.binaryStream.get();
        stream.reuse();
        if (isOld) {
            stream.putByte((byte) count);  //count is now sent in packet
            for (int i = 0; i < count; i++) {
                stream.putByte((byte) 0);
                if (sections[i] instanceof ChunkSection) ((ChunkSection) sections[i]).writeToOld(stream);
                else if (sections[i] instanceof EmptyChunkSection) ((EmptyChunkSection) sections[i]).writeToOld(stream);
            }
        } else {
            for (int i = 0; i < count; i++) {
                sections[i].writeTo(stream);
            }
        }
        if (isOld) {
            for (byte height : chunk.getHeightMapArray()) {
                stream.putByte(height);
            } //computed client side?
            stream.put(PAD_256);
        }
        stream.put(chunk.getBiomeIdArray());
        stream.putByte((byte) 0);
        /*
        if (extraData != null) {
            stream.put(extraData);
        } else {
            stream.putVarInt(0);
        }*/
        stream.put(blockEntities);

        return stream.getBuffer();
    }

    private byte[] encodeChunk(Chunk chunk, cn.nukkit.level.format.ChunkSection[] sections, int count, byte[] biomePalette, byte[] blockEntities, StaticVersion version) {
        BinaryStream stream = ThreadCache.binaryStream.get();
        stream.reuse();
        boolean extendedLevel = version.getProtocol() >= StaticVersion.V1_18.getProtocol();
        if (extendedLevel) {
            //HACK: fill in fake sub chunks to make up for the new negative space client-side
            for (int i = 0; i < LOWER_PADDING_SUB_CHUNK_COUNT; i++) {
                stream.put(LOWER_PADDING_SUB_CHUNK_BLOB);
            }
        }
        for (int i = 0; i < count; i++) {
            sections[i].writeTo(stream, version);
        }
        if (extendedLevel) {
            stream.put(biomePalette);
        } else {
            stream.put(chunk.getBiomeIdArray());
        }
        stream.putByte((byte) 0);
        stream.put(blockEntities);
        return stream.getBuffer();
    }

    private static int toValidBiome(int id) {
        String name = Biome.getNameById(id);
        // make sure we aren't sending bogus biomes - the 1.18.0 client crashes if we do this
        return name == null ? EnumBiome.OCEAN.id : id;
    }

    private int lastPosition = 0;

    @Override
    public void doGarbageCollection(long time) {
        long start = System.currentTimeMillis();
        int maxIterations = size();
        if (lastPosition > maxIterations) lastPosition = 0;
        int i;
        synchronized (chunks) {
            ObjectIterator<BaseFullChunk> iter = chunks.values().iterator();
            if (lastPosition != 0) iter.skip(lastPosition);
            for (i = 0; i < maxIterations; i++) {
                if (!iter.hasNext()) {
                    iter = chunks.values().iterator();
                }
                if (!iter.hasNext()) break;
                BaseFullChunk chunk = iter.next();
                if (chunk == null) continue;
                if (chunk.isGenerated() && chunk.isPopulated() && chunk instanceof Chunk) {
                    Chunk anvilChunk = (Chunk) chunk;
                    chunk.compress();
                    if (System.currentTimeMillis() - start >= time) break;
                }
            }
        }
        lastPosition += i;
    }

    @Override
    public synchronized BaseFullChunk loadChunk(long index, int chunkX, int chunkZ, boolean create) {
        int regionX = getRegionIndexX(chunkX);
        int regionZ = getRegionIndexZ(chunkZ);
        BaseRegionLoader region = this.loadRegion(regionX, regionZ);
        this.level.timings.syncChunkLoadDataTimer.startTiming();
        BaseFullChunk chunk;
        try {
            chunk = region.readChunk(chunkX - regionX * 32, chunkZ - regionZ * 32);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (chunk == null) {
            if (create) {
                chunk = this.getEmptyChunk(chunkX, chunkZ);
                putChunk(index, chunk);
            }
        } else {
            putChunk(index, chunk);
        }
        this.level.timings.syncChunkLoadDataTimer.stopTiming();
        return chunk;
    }

    @Override
    public synchronized void saveChunk(int X, int Z) {
        BaseFullChunk chunk = this.getChunk(X, Z);
        if (chunk != null) {
            try {
                this.loadRegion(X >> 5, Z >> 5).writeChunk(chunk);
            } catch (Exception e) {
                throw new ChunkException("Error saving chunk (" + X + ", " + Z + ")", e);
            }
        }
    }


    @Override
    public synchronized void saveChunk(int x, int z, FullChunk chunk) {
        if (!(chunk instanceof Chunk)) {
            throw new ChunkException("Invalid Chunk class");
        }
        int regionX = x >> 5;
        int regionZ = z >> 5;
        this.loadRegion(regionX, regionZ);
        chunk.setX(x);
        chunk.setZ(z);
        try {
            this.getRegion(regionX, regionZ).writeChunk(chunk);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ChunkSection createChunkSection(int y) {
        ChunkSection cs = new ChunkSection(y);
        cs.hasSkyLight = true;
        return cs;
    }

    protected synchronized BaseRegionLoader loadRegion(int x, int z) {
        BaseRegionLoader tmp = lastRegion.get();
        if (tmp != null && x == tmp.getX() && z == tmp.getZ()) {
            return tmp;
        }
        long index = Level.chunkHash(x, z);
        synchronized (regions) {
            BaseRegionLoader region = this.regions.get(index);
            if (region == null) {
                try {
                    region = new RegionLoader(this, x, z);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                this.regions.put(index, region);
            }
            lastRegion.set(region);
            return region;
        }
    }
}
