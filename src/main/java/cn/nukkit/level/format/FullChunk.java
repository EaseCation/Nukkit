package cn.nukkit.level.format;

import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.format.LevelProviderManager.LevelProviderHandle;
import cn.nukkit.level.util.PalettedSubChunkStorage;
import com.google.common.annotations.Beta;
import it.unimi.dsi.fastutil.ints.Int2IntMap;

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

    int getHighestBlockAt(int x, int z);

    int getHighestBlockAt(int x, int z, boolean cache);

    int getHeightMap(int x, int z);

    void setHeightMap(int x, int z, int value);

    void recalculateHeightMap();

    void populateSkyLight();

    int getBiomeId(int x, int z);

    @Beta
    default int getBiomeId(int x, int y, int z) {
        return getBiomeId(x, z);
    }

    void setBiomeId(int x, int z, byte biomeId);

    @Beta
    default void setBiomeId(int x, int y, int z, byte biomeId) {
        this.setBiomeId(x, z, biomeId);
    }

    default void setBiomeId(int x, int z, int biomeId)  {
        setBiomeId(x, z, (byte) biomeId);
    }

    default void setBiome(int x, int z, Biome biome) {
        setBiomeId(x, z, (byte) biome.getId());
    }

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

    boolean load() throws IOException;

    boolean load(boolean generate) throws IOException;

    boolean unload() throws Exception;

    boolean unload(boolean save) throws Exception;

    boolean unload(boolean save, boolean safe) throws Exception;

    void initChunk();

    byte[] getBiomeIdArray();

    PalettedSubChunkStorage[] getBiomes();

    @Deprecated
    byte[] getHeightMapArray();

    short[] getHeightmap();

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

    int getMaxHeight();

    int getMinHeight();

    default void fixCorruptedBlockEntities() {
    }
}
