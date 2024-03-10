package cn.nukkit.level.format;

import cn.nukkit.block.Block;
import cn.nukkit.level.GlobalBlockPaletteInterface.StaticVersion;
import cn.nukkit.utils.BinaryStream;

import java.util.function.IntFunction;

/**
 * Sub chunk.
 * @author MagicDroidX @ Nukkit Project
 */
public interface ChunkSection {
    int getY();

    int getBlockId(int layer, int x, int y, int z);

    void setBlockId(int layer, int x, int y, int z, int id);

    int getBlockData(int layer, int x, int y, int z);

    void setBlockData(int layer, int x, int y, int z, int data);

    int getFullBlock(int layer, int x, int y, int z);

    Block getAndSetBlock(int layer, int x, int y, int z, Block block);

    boolean setFullBlockId(int layer, int x, int y, int z, int fullId);

    default boolean setBlock(int layer, int x, int y, int z, int blockId) {
        return this.setBlock(layer, x, y, z, blockId, 0);
    }

    boolean setBlock(int layer, int x, int y, int z, int blockId, int meta);

    int getBlockSkyLight(int x, int y, int z);

    void setBlockSkyLight(int x, int y, int z, int level);

    int getBlockLight(int x, int y, int z);

    void setBlockLight(int x, int y, int z, int level);

    byte[] getIdArray();

    byte[] getDataArray();

    byte[] getSkyLightArray();

    byte[] getLightArray();

    boolean isEmpty();

    default boolean isEmpty(boolean fast) {
        return isEmpty();
    }

    boolean hasLayer(int layer);

    @Deprecated
    void writeToLegacy(BinaryStream stream);

    /**
     * Paletted sub chunk storage (static runtime).
     * @since 1.16.100
     */
    void writeTo(BinaryStream stream, StaticVersion version);

    /**
     * Paletted sub chunk storage (runtime).
     * @since 1.2.13
     */
    void writeTo(BinaryStream stream);

    /**
     * Paletted sub chunk storage (client blob cache).
     * @return all air
     * @since 1.12.0
     */
    boolean writeToCache(BinaryStream stream);

    /**
     * Paletted sub chunk storage (client blob cache).
     * @return all air
     * @since 1.12.0
     */
    default boolean writeToCache(BinaryStream stream, IntFunction<String> blockIdToName) {
        return writeToCache(stream);
    }

    /**
     * Paletted sub chunk storage.
     * @since 1.2.13
     */
    void writeToDisk(BinaryStream stream);

    default boolean compress() {
        return false;
    }

    boolean isDirty();

    void setDirty();

    ChunkSection copy();
}
