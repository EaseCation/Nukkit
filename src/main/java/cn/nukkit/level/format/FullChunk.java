package cn.nukkit.level.format;

import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.HeightRange;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.format.LevelProviderManager.LevelProviderHandle;
import cn.nukkit.level.util.PalettedSubChunkStorage;
import cn.nukkit.utils.BinaryStream;
import it.unimi.dsi.fastutil.ints.Int2ByteMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.IntList;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface FullChunk extends Cloneable {

    int getX();

    int getZ();

    default void setPosition(int x, int z) {
        setX(x);
        setZ(z);
    }

    void setX(int x);

    void setZ(int z);

    long getIndex();

    LevelProviderHandle getProviderHandle();

    LevelProvider getProvider();

    void setProvider(LevelProvider provider);

    int getFullBlock(int layer, int x, int y, int z);

    Block getAndSetBlock(int layer, int x, int y, int z, Block block);

    default boolean setFullBlockId(int layer, int x, int y, int z, int fullId) {
        return setBlock(layer, x, y, z, Block.getIdFromFullId(fullId), Block.getDamageFromFullId(fullId));
    }

    default boolean setBlock(int layer, int x, int y, int z, int blockId) {
        return setBlock(layer, x, y, z, blockId, 0);
    }

    boolean setBlock(int layer, int x, int y, int z, int  blockId, int meta);

    int getBlockId(int layer, int x, int y, int z);

    void setBlockId(int layer, int x, int y, int z, int id);

    int getBlockData(int layer, int x, int y, int z);

    void setBlockData(int layer, int x, int y, int z, int data);

    @Deprecated
    int getBlockExtraData(int layer, int x, int y, int z);

    @Deprecated
    void setBlockExtraData(int layer, int x, int y, int z, int data);

    int getBlockSkyLight(int x, int y, int z);

    void setBlockSkyLight(int x, int y, int z, int level);

    int getBlockLight(int x, int y, int z);

    void setBlockLight(int x, int y, int z, int level);

    default int getHighestBlockAt(int x, int z) {
        return getHighestBlockAt(x, z, true);
    }

    int getHighestBlockAt(int x, int z, boolean cache);

    int getHeightMap(int x, int z);

    void setHeightMap(int x, int z, int value);

    void recalculateHeightMap();

    void populateSkyLight();

    boolean hasBorder(int x, int z);

    int getBiomeId(int x, int z);

    default int getBiomeId(int x, int y, int z) {
        return getBiomeId(x, z);
    }

    void setBiomeId(int x, int z, int biomeId);

    default void setBiomeId(int x, int y, int z, int biomeId) {
        this.setBiomeId(x, z, biomeId);
    }

    default void setBiome(int x, int z, Biome biome) {
        setBiomeId(x, z, biome.getId());
    }

    default void fillBiome(int biomeId) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                setBiomeId(x, z, biomeId);
            }
        }
    }

    void writeBiomeTo(BinaryStream stream, boolean network, @Nullable IntList customBiomeIds);

    boolean isLightPopulated();

    void setLightPopulated();

    void setLightPopulated(boolean value);

    boolean isPopulated();

    void setPopulated();

    void setPopulated(boolean value);

    boolean isGenerated();

    void setGenerated();

    void setGenerated(boolean value);

    void addEntity(Entity entity);

    void removeEntity(Entity entity);

    void addBlockEntity(BlockEntity blockEntity);

    void removeBlockEntity(BlockEntity blockEntity);

    Map<Long, Entity> getEntities();

    Map<Long, BlockEntity> getBlockEntities();

    BlockEntity getTile(int x, int y, int z);

    boolean isLoaded();

    default boolean load() throws IOException {
        return load(true);
    }

    boolean load(boolean generate) throws IOException;

    default boolean unload() {
        return unload(true);
    }

    default boolean unload(boolean save) {
        return unload(save, true);
    }

    boolean unload(boolean save, boolean safe);

    void initChunk();

    byte[] getBiomeIdArray();

    PalettedSubChunkStorage[] getBiomes();

    Int2ByteMap getBiomeStates();

    @Deprecated
    byte[] getHeightMapArray();

    short[] getHeightmap();

    boolean[] getBorders();

    byte[] getBlockIdArray();

    byte[] getBlockDataArray();

    @Deprecated
    Int2IntMap getBlockExtraDataArray();

    byte[] getBlockSkyLightArray();

    byte[] getBlockLightArray();

    byte[] toBinary();

    @Deprecated
    byte[] toFastBinary();

    boolean hasChanged();

    void setChanged();

    void setChanged(boolean changed);

    boolean compress();

    default boolean isEmpty() {
        return false;
    }

    HeightRange getHeightRange();

    /**
     * Upgrade wall states to 1.16+ (connected walls)
     */
    default void fixWalls() {
        fixBlocks(true, false, false);
    }

    /**
     * Remove invalid extra blocks.
     */
    default void fixBlockLayers() {
        fixBlocks(false, true, false);
    }

    /**
     * Remove invalid block entities and create missing block entities.
     */
    default void fixCorruptedBlockEntities() {
        fixBlocks(false, false, true);
    }

    default void fixBlocks() {
        fixBlocks(true, true, true);
    }

    default void fixBlocks(boolean fixWalls, boolean fixBlockLayers, boolean fixBlockEntities) {
        fixBlocks(fixWalls, fixBlockLayers, fixBlockEntities, false, false, false, false);
    }

    /**
     * @param replaceInvisibleBedrock replace invisible bedrock with barrier
     */
    default void fixBlocks(boolean fixWalls, boolean fixBlockLayers, boolean fixBlockEntities, boolean emptyContainers, boolean persistentLeaves, boolean replaceInvisibleBedrock, boolean waxSigns) {
    }

    default boolean fixInvalidBiome() {
        return fixInvalidBiome(false);
    }

    default boolean fixInvalidBiome(boolean forceCompress) {
        return false;
    }
}
