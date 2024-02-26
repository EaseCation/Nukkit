package cn.nukkit.level.format.anvil;

import cn.nukkit.level.HeightRange;
import cn.nukkit.level.Level;
import cn.nukkit.level.LevelCreationOptions;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.LevelProviderManager;
import cn.nukkit.level.format.LevelProviderManager.LevelProviderHandle;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.format.generic.BaseLevelProvider;
import cn.nukkit.level.format.generic.BaseRegionLoader;
import cn.nukkit.level.format.generic.ChunkRequestTask;
import cn.nukkit.level.generator.FlatGeneratorOptions;
import cn.nukkit.level.generator.GeneratorOptions;
import cn.nukkit.level.generator.Generators;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.ChunkException;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@Log4j2
public class Anvil extends BaseLevelProvider {
    public static final int VERSION = 19133;
    public static final Pattern ANVIL_REGEX = Pattern.compile("^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca$");

    static final HeightRange DEFAULT_HEIGHT_RANGE = HeightRange.blockY(0, 256);
    private static final GeneratorOptions GENERATOR_OPTIONS = GeneratorOptions.builder()
            .flatOptions(FlatGeneratorOptions.LEGACY)
            .build();

    public Anvil(Level level, String path) throws IOException {
        super(level, path);
    }

    @SuppressWarnings("unused")
    public static String getProviderName() {
        return "anvil";
    }

    @SuppressWarnings("unused")
    public static byte getProviderOrder() {
        return ORDER_YZX;
    }

    @SuppressWarnings("unused")
    public static boolean usesChunkSection() {
        return true;
    }

    @SuppressWarnings("unused")
    public static boolean isValid(String path) {
        boolean isValid = (new File(path, "level.dat").exists()) && new File(path, "region").isDirectory();
        if (isValid) {
            for (File file : new File(path, "region").listFiles((dir, name) -> REGEX.matcher(name).matches())) {
                if (!file.getName().endsWith(".mca")) {
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
        if (!new File(path + "/region").exists()) {
            new File(path + "/region").mkdirs();
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
                .putInt("version", VERSION)
                .putLong("Time", 0)
                .putLong("SizeOnDisk", 0);

        NBTIO.writeGZIPCompressed(new CompoundTag().putCompound("Data", levelData), Files.newOutputStream(Paths.get(path, "level.dat")), ByteOrder.BIG_ENDIAN);
    }

    @Override
    public Chunk getEmptyChunk(int chunkX, int chunkZ) {
        return Chunk.getEmptyChunk(chunkX, chunkZ, this);
    }

    @Override
    public LevelProviderHandle getHandle() {
        return LevelProviderManager.ANVIL;
    }

    @Override
    public AsyncTask requestChunkTask(int x, int z) throws ChunkException {
        Chunk chunk = (Chunk) this.getChunk(x, z, false);
        if (chunk == null) {
//            throw new ChunkException("Invalid Chunk Set");
            return null;
        }
        return new ChunkRequestTask(chunk);
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

    @SuppressWarnings("unused")
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

    @Override
    public void forEachChunks(Function<FullChunk, Boolean> action, boolean skipCorrupted) {
        File regionDir = new File(path, "region");
        File[] regionFiles = regionDir.listFiles((dir, name) -> name.endsWith(".mca"));

        if (regionFiles == null) {
            return;
        }

        for (File regionFile : regionFiles) {
            Matcher matcher = ANVIL_REGEX.matcher(regionFile.getName());
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

    @Override
    public HeightRange getHeightRange() {
        return DEFAULT_HEIGHT_RANGE;
    }

    @Override
    public GeneratorOptions getWorldGeneratorOptions() {
        return GENERATOR_OPTIONS;
    }
}
