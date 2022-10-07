package cn.nukkit.level.format.leveldb;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.generic.BaseChunk;
import cn.nukkit.level.util.PalettedSubChunkStorage;
import cn.nukkit.nbt.tag.CompoundTag;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static cn.nukkit.level.format.leveldb.LevelDbConstants.*;

public class LevelDbChunk extends BaseChunk {
    protected PalettedSubChunkStorage[] biomes3d; //TODO
    /**
     * ZZZZXXXX key bit order.
     */
    protected short[] heightmap; // air Y

    protected boolean terrainGenerated;
    protected boolean terrainPopulated;

    protected boolean subChunksDirty;
    protected boolean heightmapOrBiomesDirty;
    //TODO: more DirtyTicksCounter
//    protected boolean blockTickingDirty;
//    protected boolean randomBlockTickingDirty;
//    protected boolean blockEntitiesDirty;
//    protected boolean entitiesDirty;

    protected final Lock ioLock;

    public LevelDbChunk(@Nullable LevelProvider level, int chunkX, int chunkZ) {
        this(level, chunkX, chunkZ, null, null, null, null, null, null);
    }

    public LevelDbChunk(@Nullable LevelProvider level, int chunkX, int chunkZ, @Nullable LevelDbSubChunk[] sections,
                        @Nullable short[] heightmap, @Nullable byte[] biomes2d, @Nullable PalettedSubChunkStorage[] biomes3d,
                        @Nullable List<CompoundTag> entities, @Nullable List<CompoundTag> blockEntities) {
        this.ioLock = new ReentrantLock();
        this.provider = level;
        this.providerClass = level != null ? level.getClass() : LevelDB.class;
        this.setPosition(chunkX, chunkZ);

        if (sections != null) {
            if (sections.length == SECTION_COUNT) {
                this.sections = sections;
            } else {
                this.sections = Arrays.copyOf(sections, SECTION_COUNT);
            }
        } else {
            this.sections = new LevelDbSubChunk[SECTION_COUNT];
        }
        for (int i = 0; i < SECTION_COUNT; i++) {
            ChunkSection section = this.sections[i];
            if (section == null) {
                section = new LevelDbSubChunk(this, i);
                this.sections[i] = section;
            }
            ((LevelDbSubChunk) section).setParent(this);
        }

        if (heightmap != null && heightmap.length == SUB_CHUNK_2D_SIZE) {
            this.heightmap = heightmap;
        } else {
            this.heightmap = new short[SUB_CHUNK_2D_SIZE];
            this.recalculateHeightMap();
        }

        if (biomes2d != null && biomes2d.length == SUB_CHUNK_2D_SIZE) {
            this.biomes = biomes2d;
        } else {
            this.biomes = new byte[SUB_CHUNK_2D_SIZE];
        }
        this.biomes3d = biomes3d;

        this.NBTentities = entities;
        this.NBTtiles = blockEntities;
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
    public short[] getHeightmap() {
        return this.heightmap;
    }

    @Override
    public int getBiomeId(int x, int z) {
        return this.biomes[index2d(x, z)] & 0xff;
//        return this.getBiomeId(x, 0, z);
    }

    @Override
    public int getBiomeId(int x, int y, int z) {
        return this.biomes[index2d(x, z)] & 0xff;
//        return this.biomes3d[0].get(x, 0, z);
    }

    @Override
    public void setBiomeId(int x, int z, byte biomeId) {
//        this.biomes[index2d(x, z)] = biomeId;
        this.setBiomeId(x, 0, z, biomeId);
    }

    @Override
    public void setBiomeId(int x, int y, int z, byte biomeId) {
        int index = index2d(x, z);
        if (this.biomes[index] == biomeId) {
            return;
        }
        this.biomes[index] = biomeId;
//        this.biomes3d[0].set(x, 0, z, biomeId);

        this.heightmapOrBiomesDirty = true;
        this.setChanged();
    }

    @Override
    public int getBlockSkyLight(int x, int y, int z) {
        return this.getHighestBlockAt(x, z) >= y ? 15 : 0;
    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        return Block.light[this.getBlockId(0, x, y, z)];
    }

    @Override
    public int getHighestBlockAt(int x, int z, boolean cache) {
        if (cache) {
            int airY = this.getHeightMap(x, z);
            return airY > 0 ? airY - 1 : 0;
        }

        for (int chunkY = 15; chunkY >= 0; chunkY--) {
            LevelDbSubChunk subChunk = this.getSection(chunkY);
            for (int localY = 15; localY >= 0; localY--) {
                if (subChunk.getBlockId(0, x, localY, z) == BlockID.AIR && subChunk.getBlockId(1, x, localY, z) == BlockID.AIR) {
                    continue;
                }
                int y = (chunkY << 4) | localY;
                this.setHeightMap(x, z, y + 1);
                return y;
            }
        }
        this.setHeightMap(x, z, 0);
        return 0;
    }

    @Override
    public int getHeightMap(int x, int z) {
        return this.heightmap[index2d(x, z)];
    }

    @Override
    public void setHeightMap(int x, int z, int value) {
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

    protected void onSubChunkBlockChanged(LevelDbSubChunk subChunk, int layer, int x, int y, int z, int previousId, int newId) {
        subChunksDirty = true;

        if (previousId == BlockID.AIR) {
            int height = getHeightMap(x, z);
            int worldY = (subChunk.getY() << 4) | y;
            if (height <= worldY) {
                setHeightMap(x, z, worldY + 1);
            }
        } else if (newId == BlockID.AIR) {
            int subChunkY = subChunk.getY();
            int worldY = (subChunkY << 4) | y;
            int height = getHeightMap(x, z);
            if (height == worldY) {
                boolean hasColumn = false;
                for (int localY = y; localY >= 0; localY--) {
                    if (subChunk.getBlockId(0, x, localY, z) == BlockID.AIR && subChunk.getBlockId(1, x, localY, z) == BlockID.AIR) {
                        continue;
                    }
                    this.setHeightMap(x, z, ((subChunkY << 4) | localY) + 1);
                    hasColumn = true;
                    break;
                }

                // thread safe?
                SUB_CHUNKS:
                for (int chunkY = subChunkY - 1; chunkY >= 0; chunkY--) {
                    LevelDbSubChunk section = this.getSection(chunkY);
                    for (int localY = 15; localY >= 0; localY--) {
                        if (section.getBlockId(0, x, localY, z) == BlockID.AIR && section.getBlockId(1, x, localY, z) == BlockID.AIR) {
                            continue;
                        }
                        this.setHeightMap(x, z, ((chunkY << 4) | localY) + 1);
                        hasColumn = true;
                        break SUB_CHUNKS;
                    }
                }

                if (!hasColumn) {
                    this.setHeightMap(x, z, 0);
                }
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

    public void setAllSubChunksDirty() {
        this.subChunksDirty = true;
        for (int i = 0; i < SECTION_COUNT; i++) {
            this.sections[i].setDirty();
        }
    }

    @Deprecated
    @Override
    public byte[] toBinary() {
        return new byte[0];
    }

    @Override
    public boolean compress() {
        super.compress();

        boolean dirty = false;
        for (int i = 0; i < SECTION_COUNT; i++) {
            dirty |= this.sections[i].compress();
        }
        this.subChunksDirty |= dirty;
        return dirty;
    }

    @Override
    public void fixCorruptedBlockEntities() {
        super.fixCorruptedBlockEntities();
        this.setAllSubChunksDirty();
    }

    @Override
    public LevelDbSubChunk getSection(float y) {
        return (LevelDbSubChunk) this.sections[(int) y];
    }

    @Override
    protected void setInternalSection(float y, ChunkSection section) {
        this.sections[(int) y] = section;

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

        for (int i = 0; i < chunk.sections.length; i++) {
            ChunkSection section = chunk.sections[i];
            if (section == null) {
                continue;
            }
            ((LevelDbSubChunk) section).setParent(chunk);
        }

        return chunk;
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
