package cn.nukkit.level.format.generic;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.HeightRange;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.util.PalettedSubChunkStorage;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.NumberTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.BlockUpdateEntry;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntMaps;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static cn.nukkit.level.format.generic.EmptyChunkSection.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@Log4j2
public abstract class BaseFullChunk implements FullChunk, ChunkManager {
    protected Long2ObjectMap<Entity> entities;

    protected Long2ObjectMap<BlockEntity> tiles;

    protected Int2ObjectMap<BlockEntity> tileList;

    /**
     * encoded as:
     *
     * (x &lt;&lt; 4) | z
     */
    protected byte[] biomes;

    protected byte[] blocks;

    protected byte[] data;

    protected byte[] skyLight;

    protected byte[] blockLight;

    protected byte[] heightMap;

    @Nullable
    protected List<CompoundTag> NBTtiles;

    @Nullable
    protected List<CompoundTag> NBTentities;

    @Nullable
    protected List<BlockUpdateEntry> blockUpdateEntries;

    protected Int2IntMap extraData;

    protected LevelProvider provider;

    private int x;
    private int z;
    private long hash;

    protected long changes;

    protected boolean isInit;

    protected transient ChunkCachedData cachedData;

    @Override
    public BaseFullChunk clone() {
        BaseFullChunk chunk;
        try {
            chunk = (BaseFullChunk) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }

        if (this.biomes != null) {
            chunk.biomes = this.biomes.clone();
        }

        if (this.blocks != null) {
            chunk.blocks = this.blocks.clone();
        }

        if (this.data != null) {
            chunk.data = this.data.clone();
        }

        if (this.skyLight != null) {
            chunk.skyLight = this.skyLight.clone();
        }

        if (this.blockLight != null) {
            chunk.blockLight = this.blockLight.clone();
        }

        if (this.heightMap != null) {
            chunk.heightMap = this.heightMap.clone();
        }

        return chunk;
    }

    public void setCachedData(ChunkCachedData cachedData) {
        this.cachedData = cachedData;
    }

    public ChunkCachedData getCachedData() {
        return cachedData;
    }

    @Override
    public void initChunk() {
        if (this.getProvider() != null && !this.isInit) {
            boolean changed = false;
            if (this.NBTentities != null) {
                for (CompoundTag nbt : NBTentities) {
                    if (!nbt.contains("id")) {
                        this.setChanged();
                        continue;
                    }
                    ListTag<? extends Tag> pos = nbt.getList("Pos");
                    if ((((NumberTag<?>) pos.get(0)).getData().intValue() >> 4) != this.getX() || ((((NumberTag<?>) pos.get(2)).getData().intValue() >> 4) != this.getZ())) {
                        changed = true;
                        continue;
                    }
                    Entity entity = Entity.createEntity(nbt.getString("id"), this, nbt);
                    if (entity != null) {
                        changed = true;
                    }
                }
                this.NBTentities = null;
            }

            if (this.NBTtiles != null) {
                for (CompoundTag nbt : NBTtiles) {
                    if (nbt != null) {
                        if (!nbt.contains("id")) {
                            changed = true;
                            continue;
                        }
                        if ((nbt.getInt("x") >> 4) != this.getX() || ((nbt.getInt("z") >> 4) != this.getZ())) {
                            changed = true;
                            continue;
                        }
                        BlockEntity blockEntity = BlockEntity.createBlockEntity(nbt.getString("id"), this, nbt);
                        if (blockEntity == null) {
                            changed = true;
                        }
                    }
                }
                this.NBTtiles = null;
            }

            this.setChanged(changed);

            this.isInit = true;
        }
    }

    @Override
    public final long getIndex() {
        return hash;
    }

    @Override
    public final int getX() {
        return x;
    }

    @Override
    public final int getZ() {
        return z;
    }

    @Override
    public void setPosition(int x, int z) {
        this.x = x;
        this.z = z;
        this.hash = Level.chunkHash(x, z);
    }

    public final void setX(int x) {
        this.x = x;
        this.hash = Level.chunkHash(x, getZ());
    }

    public final void setZ(int z) {
        this.z = z;
        this.hash = Level.chunkHash(getX(), z);
    }

    @Override
    public LevelProvider getProvider() {
        return provider;
    }

    @Override
    public void setProvider(LevelProvider provider) {
        this.provider = provider;
    }

    @Override
    public int getBiomeId(int x, int z) {
        return this.biomes[(x << 4) | z] & 0xFF;
    }

    @Override
    public void setBiomeId(int x, int z, int biomeId) {
        this.setChanged();
        this.biomes[(x << 4) | z] = (byte) biomeId;
    }

    @Override
    public void writeBiomeTo(BinaryStream stream, boolean network) {
        stream.put(biomes);
    }

    @Override
    public int getHeightMap(int x, int z) {
        return this.heightMap[(z << 4) | x] & 0xFF;
    }

    @Override
    public void setHeightMap(int x, int z, int value) {
        this.heightMap[(z << 4) | x] = (byte) value;
    }

    @Override
    public void recalculateHeightMap() {
        for (int z = 0; z < 16; ++z) {
            for (int x = 0; x < 16; ++x) {
                this.setHeightMap(x, z, this.getHighestBlockAt(x, z, false));
            }
        }
    }

    @Override
    public int getBlockExtraData(int layer, int x, int y, int z) {
        int index = Level.chunkBlockHash(x, y, z);
        if (this.extraData != null) {
            return this.extraData.get(index);
        }

        return 0;
    }

    @Override
    public void setBlockExtraData(int layer, int x, int y, int z, int data) {
        if (data == 0) {
            if (this.extraData != null) {
                this.extraData.remove(Level.chunkBlockHash(x, y, z));
            }
        } else {
            if (this.extraData == null) this.extraData = new Int2IntOpenHashMap();
            this.extraData.put(Level.chunkBlockHash(x, y, z), data);
        }

        this.setChanged(true);
    }

    @Override
    public void populateSkyLight() {
        for (int z = 0; z < 16; ++z) {
            for (int x = 0; x < 16; ++x) {
                int top = this.getHeightMap(x, z);
                for (int y = 255; y > top; --y) {
                    this.setBlockSkyLight(x, y, z, 15);
                }
                for (int y = top; y >= 0; --y) {
                    if (Block.solid[this.getBlockId(0, x, y, z)]) {
                        break;
                    }
                    this.setBlockSkyLight(x, y, z, 15);
                }
                this.setHeightMap(x, z, this.getHighestBlockAt(x, z, false));
            }
        }
    }

    @Override
    public int getHighestBlockAt(int x, int z) {
        return this.getHighestBlockAt(x, z, true);
    }

    @Override
    public int getHighestBlockAt(int x, int z, boolean cache) {
        if (cache) {
            int h = this.getHeightMap(x, z);
            if (h != 0 && h != 255) {
                return h;
            }
        }
        for (int y = 255; y >= 0; --y) {
            if (getBlockId(0, x, y, z) != BlockID.AIR) {
                this.setHeightMap(x, z, y);
                return y;
            }
        }
        return 0;
    }

    @Override
    public void addEntity(Entity entity) {
        if (this.entities == null) {
            this.entities = new Long2ObjectOpenHashMap<>();
        }
        this.entities.put(entity.getId(), entity);
        if (!(entity instanceof Player) && this.isInit) {
            this.setChanged();
        }
    }

    @Override
    public void removeEntity(Entity entity) {
        if (this.entities != null) {
            this.entities.remove(entity.getId());
            if (!(entity instanceof Player) && this.isInit) {
                this.setChanged();
            }
        }
    }

    @Override
    public void addBlockEntity(BlockEntity blockEntity) {
        if (this.tiles == null) {
            this.tiles = new Long2ObjectOpenHashMap<>();
            this.tileList = new Int2ObjectOpenHashMap<>();
        }
        this.tiles.put(blockEntity.getId(), blockEntity);
        int index = ((blockEntity.getFloorZ() & 0x0f) << 12) | ((blockEntity.getFloorX() & 0x0f) << 8) | (blockEntity.getFloorY() & 0xff);
        BlockEntity entity = this.tileList.get(index);
        if (entity != null && !entity.equals(blockEntity)) {
            this.tiles.remove(entity.getId());
            entity.close();
        }
        this.tileList.put(index, blockEntity);
        if (this.isInit) {
            this.setChanged();
        }
    }

    @Override
    public void removeBlockEntity(BlockEntity blockEntity) {
        if (this.tiles != null) {
            this.tiles.remove(blockEntity.getId());
            int index = ((blockEntity.getFloorZ() & 0x0f) << 12) | ((blockEntity.getFloorX() & 0x0f) << 8) | (blockEntity.getFloorY() & 0xff);
            this.tileList.remove(index);
            if (this.isInit) {
                this.setChanged();
            }
        }
    }

    @Override
    public Long2ObjectMap<Entity> getEntities() {
        return entities == null ? Long2ObjectMaps.emptyMap() : entities;
    }

    @Override
    public Long2ObjectMap<BlockEntity> getBlockEntities() {
        return tiles == null ? Long2ObjectMaps.emptyMap() : tiles;
    }

    @Override
    public Int2IntMap getBlockExtraDataArray() {
        return extraData == null ? Int2IntMaps.EMPTY_MAP : extraData;
    }

    @Override
    public BlockEntity getTile(int x, int y, int z) {
        return this.tileList != null ? this.tileList.get((z << 12) | (x << 8) | y) : null;
    }

    @Override
    public boolean isLoaded() {
        return this.getProvider() != null && this.getProvider().isChunkLoaded(this.getX(), this.getZ());
    }

    @Override
    public boolean load() throws IOException {
        return this.load(true);
    }

    @Override
    public boolean load(boolean generate) throws IOException {
        return this.getProvider() != null && this.getProvider().getChunk(this.getX(), this.getZ(), true) != null;
    }

    @Override
    public boolean unload() {
        return this.unload(true, true);
    }

    @Override
    public boolean unload(boolean save) {
        return this.unload(save, true);
    }

    @Override
    public boolean unload(boolean save, boolean safe) {
        LevelProvider level = this.getProvider();
        if (level == null) {
            return true;
        }
        if (save && this.changes != 0) {
            level.saveChunk(this.getX(), this.getZ());
        }
        if (safe) {
            for (Entity entity : this.getEntities().values()) {
                if (entity instanceof Player) {
                    return false;
                }
            }
        }
        for (Entity entity : new ObjectArrayList<>(this.getEntities().values())) {
            if (entity instanceof Player) {
                continue;
            }
            entity.close();
        }

        for (BlockEntity blockEntity : new ObjectArrayList<>(this.getBlockEntities().values())) {
            blockEntity.close();
        }
        this.provider = null;
        return true;
    }

    @Override
    public byte[] getBlockIdArray() {
        return this.blocks;
    }

    @Override
    public byte[] getBlockDataArray() {
        return this.data;
    }

    @Override
    public byte[] getBlockSkyLightArray() {
        return this.skyLight;
    }

    @Override
    public byte[] getBlockLightArray() {
        return this.blockLight;
    }

    @Override
    public byte[] getBiomeIdArray() {
        return this.biomes;
    }

    @Override
    public PalettedSubChunkStorage[] getBiomes() {
        return new PalettedSubChunkStorage[0];
    }

    @Override
    public byte[] getHeightMapArray() {
        return this.heightMap;
    }

    @Override
    public short[] getHeightmap() {
        return EMPTY_HEIGHTMAP_ARR;
    }

    public long getChanges() {
        return changes;
    }

    @Override
    public boolean hasChanged() {
        return this.changes != 0;
    }

    @Override
    public void setChanged() {
        this.changes++;
        cachedData = null;
    }

    @Override
    public void setChanged(boolean changed) {
        if (changed) {
            setChanged();
        } else {
            changes = 0;
        }
    }

    @Override
    public byte[] toFastBinary() {
        return this.toBinary();
    }

    @Override
    public boolean isLightPopulated() {
        return true;
    }

    @Override
    public void setLightPopulated() {
        this.setLightPopulated(true);
    }

    @Override
    public void setLightPopulated(boolean value) {

    }

    @Override
    public int getBlockIdAt(int layer, int x, int y, int z) {
        if (x >> 4 == getX() && z >> 4 == getZ()) {
            return getBlockId(0, x & 15, y, z & 15);
        }
        return BlockID.AIR;
    }

    @Override
    public void setBlockFullIdAt(int layer, int x, int y, int z, int fullId) {
        if (x >> 4 == getX() && z >> 4 == getZ()) {
            setFullBlockId(0, x & 15, y, z & 15, fullId);
        }
    }

    @Override
    public void setBlockIdAt(int layer, int x, int y, int z, int id) {
        if (x >> 4 == getX() && z >> 4 == getZ()) {
            setBlockId(0, x & 15, y, z & 15, id);
        }
    }

    @Override
    public void setBlockAt(int layer, int x, int y, int z, int id, int data) {
        if (x >> 4 == getX() && z >> 4 == getZ()) {
            setBlock(0, x & 15, y, z & 15, id, data);
        }
    }

    @Override
    public int getBlockDataAt(int layer, int x, int y, int z) {
        if (x >> 4 == getX() && z >> 4 == getZ()) {
            return getBlockIdAt(0, x & 15, y, z & 15);
        }
        return 0;
    }

    @Override
    public void setBlockDataAt(int layer, int x, int y, int z, int data) {
        if (x >> 4 == getX() && z >> 4 == getZ()) {
            setBlockData(0, x & 15, y, z & 15, data);
        }
    }

    @Override
    public BaseFullChunk getChunk(int chunkX, int chunkZ) {
        if (chunkX == getX() && chunkZ == getZ()) return this;
        return null;
    }

    @Override
    public void setChunk(int chunkX, int chunkZ) {
        setChunk(chunkX, chunkZ, null);
    }

    @Override
    public void setChunk(int chunkX, int chunkZ, BaseFullChunk chunk) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getSeed() {
        throw new UnsupportedOperationException("Chunk does not have a seed");
    }

    @Override
    public boolean compress() {
        return false;
    }

    @Override
    public void fixCorruptedBlockEntities() {
        Collection<BlockEntity> blockEntities = new ObjectArrayList<>(this.tiles.values());
        Iterator<BlockEntity> iter = blockEntities.iterator();
        while (iter.hasNext()) {
            BlockEntity blockEntity = iter.next();

            if (blockEntity.getChunkX() != this.getX() || blockEntity.getChunkZ() != this.getZ()) {
                blockEntity.close();
                iter.remove();
                log.debug("Removed an invalid (pos) BlockEntity: {} ({})", blockEntity, blockEntity.getSaveId());
                continue;
            }

            if (!blockEntity.isBlockEntityValid()) {
                blockEntity.close();
                iter.remove();
                log.debug("Removed an invalid (block) BlockEntity: {} ({})", blockEntity, blockEntity.getSaveId());
                continue;
            }

            for (BlockEntity other : blockEntities) {
                if (other == blockEntity
                        || other.getFloorX() != blockEntity.getFloorX()
                        || other.getFloorY() != blockEntity.getFloorY()
                        || other.getFloorZ() != blockEntity.getFloorZ()) {
                    continue;
                }

                blockEntity.close();
                iter.remove();
                log.debug("Removed an duplicate BlockEntity: {} ({})", blockEntity, blockEntity.getSaveId());
                break;
            }
        }

        HeightRange heightRange = getHeightRange();
        for (int y = heightRange.getMinY(); y < heightRange.getMaxY(); y++) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    int fullId = this.getFullBlock(0, x, y, z);
                    Block block = Block.fromFullId(fullId);
                    int type = block.getBlockEntityType();
                    if (type == 0) {
                        continue;
                    }

                    BlockEntity blockEntity = this.getTile(x, y, z);
                    if (blockEntity == null) {
                        String id = BlockEntities.getIdByType(type);
                        if (id == null) {
                            log.warn("Unregistered BlockEntity type: {} at {}, {}, {} ({}:{})", type, x, y, z, block.getId(), block.getDamage());
                            continue;
                        }

                        blockEntity = BlockEntity.createBlockEntity(id, this, BlockEntity.getDefaultCompound(x, y, z, id));

                        if (blockEntity == null) {
                            log.warn("Failed to create BlockEntity: {}, {}, {} ({}:{})", x, y, z, block.getId(), block.getDamage());
                            continue;
                        }

                        log.debug("Created a missing BlockEntity: {}, {}, {} ({})", x, y, z, blockEntity.getSaveId());
                    }
                }
            }
        }
    }

    /**
     * internal.
     */
    @Nullable
    public List<CompoundTag> getBlockEntityTags() {
        return NBTtiles;
    }

    /**
     * internal.
     */
    public void setBlockEntityTags(@Nullable List<CompoundTag> tags) {
        NBTtiles = tags;
    }

    /**
     * internal.
     */
    @Nullable
    public List<CompoundTag> getEntityTags() {
        return NBTentities;
    }

    /**
     * internal.
     */
    public void setEntityTags(@Nullable List<CompoundTag> tags) {
        NBTentities = tags;
    }

    /**
     * internal.
     */
    @Nullable
    public List<BlockUpdateEntry> getBlockUpdateEntries() {
        return blockUpdateEntries;
    }

    /**
     * internal.
     */
    public void setBlockUpdateEntries(@Nullable List<BlockUpdateEntry> entries) {
        blockUpdateEntries = entries;
    }
}
