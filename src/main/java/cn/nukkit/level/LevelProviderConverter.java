package cn.nukkit.level;

import cn.nukkit.block.*;
import cn.nukkit.level.biome.Biomes;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.LevelProviderManager.LevelProviderHandle;
import cn.nukkit.level.format.anvil.Anvil;
import cn.nukkit.level.format.anvil.Chunk;
import cn.nukkit.level.format.anvil.RegionLoader;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.format.generic.ChunkConverter;
import cn.nukkit.level.format.leveldb.LevelDB;
import cn.nukkit.level.format.leveldb.LevelDbChunk;
import cn.nukkit.level.format.leveldb.LevelDbConstants;
import cn.nukkit.level.format.leveldb.LevelDbSubChunk;
import cn.nukkit.level.format.mcregion.McRegion;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.LevelException;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Log4j2
class LevelProviderConverter {

    private LevelProvider provider;
    private LevelProviderHandle to;
    private final Level level;
    private final String path;

    LevelProviderConverter(Level level, String path) {
        this.level = level;
        this.path = path;
    }

    LevelProviderConverter from(LevelProvider provider) {
        if (!(provider instanceof McRegion) && !(provider instanceof Anvil)) {
            throw new IllegalArgumentException("From type can be only McRegion or Anvil");
        }
        this.provider = provider;
        return this;
    }

    LevelProviderConverter to(LevelProviderHandle to) {
        if (to.getClazz() != LevelDB.class) {
            throw new IllegalArgumentException("To type can be only LevelDB");
        }
        this.to = to;
        return this;
    }

    private static final Pattern REGEX = Pattern.compile("-?\\d+");

    LevelProvider perform() throws IOException {
        Path dirPath = Paths.get(path);
        Path datNew = dirPath.resolve("level.dat");
        Files.createDirectories(dirPath);

        Path providerPath = Paths.get(provider.getPath());
        Path dat = providerPath.resolve("level.dat");
        Path datBak = providerPath.resolve("level.dat.old");

        Files.copy(dat, datNew, StandardCopyOption.REPLACE_EXISTING);
        Files.move(dat, datBak, StandardCopyOption.REPLACE_EXISTING);

        LevelProvider result;
        try {
            /*if (provider instanceof LevelDB) {
                try (FileInputStream stream = new FileInputStream(path + "level.dat")) {
                    stream.skip(8);
                    CompoundTag levelData = NBTIO.read(stream, ByteOrder.LITTLE_ENDIAN);
                    NBTIO.writeGZIPCompressed(new CompoundTag().putCompound("Data", levelData), new FileOutputStream(path + "level.dat"));
                } catch (IOException e) {
                    throw new LevelException("Invalid level.dat");
                }
            }*/

            if (to.getClazz() == LevelDB.class) {
                byte[] levelData;
                try (InputStream stream = Files.newInputStream(datNew)) {
                    CompoundTag nbt = NBTIO.readCompressed(stream, ByteOrder.BIG_ENDIAN);
                    Tag tag = nbt.get("Data");
                    if (!(tag instanceof CompoundTag)) {
                        throw new IOException("Invalid 'Data' tag");
                    }

                    levelData = NBTIO.write((CompoundTag) tag, ByteOrder.LITTLE_ENDIAN, false);
                } catch (IOException e) {
                    throw new LevelException("Invalid level.dat", e);
                }

                try (OutputStream stream = Files.newOutputStream(datNew)) {
                    stream.write(Binary.writeLInt(LevelDbConstants.CURRENT_STORAGE_VERSION));
                    stream.write(Binary.writeLInt(levelData.length));
                    stream.write(levelData);
                }
            }

            result = to.getInstantiator().create(level, path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (to.getClazz() == Anvil.class) {
            if (provider instanceof McRegion) {
                Files.createDirectories(dirPath.resolve("region"));
                for (File file : new File(provider.getPath() + "region/").listFiles()) {
                    Matcher m = REGEX.matcher(file.getName());
                    int regionX, regionZ;
                    try {
                        if (m.find()) {
                            regionX = Integer.parseInt(m.group());
                        } else continue;
                        if (m.find()) {
                            regionZ = Integer.parseInt(m.group());
                        } else continue;
                    } catch (NumberFormatException e) {
                        continue;
                    }
                    cn.nukkit.level.format.mcregion.RegionLoader region = new cn.nukkit.level.format.mcregion.RegionLoader(provider, regionX, regionZ);
                    for (Integer index : region.getLocationIndexes()) {
                        int chunkX = index & 0x1f;
                        int chunkZ = index >> 5;
                        BaseFullChunk old = region.readChunk(chunkX, chunkZ);
                        if (old == null) continue;
                        int x = (regionX << 5) | chunkX;
                        int z = (regionZ << 5) | chunkZ;
                        FullChunk chunk = new ChunkConverter(result)
                                .from(old)
                                .to(Chunk.class)
                                .perform();
                        result.saveChunk(x, z, chunk);
                    }
                    region.close();
                }
            } else if (provider instanceof LevelDB) {
                throw new IllegalArgumentException("no longer available");
                /*new File(path, "region").mkdir();
                for (byte[] key : ((LevelDB) provider).getTerrainKeys()) {
                    int x = getChunkX(key);
                    int z = getChunkZ(key);
                    BaseFullChunk old = ((LevelDB) provider).readChunk(x, z);
                    FullChunk chunk = new ChunkConverter(result)
                            .from(old)
                            .to(Chunk.class)
                            .perform();
                    result.saveChunk(x, z, chunk);
                }*/
            }
            result.doGarbageCollection();
        } else if (to.getClazz() == LevelDB.class) {
            if (provider instanceof McRegion) {
                throw new IllegalArgumentException("Please convert to Anvil first");
            }
            if (provider instanceof Anvil) {
                int totalChunks = anvilToLevelDb((Anvil) provider, (LevelDB) result);
                log.info("{} chunks have been converted", totalChunks);
            }
            result.doGarbageCollection();
        }

        return result;
    }

    private static final int WORKER_COUNT = Runtime.getRuntime().availableProcessors();

    /**
     * @return success chunk count
     */
    private int anvilToLevelDb(Anvil anvil, LevelDB levelDb) {
        File regionDir = new File(anvil.getPath(), "region");
        File[] regionFiles = regionDir.listFiles((dir, name) -> name.endsWith(".mca"));

        if (regionFiles == null || regionFiles.length == 0) {
            return 0;
        }

        int fileCount = regionFiles.length;
        int workerCount = Math.min(fileCount, WORKER_COUNT);
        CompletableFuture<Integer>[] futures = new CompletableFuture[workerCount];
        AtomicInteger cursor = new AtomicInteger();
        for (int i = 0; i < workerCount; i++) {
            futures[i] = CompletableFuture.supplyAsync(() -> {
                int count = 0;
                int index;
                while ((index = cursor.getAndIncrement()) < fileCount) {
                    count += processRegionFile(anvil, levelDb, regionFiles[index]);
                }
                return count;
            });
        }
        CompletableFuture.allOf(futures).join();

        int totalChunks = 0;
        for (CompletableFuture<Integer> future : futures) {
            try {
                totalChunks += future.get();
            } catch (Exception e) {
                log.error("Failed to get conversion count", e);
            }
        }
        return totalChunks;
    }

    private int processRegionFile(Anvil anvil, LevelDB levelDb, File regionFile) {
        Matcher matcher = Anvil.ANVIL_REGEX.matcher(regionFile.getName());
        if (!matcher.matches()) {
            return 0;
        }

        int regionX;
        int regionZ;
        try {
            regionX = Integer.parseInt(matcher.group(1));
            regionZ = Integer.parseInt(matcher.group(2));
        } catch (Exception e) {
            log.error("Skipped invalid region: {} in {}", regionFile, anvil.getPath(), e);
            return 0;
        }

        RegionLoader region;
        try {
            region = new RegionLoader(anvil, regionX, regionZ);
        } catch (Exception e) {
            log.error("Skipped corrupted region: {} in {}", regionFile, anvil.getPath(), e);
            return 0;
        }

        int totalChunks = 0;
        for (int x = 0; x < 32; x++) {
            for (int z = 0; z < 32; z++) {
                if (!region.chunkExists(x, z)) {
                    continue;
                }

                Chunk chunk;
                try {
                    chunk = region.readChunk(x, z);
                } catch (Exception e) {
                    log.error("Skipped corrupted chunk: region {},{} pos {},{} in {}", regionX, regionZ, x, z, anvil.getPath(), e);
                    continue;
                }

                if (chunk == null) {
                    continue;
                }

                try {
                    LevelDbChunk dbChunk = anvilChunkToLevelDbChunk(chunk, levelDb);
                    levelDb.saveChunk(x, z, dbChunk);

                    /*List<BlockEntity> blockEntities = new ObjectArrayList<>(dbChunk.getBlockEntities().values());
                    for (BlockEntity blockEntity : blockEntities) {
                        if (blockEntity == null || blockEntity.isClosed()) {
                            continue;
                        }
                        try {
                            level.removeBlockEntity(blockEntity);
                        } catch (Exception e) {
                            log.debug("Failed to remove block entity {}", blockEntity, e);
                        }
                    }

                    List<Entity> entities = new ObjectArrayList<>(dbChunk.getEntities().values());
                    for (Entity entity : entities) {
                        if (entity == null || entity.isClosed()) {
                            continue;
                        }
                        try {
                            level.removeEntityDirect(entity);
                        } catch (Exception e) {
                            log.debug("Failed to remove entity {}", entity, e);
                        }
                    }*/
                } catch (Exception e) {
                    log.error("chunk conversion failed: region {},{} pos {},{} in {}", regionX, regionZ, x, z, anvil.getPath(), e);
                    continue;
                }

                totalChunks++;
            }
        }

        try {
            region.close();
        } catch (Exception e) {
            log.error("An error occurred while unloading region: {} in {}", regionFile, anvil.getPath(), e);
        }
        return totalChunks;
    }

    private LevelDbChunk anvilChunkToLevelDbChunk(Chunk oldChunk, LevelDB levelDb) {
        LevelDbChunk chunk = new LevelDbChunk(levelDb, oldChunk.getX(), oldChunk.getZ());

        ChunkSection[] oldSections = oldChunk.getSections();
        if (oldSections != null) {
            for (ChunkSection oldSection : oldSections) {
                if (oldSection.isEmpty()) {
                    continue;
                }

                LevelDbSubChunk subChunk = (LevelDbSubChunk) chunk.getSection(oldSection.getY());
                for (int y = 0; y < 16; y++) {
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            int id = oldSection.getBlockId(0, x, y, z);
                            int meta = oldSection.getBlockData(0, x, y, z);

                            // special Log upgrade to Wood
                            if (id == BlockID.LOG) {
                                if ((meta & 0b1100) == 0b1100 && Block.list[BlockID.WOOD] != null) {
                                    id = BlockID.WOOD;
                                    meta = meta & 0b11;
                                }
                            } else if (id == BlockID.LOG2) {
                                if ((meta & 0b1100) == 0b1100 && Block.list[BlockID.WOOD] != null) {
                                    id = BlockID.WOOD;
                                    meta = 0b100 | (meta & 0b1);
                                } else {
                                    meta = BlockLog.LOG2_NUKKIT_LEGACY_META_TO_NUKKIT_RUNTIME_META[meta & 0xf];
                                }
                            } else if (id == BlockID.LEAVES) {
                                meta = BlockLeaves.LEAVES1_NUKKIT_LEGACY_META_TO_NUKKIT_RUNTIME_META[meta & 0xf];
                            } else if (id == BlockID.LEAVES2) {
                                meta = BlockLeaves.LEAVES2_NUKKIT_LEGACY_META_TO_NUKKIT_RUNTIME_META[meta & 0xf];
                            } else if (id == BlockID.FRAME) {
                                // frame 1.13 upgrade
                                meta = ((meta & ~0b11) << 1) | BlockItemFrame.LEGACY_DIRECTION_BITS_TO_FACING_DIRECTION_BITS[meta & 0b11];
                            } else if (!Block.hasMeta[id]) {
                                // additional fixes for corrupted meta... (FAWE?)
                                meta = 0;
                            }

                            subChunk.setBlock(0, x, y, z, id, meta);
                        }
                    }
                }
            }

            chunk.recalculateHeightMap();
        }

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                chunk.setBiomeId(x, z, Biomes.toValid(oldChunk.getBiomeId(x, z)));
            }
        }

        List<CompoundTag> blockEntities = oldChunk.getBlockEntityTags();
        if (blockEntities != null && !blockEntities.isEmpty()) {
            chunk.setBlockEntityTags(blockEntities.stream()
                    .map(CompoundTag::copy)
                    .collect(Collectors.toList()));
        }
        List<CompoundTag> entities = oldChunk.getEntityTags();
        if (entities != null && !entities.isEmpty()) {
            chunk.setEntityTags(entities.stream()
                    .map(CompoundTag::copy)
                    .collect(Collectors.toList()));
        }

        chunk.setBlockUpdateEntries(oldChunk.getBlockUpdateEntries());

        chunk.setGenerated(oldChunk.isGenerated());
        chunk.setPopulated(oldChunk.isPopulated());
        chunk.setLightPopulated(oldChunk.isLightPopulated());

//        chunk.initChunk();
//        chunk.fixCorruptedBlockEntities();

        return chunk;
    }
}
