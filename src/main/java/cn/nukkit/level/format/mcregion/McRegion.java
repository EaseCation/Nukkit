package cn.nukkit.level.format.mcregion;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.level.Level;
import cn.nukkit.level.LevelCreationOptions;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.LevelProviderManager;
import cn.nukkit.level.format.LevelProviderManager.LevelProviderHandle;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.format.generic.BaseLevelProvider;
import cn.nukkit.level.format.generic.BaseRegionLoader;
import cn.nukkit.level.generator.Generators;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.ChunkException;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@Log4j2
public class McRegion extends BaseLevelProvider {
    private static final Pattern REGION_REGEX = Pattern.compile("^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mcr$");

    public McRegion(Level level, String path) throws IOException {
        super(level, path);
    }

    @SuppressWarnings("unused")
    public static String getProviderName() {
        return "mcregion";
    }

    @SuppressWarnings("unused")
    public static byte getProviderOrder() {
        return ORDER_ZXY;
    }

    @SuppressWarnings("unused")
    public static boolean usesChunkSection() {
        return false;
    }

    @SuppressWarnings("unused")
    public static boolean isValid(String path) {
        boolean isValid = (new File(path, "level.dat").exists()) && new File(path, "region").isDirectory();
        if (isValid) {
            for (File file : new File(path, "region").listFiles((dir, name) -> REGEX.matcher(name).matches())) {
                if (!file.getName().endsWith(".mcr")) {
                    isValid = false;
                    break;
                }
            }
        }
        return isValid;
    }

    @SuppressWarnings("unused")
    public static void generate(String path, String name) throws IOException {
        generate(path, name, LevelCreationOptions.builder().build());
    }

    public static void generate(String path, String name, LevelCreationOptions options) throws IOException {
        if (!new File(path, "region").exists()) {
            new File(path, "region").mkdirs();
        }

        CompoundTag levelData = new CompoundTag("Data")
                .putCompound("GameRules", new CompoundTag())
                .putLong("DayTime", 0)
                .putInt("GameType", 0)
                .putString("generatorName", Generators.getGeneratorName(options.getGenerator()))
                .putString("generatorOptions", String.valueOf(options.getOptions().getOrDefault("preset", "")))
                .putInt("generatorVersion", 1)
                .putBoolean("hardcore", false)
                .putBoolean("initialized", true)
                .putLong("LastPlayed", System.currentTimeMillis() / 1000)
                .putString("LevelName", name)
                .putBoolean("raining", false)
                .putInt("rainTime", 0)
                .putLong("RandomSeed", options.getSeed())
                .putInt("SpawnX", 128)
                .putInt("SpawnY", 70)
                .putInt("SpawnZ", 128)
                .putBoolean("thundering", false)
                .putInt("thunderTime", 0)
                .putInt("version", 19133)
                .putLong("Time", 0)
                .putLong("SizeOnDisk", 0);

        NBTIO.writeGZIPCompressed(new CompoundTag().putCompound("Data", levelData), Files.newOutputStream(new File(path, "level.dat").toPath()), ByteOrder.BIG_ENDIAN);
    }

    @Override
    public LevelProviderHandle getHandle() {
        return LevelProviderManager.MCREGION;
    }

    @Deprecated
    @Override
    public AsyncTask requestChunkTask(int x, int z) throws ChunkException {
        BaseFullChunk chunk = this.getChunk(x, z, false);
        if (chunk == null) {
            throw new ChunkException("Invalid Chunk Sent");
        }

        long timestamp = chunk.getChanges();

        byte[] tiles = new byte[0];

        if (!chunk.getBlockEntities().isEmpty()) {
            List<CompoundTag> tagList = new ObjectArrayList<>();

            for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                if (blockEntity instanceof BlockEntitySpawnable) {
                    CompoundTag nbt = ((BlockEntitySpawnable) blockEntity).getSpawnCompound();
                    if (nbt.isEmpty()) {
                        continue;
                    }
                    tagList.add(nbt);
                }
            }

            try {
                tiles = NBTIO.write(tagList, ByteOrder.LITTLE_ENDIAN, true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Int2IntMap extra = chunk.getBlockExtraDataArray();
        BinaryStream extraData;
        if (!extra.isEmpty()) {
            extraData = new BinaryStream();
            extraData.putLInt(extra.size());
            for (Int2IntMap.Entry entry : extra.int2IntEntrySet()) {
                extraData.putLInt(entry.getIntKey());
                extraData.putLShort(entry.getIntValue());
            }
        } else {
            extraData = null;
        }

        BinaryStream stream = new BinaryStream();
        stream.put(chunk.getBlockIdArray());
        stream.put(chunk.getBlockDataArray());
        stream.put(chunk.getBlockSkyLightArray());
        stream.put(chunk.getBlockLightArray());
        stream.put(chunk.getHeightMapArray());
        stream.put(chunk.getBiomeIdArray());
        if (extraData != null) {
            stream.put(extraData.getBuffer());
        } else {
            stream.putLInt(0);
        }
        stream.put(tiles);

        byte[] payload = stream.getBuffer();
        this.getLevel().chunkRequestCallback(timestamp, x, z, 16, null, null, payload, payload, payload, payload, Collections.emptyMap(), Collections.emptyMap(), null, null, null);

        return null;
    }

    @Override
    public BaseFullChunk loadChunk(long index, int chunkX, int chunkZ, boolean create) {
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

    public Chunk getEmptyChunk(int chunkX, int chunkZ) {
        return Chunk.getEmptyChunk(chunkX, chunkZ, this);
    }

    @Override
    public void saveChunk(int x, int z) {
        if (this.isChunkLoaded(x, z)) {
            try {
                this.getRegion(x >> 5, z >> 5).writeChunk(this.getChunk(x, z));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void saveChunk(int x, int z, FullChunk chunk) {
        if (!(chunk instanceof Chunk)) {
            throw new ChunkException("Invalid Chunk class");
        }
        this.loadRegion(x >> 5, z >> 5);
        chunk.setPosition(x, z);
        try {
            this.getRegion(x >> 5, z >> 5).writeChunk(chunk);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unused")
    public static ChunkSection createChunkSection(int y) {
        return null;
    }

    protected BaseRegionLoader loadRegion(int x, int z) {
        BaseRegionLoader tmp = lastRegion.get();
        if (tmp != null && x == tmp.getX() && z == tmp.getZ()) {
            return tmp;
        }
        long index = Level.chunkHash(x, z);
        synchronized (regions) {
            BaseRegionLoader region = this.regions.get(index);
            if (region == null) {
                region = new RegionLoader(this, x, z);
                this.regions.put(index, region);
            }
            lastRegion.set(region);
            return region;
        }
    }

    @Override
    public void forEachChunks(Function<FullChunk, Boolean> action) {
        forEachChunks(action, false);
    }

    @Override
    public void forEachChunks(Function<FullChunk, Boolean> action, boolean skipCorrupted) {
        File regionDir = new File(path, "region");
        File[] regionFiles = regionDir.listFiles((dir, name) -> name.endsWith(".mcr"));

        if (regionFiles == null) {
            return;
        }

        for (File regionFile : regionFiles) {
            Matcher matcher = REGION_REGEX.matcher(regionFile.getName());
            if (!matcher.matches()) {
                continue;
            }

            int regionX;
            int regionZ;
            try {
                regionX = Integer.parseInt(matcher.group(1));
                regionZ = Integer.parseInt(matcher.group(2));
            } catch (Exception e) {
                log.error("Skipped invalid region: {}", regionFile, e);
                continue;
            }

            RegionLoader region;
            try {
                region = new RegionLoader(this, regionX, regionZ);
            } catch (Exception e) {
                log.error("Skipped corrupted region: {}", regionFile, e);
                continue;
            }

            for (int x = 0; x < 32; x++) {
                for (int z = 0; z < 32; z++) {
                    if (!region.chunkExists(x, z)) {
                        continue;
                    }

                    Chunk chunk;
                    try {
                        chunk = region.readChunk(x, z);
                    } catch (Exception e) {
                        if (!skipCorrupted) {
                            throw new ChunkException(e);
                        }
                        log.error("Skipped corrupted chunk: region {},{} pos {},{}", regionX, regionZ, x, z, e);
                        continue;
                    }

                    if (chunk == null) {
                        continue;
                    }

                    if (!action.apply(chunk)) {
                        try {
                            region.close();
                        } catch (Exception e) {
                            log.error("An error occurred while unloading region: {}", regionFile, e);
                        }
                        return;
                    }
                }
            }

            try {
                region.close();
            } catch (Exception e) {
                log.error("An error occurred while unloading region: {}", regionFile, e);
            }
        }
    }
}
