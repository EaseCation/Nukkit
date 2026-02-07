package cn.nukkit.level.format.leveldb;

import cn.nukkit.block.Block;
import cn.nukkit.level.HeightRange;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.biome.Biomes;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.LevelProviderManager;
import cn.nukkit.level.format.LevelProviderManager.LevelProviderHandle;
import cn.nukkit.level.format.generic.BaseChunk;
import cn.nukkit.level.format.generic.EmptyChunkSection;
import cn.nukkit.level.util.PalettedSubChunkStorage;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.ChunkException;
import it.unimi.dsi.fastutil.ints.Int2ByteMap;
import it.unimi.dsi.fastutil.ints.Int2ByteOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static cn.nukkit.level.format.leveldb.LevelDbConstants.*;

public class LevelDbChunk extends BaseChunk {
    protected static final byte SECTION_COUNT = 64;

    protected PalettedSubChunkStorage[] biomes3d;
    protected final Lock biomeReadLock;
    protected final Lock biomeWriteLock;
    protected Int2ByteMap biomeStates;

    protected HeightRange heightRange;
    /**
     * ZZZZXXXX key bit order.
     */
    protected short[] heightmap;

    protected boolean[] borders;

    //TODO: atomic?
    protected boolean terrainGenerated;
    protected boolean terrainPopulated;

    protected boolean subChunksDirty;
    protected boolean heightmapOrBiomesDirty;
    protected boolean biomeStatesDirty;
    protected boolean bordersDirty;
    //TODO: more DirtyTicksCounter
//    protected boolean blockTickingDirty;
//    protected boolean randomBlockTickingDirty;
//    protected boolean blockEntitiesDirty;
//    protected boolean entitiesDirty;

    protected final Lock ioLock;

    public LevelDbChunk(@Nullable LevelProvider level, int chunkX, int chunkZ) {
        this(level, chunkX, chunkZ, null, new short[SUB_CHUNK_2D_SIZE], null, null, null, null, new boolean[SUB_CHUNK_2D_SIZE]);
    }

    public LevelDbChunk(@Nullable LevelProvider level, int chunkX, int chunkZ, @Nullable LevelDbSubChunk[] sections,
                        @Nullable short[] heightmap, @Nullable PalettedSubChunkStorage[] biomes, @Nullable Int2ByteMap biomeStates,
                        @Nullable List<CompoundTag> entities, @Nullable List<CompoundTag> blockEntities,
                        @Nullable boolean[] borders) {
        this.ioLock = new ReentrantLock();
        this.provider = level;
        if (level != null) {
            heightRange = level.getHeightRange();
        }
        this.setPosition(chunkX, chunkZ);

        ReentrantReadWriteLock biomeLock = new ReentrantReadWriteLock();
        this.biomeReadLock = biomeLock.readLock();
        this.biomeWriteLock = biomeLock.writeLock();

        if (sections != null) {
            if (sections.length == SECTION_COUNT) {
                this.sections = sections;
            } else {
                this.sections = Arrays.copyOf(sections, SECTION_COUNT);
            }
        } else {
            this.sections = new LevelDbSubChunk[SECTION_COUNT];
        }
        HeightRange heightRange = getHeightRange();
        for (int chunkY = heightRange.getMinChunkY(); chunkY < heightRange.getMaxChunkY(); chunkY++) {
            int index = Level.subChunkYtoIndex(chunkY);
            ChunkSection section = this.sections[index];
            if (section == null) {
                section = new LevelDbSubChunk(this, chunkY);
                this.sections[index] = section;
            }
            ((LevelDbSubChunk) section).setParent(this);
        }

        if (heightmap != null && heightmap.length == SUB_CHUNK_2D_SIZE) {
            this.heightmap = heightmap;
        } else {
            this.heightmap = new short[SUB_CHUNK_2D_SIZE];
            this.recalculateHeightMap();
        }

        if (biomes != null) {
            if (biomes.length == SECTION_COUNT) {
                this.biomes3d = biomes;
            } else {
                this.biomes3d = Arrays.copyOf(biomes, SECTION_COUNT);
            }
        } else {
            this.biomes3d = new PalettedSubChunkStorage[SECTION_COUNT];
        }
        int bottomIndex = Level.subChunkYtoIndex(heightRange.getMinChunkY());
        if (this.biomes3d[bottomIndex] == null) {
            this.biomes3d[bottomIndex] = PalettedSubChunkStorage.ofBiome(BiomeID.OCEAN);
        }

        if (biomeStates != null) {
            this.biomeStates = biomeStates;
        } else {
            this.biomeStates = new Int2ByteOpenHashMap();
        }

        if (borders != null && borders.length == SUB_CHUNK_2D_SIZE) {
            this.borders = borders;
        } else {
            this.borders = new boolean[SUB_CHUNK_2D_SIZE];
        }

        this.NBTentities = entities;
        this.NBTtiles = blockEntities;
    }

    @Override
    public void setProvider(LevelProvider provider) {
        super.setProvider(provider);
        if (provider != null) {
            heightRange = provider.getHeightRange();
        }
    }

    @Override
    public LevelProviderHandle getProviderHandle() {
        return LevelProviderManager.LEVELDB;
    }

    @Override
    public boolean isGenerated() {
        return this.terrainGenerated || this.terrainPopulated;
    }

    @Override
    public void setGenerated() {
        this.setGenerated(true);
    }

    @Override
    public void setGenerated(boolean state) {
        if (this.terrainGenerated != state) {
            this.terrainGenerated = state;
            this.setChanged();
        }
    }

    @Override
    public boolean isPopulated() {
        return this.terrainPopulated;
    }

    @Override
    public void setPopulated() {
        this.setPopulated(true);
    }

    @Override
    public void setPopulated(boolean state) {
        if (this.terrainPopulated != state) {
            this.terrainPopulated = state;
            this.setChanged();
        }
    }

    @Override
    public PalettedSubChunkStorage[] getBiomes() {
        return this.biomes3d;
    }

    @Override
    public Int2ByteMap getBiomeStates() {
        return this.biomeStates;
    }

    @Override
    public short[] getHeightmap() {
        return this.heightmap;
    }

    @Override
    public boolean[] getBorders() {
        return borders;
    }

    @Override
    public int getBiomeId(int x, int z) {
        int bottomIndex = Level.subChunkYtoIndex(getHeightRange().getMinChunkY());

        biomeReadLock.lock();
        try {
            return this.biomes3d[bottomIndex].get(x, 0, z);
        } finally {
            biomeReadLock.unlock();
        }
    }

    @Override
    public int getBiomeId(int x, int y, int z) {
        int chunkY = y >> 4;
        int index = Level.subChunkYtoIndex(chunkY);

        biomeReadLock.lock();
        try {
            PalettedSubChunkStorage storage = this.biomes3d[index];

            if (storage == null) {
                int minChunkY = getHeightRange().getMinChunkY();
                for (int subChunkY = chunkY - 1; subChunkY >= minChunkY; subChunkY--) {
                    PalettedSubChunkStorage below = this.biomes3d[Level.subChunkYtoIndex(subChunkY)];
                    if (below == null) {
                        continue;
                    }
                    return below.get(x, 15, z);
                }

                return BiomeID.OCEAN;
            }

            return storage.get(x, y & 0xf, z);
        } finally {
            biomeReadLock.unlock();
        }
    }

    @Override
    public void setBiomeId(int x, int z, int biomeId) {
        int bottomIndex = Level.subChunkYtoIndex(getHeightRange().getMinChunkY());

        biomeWriteLock.lock();
        try {
            PalettedSubChunkStorage storage = this.biomes3d[bottomIndex];

            int previous = storage.get(x, 0, z);
            if (previous == biomeId) {
                return;
            }

            for (int y = 0; y < 16; y++) {
                storage.set(x, y, z, biomeId);
            }
        } finally {
            biomeWriteLock.unlock();
        }

        this.heightmapOrBiomesDirty = true;
        this.setChanged();
    }

    @Override
    public void setBiomeId(int x, int y, int z, int biomeId) {
        int chunkY = y >> 4;
        int index = Level.subChunkYtoIndex(chunkY);
        int localY = y & 0xf;

        biomeWriteLock.lock();
        try {
            PalettedSubChunkStorage storage = this.biomes3d[index];

            if (storage == null) {
                HeightRange heightRange = getHeightRange();
                for (int subChunkY = chunkY - 1; subChunkY >= heightRange.getMinChunkY(); subChunkY--) {
                    PalettedSubChunkStorage below = this.biomes3d[Level.subChunkYtoIndex(subChunkY)];
                    if (below == null) {
                        continue;
                    }

                    if (localY == 0 && below.get(x, 15, z) == biomeId) {
                        return;
                    }

                    storage = below.copy();
                    this.biomes3d[index] = storage;

                    int aboveChunkY;
                    if (localY == 15 && (aboveChunkY = chunkY + 1) < heightRange.getMaxChunkY()) {
                        this.biomes3d[Level.subChunkYtoIndex(aboveChunkY)] = below.copy();
                    }
                    break;
                }
                assert storage != null : "bottom biome storage is null";
            } else {
                int previous = storage.get(x, localY, z);
                if (previous == biomeId) {
                    return;
                }
            }

            storage.set(x, localY, z, biomeId);
        } finally {
            biomeWriteLock.unlock();
        }

        this.heightmapOrBiomesDirty = true;
        this.setChanged();
    }

    @Override
    public void fillBiome(int biomeId) {
        HeightRange heightRange = getHeightRange();
        int minChunkHeight = heightRange.getMinChunkY();
        int maxChunkHeight = heightRange.getMaxChunkY();

        int bottomIndex = Level.subChunkYtoIndex(minChunkHeight);
        PalettedSubChunkStorage storage = PalettedSubChunkStorage.ofBiome(biomeId);

        biomeWriteLock.lock();
        try {
            biomes3d[bottomIndex] = storage;

            for (int chunkY = minChunkHeight + 1; chunkY < maxChunkHeight; chunkY++) {
                biomes3d[Level.subChunkYtoIndex(chunkY)] = null;
            }
        } finally {
            biomeWriteLock.unlock();
        }
    }

    @Override
    public void writeBiomeTo(BinaryStream stream, boolean network, IntList customBiomeIds) {
        HeightRange heightRange = getHeightRange();
        int minChunkHeight = heightRange.getMinChunkY();
        int maxChunkHeight = heightRange.getMaxChunkY();

        biomeReadLock.lock();
        try {
            for (int chunkY = minChunkHeight; chunkY < maxChunkHeight; chunkY++) {
                PalettedSubChunkStorage storage = biomes3d[Level.subChunkYtoIndex(chunkY)];

                if (storage == null) {
                    stream.putByte((byte) -1);
                    continue;
                }

                if (network) {
                    boolean legacy = customBiomeIds == null;
                    // make sure we aren't sending bogus biomes - the 1.18.0 client crashes if we do this
                    storage.writeTo(stream, id -> Biomes.toClientId(id, legacy));
                } else {
                    storage.writeToDiskBiome(stream, customBiomeIds != null ? customBiomeIds : IntLists.emptyList());
                }
            }
        } finally {
            biomeReadLock.unlock();
        }
    }

    @Override
    public int getFullBlock(int layer, int x, int y, int z) {
        return this.sections[Level.subChunkYtoIndex(y >> 4)].getFullBlock(layer, x, y & 0xf, z);
    }

    @Override
    public Block getAndSetBlock(int layer, int x, int y, int z, Block block) {
        int index = Level.subChunkYtoIndex(y >> 4);
        try {
            setChanged();
            return this.sections[index].getAndSetBlock(layer, x, y & 0xf, z, block);
        } catch (ChunkException e) {
            this.setInternalSection(index, getProviderHandle().getSubChunkFactory().create(index));
            return this.sections[index].getAndSetBlock(layer, x, y & 0xf, z, block);
        } finally {
            if (layer == 0) {
                removeInvalidTile(x, y, z);
            }
        }
    }

    @Override
    public boolean setFullBlockId(int layer, int x, int y, int z, int fullId) {
        int index = Level.subChunkYtoIndex(y >> 4);
        try {
            setChanged();
            return this.sections[index].setFullBlockId(layer, x, y & 0xf, z, fullId);
        } catch (ChunkException e) {
            this.setInternalSection(index, getProviderHandle().getSubChunkFactory().create(index));
            return this.sections[index].setFullBlockId(layer, x, y & 0xf, z, fullId);
        } finally {
            if (layer == 0) {
                removeInvalidTile(x, y, z);
            }
        }
    }

    @Override
    public boolean setBlock(int layer, int x, int y, int z, int blockId, int meta) {
        int index = Level.subChunkYtoIndex(y >> 4);
        try {
            setChanged();
            return this.sections[index].setBlock(layer, x, y & 0xf, z, blockId, meta);
        } catch (ChunkException e) {
            this.setInternalSection(index, getProviderHandle().getSubChunkFactory().create(index));
            return this.sections[index].setBlock(layer, x, y & 0xf, z, blockId, meta);
        } finally {
            if (layer == 0) {
                removeInvalidTile(x, y, z);
            }
        }
    }

    @Override
    public void setBlockId(int layer, int x, int y, int z, int id) {
        int index = Level.subChunkYtoIndex(y >> 4);
        try {
            this.sections[index].setBlockId(layer, x, y & 0xf, z, id);
            setChanged();
        } catch (ChunkException e) {
            this.setInternalSection(index, getProviderHandle().getSubChunkFactory().create(index));
            this.sections[index].setBlockId(layer, x, y & 0xf, z, id);
        } finally {
            if (layer == 0) {
                removeInvalidTile(x, y, z);
            }
        }
    }

    @Override
    public int getBlockId(int layer, int x, int y, int z) {
        return this.sections[Level.subChunkYtoIndex(y >> 4)].getBlockId(layer, x, y & 0xf, z);
    }

    @Override
    public int getBlockData(int layer, int x, int y, int z) {
        return this.sections[Level.subChunkYtoIndex(y >> 4)].getBlockData(layer, x, y & 0xf, z);
    }

    @Override
    public void setBlockData(int layer, int x, int y, int z, int data) {
        int index = Level.subChunkYtoIndex(y >> 4);
        try {
            this.sections[index].setBlockData(layer, x, y & 0xf, z, data);
            setChanged();
        } catch (ChunkException e) {
            this.setInternalSection(index, getProviderHandle().getSubChunkFactory().create(index));
            this.sections[index].setBlockData(layer, x, y & 0xf, z, data);
        } finally {
            if (layer == 0) {
                removeInvalidTile(x, y, z);
            }
        }
    }

    @Override
    public int getBlockSkyLight(int x, int y, int z) {
        return this.getHighestBlockAt(x, z) >= y ? 15 : 0;
    }

    @Override
    public void setBlockSkyLight(int x, int y, int z, int level) {
        int index = Level.subChunkYtoIndex(y >> 4);
        try {
            this.sections[index].setBlockSkyLight(x, y & 0xf, z, level);
            setChanged();
        } catch (ChunkException e) {
            this.setInternalSection(index, getProviderHandle().getSubChunkFactory().create(index));
            this.sections[index].setBlockSkyLight(x, y & 0xf, z, level);
        }
    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        return Block.light[this.getBlockId(0, x, y, z)];
    }

    @Override
    public void setBlockLight(int x, int y, int z, int level) {
        int index = Level.subChunkYtoIndex(y >> 4);
        try {
            this.sections[index].setBlockLight(x, y & 0xf, z, level);
            setChanged();
        } catch (ChunkException e) {
            this.setInternalSection(index, getProviderHandle().getSubChunkFactory().create(index));
            this.sections[index].setBlockLight(x, y & 0xf, z, level);
        }
    }

    @Override
    public void populateSkyLight() {
        //TODO
    }

    @Override
    public int getHighestBlockAt(int x, int z, boolean cache) {
        HeightRange heightRange = getHeightRange();

        if (cache) {
            int height = this.getHeightmapValue(x, z);
            return height > 0 ? Level.indexToY(height - 1, heightRange.getYIndexOffset()) : heightRange.getMinY();
        }

        for (int chunkY = heightRange.getMaxChunkY() - 1; chunkY >= heightRange.getMinChunkY(); chunkY--) {
            ChunkSection subChunk = this.sections[Level.subChunkYtoIndex(chunkY)];
            int baseY = chunkY << 4;
            for (int localY = 15; localY >= 0; localY--) {
                if (!Block.lightBlocking[subChunk.getBlockId(0, x, localY, z)]) {
                    continue;
                }
                int y = baseY | localY;
                this.setHeightMap(x, z, y + 1);
                return y;
            }
        }
        this.setHeightmapValue(x, z, 0);
        return heightRange.getMinY();
    }

    @Override
    public int getHeightMap(int x, int z) {
        return Level.indexToY(getHeightmapValue(x, z), getHeightRange().getYIndexOffset());
    }

    private int getHeightmapValue(int x, int z) {
        return this.heightmap[index2d(x, z)];
    }

    @Override
    public void setHeightMap(int x, int z, int value) {
        setHeightmapValue(x, z, Level.yToIndex(value, getHeightRange().getYIndexOffset()));
    }

    private void setHeightmapValue(int x, int z, int value) {
        int index = index2d(x, z);
        if (this.heightmap[index] == value) {
            return;
        }
        this.heightmap[index] = (short) value;

        this.heightmapOrBiomesDirty = true;
        this.setChanged();
    }

    @Override
    public void recalculateHeightMap() {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                this.getHighestBlockAt(x, z, false);
            }
        }
    }

    @Override
    public boolean hasBorder(int x, int z) {
        return borders[index2d(x, z)];
    }

    private void setBorder(int x, int z, boolean value) {
        int index = index2d(x, z);
        if (this.borders[index] == value) {
            return;
        }
        this.borders[index] = value;

        this.bordersDirty = true;
        this.setChanged();
    }

    protected void onSubChunkBlockChanged(LevelDbSubChunk subChunk, int layer, int x, int y, int z, int previousId, int newId) {
        assert previousId != newId;

        subChunksDirty = true;

        if (layer != 0) {
            return;
        }

        int previousBlockId = previousId >> Block.BLOCK_META_BITS;
        int newBlockId = newId >> Block.BLOCK_META_BITS;
        if (previousBlockId == newBlockId) {
            return;
        }

        if (newBlockId == Block.BORDER_BLOCK) {
            setBorder(x, z, true);
        } else if (previousBlockId == Block.BORDER_BLOCK && hasBorder(x, z)) {
            boolean border = false;
            HeightRange heightRange = getHeightRange();
            BORDER:
            for (int chunkY = heightRange.getMaxChunkY() - 1; chunkY >= heightRange.getMinChunkY(); chunkY--) {
                ChunkSection section = this.sections[Level.subChunkYtoIndex(chunkY)];
                if (section.isEmpty(true)) {
                    continue;
                }
                for (int localY = 15; localY >= 0; localY--) {
                    if (section.getBlockId(0, x, localY, z) == Block.BORDER_BLOCK) {
                        border = true;
                        break BORDER;
                    }
                }
            }
            if (!border) {
                setBorder(x, z, false);
            }
        }

        boolean lightBlocking = Block.lightBlocking[newBlockId];
        if (lightBlocking == Block.lightBlocking[previousBlockId]) {
            return;
        }

        if (lightBlocking) {
            int height = getHeightMap(x, z);
            int worldY = (subChunk.getY() << 4) | y;
            if (height <= worldY) {
                setHeightMap(x, z, worldY + 1);
            }
        } else {
            int subChunkY = subChunk.getY();
            int worldY = (subChunkY << 4) | y;
            int height = getHeightMap(x, z);
            if (height == (worldY + 1)) {
                for (int localY = y; localY >= 0; localY--) {
                    if (!Block.lightBlocking[subChunk.getBlockId(0, x, localY, z)]) {
                        continue;
                    }
                    this.setHeightMap(x, z, ((subChunkY << 4) | localY) + 1);
                    return;
                }

                // thread safe?
                for (int chunkY = subChunkY - 1; chunkY >= getHeightRange().getMinChunkY(); chunkY--) {
                    ChunkSection section = this.sections[Level.subChunkYtoIndex(chunkY)];
                    for (int localY = 15; localY >= 0; localY--) {
                        if (!Block.lightBlocking[section.getBlockId(0, x, localY, z)]) {
                            continue;
                        }
                        this.setHeightMap(x, z, ((chunkY << 4) | localY) + 1);
                        return;
                    }
                }

                this.setHeightmapValue(x, z, 0);
            }
        }
    }

    public boolean isSubChunksDirty() {
        return this.subChunksDirty;
    }

    public boolean isHeightmapOrBiomesDirty() {
        return this.heightmapOrBiomesDirty;
    }

    public void setHeightmapOrBiomesDirty() {
        this.heightmapOrBiomesDirty = true;
    }

    public boolean isBiomeStatesDirty() {
        return this.biomeStatesDirty;
    }

    public void setBiomeStatesDirty() {
        this.biomeStatesDirty = true;
    }

    public boolean isBordersDirty() {
        return this.bordersDirty;
    }

    public void setAllSubChunksDirty() {
        this.subChunksDirty = true;

        HeightRange heightRange = getHeightRange();
        for (int chunkY = heightRange.getMinChunkY(); chunkY < heightRange.getMaxChunkY(); chunkY++) {
            this.sections[Level.subChunkYtoIndex(chunkY)].setDirty();
        }
    }

    @Deprecated
    @Override
    public byte[] toBinary() {
        return new byte[0];
    }

    @Override
    public boolean compress() {
        boolean dirty = false;
        HeightRange heightRange = getHeightRange();
        for (int chunkY = heightRange.getMinChunkY(); chunkY < heightRange.getMaxChunkY(); chunkY++) {
            dirty |= this.sections[Level.subChunkYtoIndex(chunkY)].compress();
        }
        this.subChunksDirty |= dirty;
        return dirty;
    }

    public boolean compressBiomes() {
        boolean dirty = false;
        HeightRange heightRange = getHeightRange();
        int minChunkY = heightRange.getMinChunkY();
        int maxChunkY = heightRange.getMaxChunkY();

        biomeWriteLock.lock();
        try {
            for (int chunkY = minChunkY; chunkY < maxChunkY; chunkY++) {
                PalettedSubChunkStorage storage = this.biomes3d[Level.subChunkYtoIndex(chunkY)];
                if (storage == null) {
                    continue;
                }
                dirty |= storage.compress();
            }
        } finally {
            biomeWriteLock.unlock();
        }

        this.heightmapOrBiomesDirty |= dirty;
        return dirty;
    }

    @Override
    public void fixCorruptedBlockEntities() {
        super.fixCorruptedBlockEntities();
        this.setAllSubChunksDirty();
    }

    @Override
    public boolean fixInvalidBiome(boolean forceCompress) {
        boolean fixed = false;
        boolean dirty = false;
        HeightRange heightRange = getHeightRange();
        int minChunkY = heightRange.getMinChunkY();
        int maxChunkY = heightRange.getMaxChunkY();

        biomeWriteLock.lock();
        try {
            for (int chunkY = minChunkY; chunkY < maxChunkY; chunkY++) {
                PalettedSubChunkStorage storage = this.biomes3d[Level.subChunkYtoIndex(chunkY)];
                if (storage == null) {
                    continue;
                }
                if (storage.fixPaletteElements(Biomes::toValid)) {
                    fixed = true;
                    dirty = true;
                } else if (!forceCompress) {
                    continue;
                }
                dirty |= storage.compress();
            }
        } finally {
            biomeWriteLock.unlock();
        }

        this.heightmapOrBiomesDirty |= dirty;
        return fixed;
    }

    @Override
    public boolean isSectionEmpty(int chunkY) {
        return this.sections[Level.subChunkYtoIndex(chunkY)] instanceof EmptyChunkSection;
    }

    @Override
    public ChunkSection getSection(int chunkY) {
        return this.sections[Level.subChunkYtoIndex(chunkY)];
    }

    @Override
    public boolean setSection(int chunkY, ChunkSection section) {
        if (section.isEmpty()) {
            int index = Level.subChunkYtoIndex(chunkY);
            this.sections[index] = EmptyChunkSection.EMPTY[index];
        } else {
            this.sections[Level.subChunkYtoIndex(chunkY)] = section;
        }
        setChanged();
        return true;
    }

    @Override
    protected void setInternalSection(int chunkY, ChunkSection section) {
        this.sections[Level.subChunkYtoIndex(chunkY)] = section;

        ((LevelDbSubChunk) section).setParent(this);
        section.setDirty();
        this.subChunksDirty = true;

        this.setChanged();
    }

    @Override
    public BaseChunk clone() {
        LevelDbChunk chunk = (LevelDbChunk) super.clone();

        if (this.heightmap != null) {
            chunk.heightmap = this.heightmap.clone();
        }

        if (biomes3d != null) {
            HeightRange heightRange = getHeightRange();
            int minChunkHeight = heightRange.getMinChunkY();
            int maxChunkHeight = heightRange.getMaxChunkY();

            PalettedSubChunkStorage[] storages = new PalettedSubChunkStorage[biomes3d.length];
            chunk.biomes3d = storages;
            biomeReadLock.lock();
            try {
                for (int chunkY = minChunkHeight; chunkY < maxChunkHeight; chunkY++) {
                    int index = Level.subChunkYtoIndex(chunkY);
                    PalettedSubChunkStorage storage = biomes3d[index];
                    if (storage == null) {
                        continue;
                    }
                    storages[index] = storage.copy();
                }
            } finally {
                biomeReadLock.unlock();
            }
        }

        //TODO: lock

        return chunk;
    }

    @Override
    protected void cloneSections(BaseChunk newChunk) {
        if (sections == null) {
            return;
        }

        LevelDbChunk chunk = (LevelDbChunk) newChunk;
        ChunkSection[] newSections = new ChunkSection[sections.length];
        HeightRange heightRange = getHeightRange();
        for (int chunkY = heightRange.getMinChunkY(); chunkY < heightRange.getMaxChunkY(); chunkY++) {
            int index = Level.subChunkYtoIndex(chunkY);
            LevelDbSubChunk newSection = (LevelDbSubChunk) sections[index].copy();
            newSection.setParent(chunk);
            newSections[index] = newSection;
        }
        chunk.sections = newSections;
    }

    @Override
    public HeightRange getHeightRange() {
        HeightRange heightRange = this.heightRange;
        if (heightRange != null) {
            return heightRange;
        }
        LevelProvider provider = this.provider;
        if (provider != null) {
            return provider.getHeightRange();
        }
        return HeightRange.MINIMUM;
    }

    @SuppressWarnings("unused")
    public static LevelDbChunk getEmptyChunk(int chunkX, int chunkZ) {
        return getEmptyChunk(chunkX, chunkZ, null);
    }

    public static LevelDbChunk getEmptyChunk(int chunkX, int chunkZ, LevelProvider provider) {
        return new LevelDbChunk(provider, chunkX, chunkZ);
    }

    protected static int index2d(int x, int z) {
        int index = (z << 4) | x;
        if (index < 0 || index >= SUB_CHUNK_2D_SIZE) {
            throw new IllegalArgumentException("Invalid index: " + x + ", " + z );
        }
        return index;
    }
}
