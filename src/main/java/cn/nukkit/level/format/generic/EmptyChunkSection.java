package cn.nukkit.level.format.generic;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.GlobalBlockPaletteInterface.StaticVersion;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.util.BitArrayVersion;
import cn.nukkit.level.util.PalettedSubChunkStorage;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.ChunkException;

import java.util.Arrays;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public final class EmptyChunkSection implements ChunkSection {
    public static final EmptyChunkSection[] EMPTY = new EmptyChunkSection[Level.MAX_SUB_CHUNK_COUNT];
    public static final PalettedSubChunkStorage EMPTY_STORAGE = PalettedSubChunkStorage.ofBlock(BitArrayVersion.V1);

    private static final byte[] EMPTY_2048 = new byte[2048];
    public static final byte[] EMPTY_BLOCK_ARR = new byte[4096];
    public static final byte[] EMPTY_META_ARR = EMPTY_2048;
    public static final short[] EMPTY_HEIGHTMAP_ARR = new short[256];
    public static final boolean[] EMPTY_BORDER_ARR = new boolean[256];
    public static final byte[] EMPTY_LIGHT_ARR = EMPTY_2048;
    public static final byte[] EMPTY_SKY_LIGHT_ARR = new byte[2048];

    static {
        for (int i = 0; i < EMPTY.length; i++) {
            EMPTY[i] = new EmptyChunkSection(Level.subChunkIndexToY(i));
        }

        Arrays.fill(EMPTY_SKY_LIGHT_ARR, (byte) 255);
    }

    private final int y;

    public EmptyChunkSection(int y) {
        this.y = y;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public int getBlockId(int layer, int x, int y, int z) {
        return BlockID.AIR;
    }

    @Override
    public int getFullBlock(int layer, int x, int y, int z) {
        return BlockID.AIR;
    }

    @Override
    public Block getAndSetBlock(int layer, int x, int y, int z, Block block) throws ChunkException {
        if (block.getId() != BlockID.AIR) throw new ChunkException("Attempted to modify an empty Chunk");
        return Block.get(BlockID.AIR);
    }

    @Override
    public boolean setBlock(int layer, int x, int y, int z, int blockId) throws ChunkException {
        if (blockId != BlockID.AIR) throw new ChunkException("Attempted to modify an empty Chunk");
        return false;
    }

    @Override
    public boolean setBlock(int layer, int x, int y, int z, int blockId, int meta) throws ChunkException {
        if (blockId != BlockID.AIR) throw new ChunkException("Attempted to modify an empty Chunk");
        return false;
    }

    @Override
    public byte[] getIdArray() {
        return EMPTY_BLOCK_ARR;
    }

    @Override
    public byte[] getDataArray() {
        return EMPTY_META_ARR;
    }

    @Override
    public byte[] getSkyLightArray() {
        return EMPTY_SKY_LIGHT_ARR;
    }

    @Override
    public byte[] getLightArray() {
        return EMPTY_LIGHT_ARR;
    }

    @Override
    public void setBlockId(int layer, int x, int y, int z, int id) throws ChunkException {
        if (id != BlockID.AIR) throw new ChunkException("Attempted to modify an empty Chunk");
    }

    @Override
    public int getBlockData(int layer, int x, int y, int z) {
        return 0;
    }

    @Override
    public void setBlockData(int layer, int x, int y, int z, int data) throws ChunkException {
        if (data != 0) throw new ChunkException("Attempted to modify an empty Chunk");
    }

    @Override
    public boolean setFullBlockId(int layer, int x, int y, int z, int fullId) throws ChunkException {
        if (fullId != BlockID.AIR) throw new ChunkException("Attempted to modify an empty Chunk");
        return false;
    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        return 0;
    }

    @Override
    public void setBlockLight(int x, int y, int z, int level) throws ChunkException {
        if (level != 0) throw new ChunkException("Attempted to modify an empty Chunk");
    }

    @Override
    public int getBlockSkyLight(int x, int y, int z) {
        return 15;
    }

    @Override
    public void setBlockSkyLight(int x, int y, int z, int level) throws ChunkException {
        if (level != 15) throw new ChunkException("Attempted to modify an empty Chunk");
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean hasLayer(int layer) {
        return false;
    }

    @Override
    public void writeToLegacy(BinaryStream stream) {
        stream.put(EMPTY_BLOCK_ARR);
        stream.put(EMPTY_META_ARR);
    }

    @Override
    public void writeTo(BinaryStream stream, StaticVersion version) {
        stream.putByte((byte) 8);
        stream.putByte((byte) 2);
        PalettedSubChunkStorage storage = PalettedSubChunkStorage.ofBlock(BitArrayVersion.V1, GlobalBlockPalette.getStaticBlockPalette(version)
                .getOrCreateRuntimeId0(Block.AIR, 0));
        storage.writeTo(stream);
        storage.writeTo(stream);
    }

    @Override
    public void writeTo(BinaryStream stream) {
        stream.putByte((byte) 8);
        stream.putByte((byte) 2);
        EMPTY_STORAGE.writeTo(stream);
        EMPTY_STORAGE.writeTo(stream);
    }

    @Override
    public boolean writeToCache(BinaryStream stream) {
        stream.putByte((byte) 8);
        stream.putByte((byte) 2);
        EMPTY_STORAGE.writeToCache(stream);
        EMPTY_STORAGE.writeToCache(stream);
        return true;
    }

    @Override
    public void writeToDisk(BinaryStream stream) {
        stream.putByte((byte) 8);
        stream.putByte((byte) 0);
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public void setDirty() {
    }

    @Override
    public EmptyChunkSection copy() {
        return this;
    }
}
