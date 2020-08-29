package cn.nukkit.level.format.anvil;

import cn.nukkit.Server;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.generic.*;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.math.XXHash64;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
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

            byte[] payload;
            byte[] payloadOld;
            ChunkPacketCache chunkPacketCache;

            @Override
            public void onRun() {
                try {
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

                    Int2IntMap extra = chunk.getBlockExtraDataArray();
                    BinaryStream extraData;
                    if (!extra.isEmpty()) {
                        extraData = new BinaryStream();
                        extraData.putVarInt(extra.size());
                        for (Int2IntMap.Entry entry : extra.int2IntEntrySet()) {
                            extraData.putVarInt(entry.getIntKey());
                            extraData.putLShort(entry.getIntValue());
                        }
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

                    Long2ObjectOpenHashMap<byte[]> clientBlobs = new Long2ObjectOpenHashMap<>(16 + 1); // 16 subChunk + 1 biome

                    LongList blobIds = new LongArrayList();
                    BinaryStream bs = new BinaryStream();
                    for (int i = 0; i < count; i++) {
                        //byte[] subChunk = new byte[6145]; // 1 subChunkVersion (always 0) + 4096 blockIds + 2048 blockMeta
                        bs.reset();
                        //bs.putByte((byte) 0);
                        sections[i].writeToCache(bs);
                        //System.arraycopy(sections[i].getBytes(), 0, subChunk, 1, 6144); // skip subChunkVersion
                        byte[] subChunk = bs.getBuffer();
                        long hash = XXHash64.getHash(subChunk);
                        blobIds.add(hash);
                        clientBlobs.put(hash, subChunk);
                    }

                    byte[] biome = chunk.getBiomeIdArray();
                    long hash = XXHash64.getHash(biome);
                    blobIds.add(hash);
                    clientBlobs.put(hash, biome);

                    byte[] clientBlobCachedPayload = new byte[1 + blockEntities.length]; // borderBlocks + blockEntities
                    System.arraycopy(blockEntities, 0, clientBlobCachedPayload, 1, blockEntities.length); // borderBlocks array size is always 0, skip it

                    chunkBlobCache = new ChunkBlobCache(count, blobIds.toLongArray(), clientBlobs, clientBlobCachedPayload);

                    payload = encodeChunk(false, chunk, sections, count, extraData, blockEntities);
                    payloadOld = encodeChunk(true, chunk, sections, count, extraData, blockEntities);

                    if (level.isCacheChunks()) {
                        chunkPacketCache = new ChunkPacketCache(
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
                    getLevel().chunkRequestCallback(timestamp, x, z, count, chunkBlobCache, chunkPacketCache, payload, payloadOld);
                }
            }
        };
    }

    private byte[] encodeChunk(boolean isOld, Chunk chunk, cn.nukkit.level.format.ChunkSection[] sections, int count, BinaryStream extraData, byte[] blockEntities) {
        BinaryStream stream = ThreadCache.binaryStream.get();
        stream.reset();
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
            stream.put(extraData.getBuffer());
        } else {
            stream.putVarInt(0);
        }*/
        stream.put(blockEntities);

        return stream.getBuffer();
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
