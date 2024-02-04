package cn.nukkit.level.format.generic;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.level.format.Chunk;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.utils.ChunkException;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class BaseChunk extends BaseFullChunk implements Chunk {

    protected ChunkSection[] sections;

    @Override
    public BaseChunk clone() {
        BaseChunk chunk = (BaseChunk) super.clone();

        if (this.biomes != null) {
            chunk.biomes = this.biomes.clone();
        }

        if (this.heightMap != null) {
            chunk.heightMap = this.heightMap.clone();
        }

        cloneSections(chunk);

        return chunk;
    }

    protected void cloneSections(BaseChunk newChunk) {
        if (sections != null && sections[0] != null) {
            newChunk.sections = new ChunkSection[sections.length];
            for (int i = 0; i < sections.length; i++) {
                newChunk.sections[i] = sections[i].copy();
            }
        }
    }

    protected void removeInvalidTile(int x, int y, int z) {
        if (Server.getInstance().isPrimaryThread()) {
            removeInvalidBlockEntity(x, y, z);
        } else {
            Server.getInstance().getScheduler().scheduleTask(null, () -> removeInvalidBlockEntity(x, y, z));
        }
    }

    private void removeInvalidBlockEntity(int x, int y, int z) {
        BlockEntity entity = getTile(x, y, z);
        if (entity != null && !entity.isBlockEntityValid()) {
            removeBlockEntity(entity);
        }
    }

    @Override
    public int getFullBlock(int layer, int x, int y, int z) {
        return this.sections[y >> 4].getFullBlock(layer, x, y & 0x0f, z);
    }

    @Override
    public Block getAndSetBlock(int layer, int x, int y, int z, Block block) {
        int Y = y >> 4;
        try {
            setChanged();
            return this.sections[Y].getAndSetBlock(layer, x, y & 0x0f, z, block);
        } catch (ChunkException e) {
            this.setInternalSection(Y, getProviderHandle().getSubChunkFactory().create(Y));
            return this.sections[Y].getAndSetBlock(layer, x, y & 0x0f, z, block);
        } finally {
            removeInvalidTile(x, y, z);
        }
    }

    @Override
    public boolean setFullBlockId(int layer, int x, int y, int z, int fullId) {
        int Y = y >> 4;
        try {
            setChanged();
            return this.sections[Y].setFullBlockId(layer, x, y & 0x0f, z, fullId);
        } catch (ChunkException e) {
            this.setInternalSection(Y, getProviderHandle().getSubChunkFactory().create(Y));
            return this.sections[Y].setFullBlockId(layer, x, y & 0x0f, z, fullId);
        } finally {
            removeInvalidTile(x, y, z);
        }
    }

    @Override
    public boolean setBlock(int layer, int x, int y, int z, int blockId, int meta) {
        int Y = y >> 4;
        try {
            setChanged();
            return this.sections[Y].setBlock(layer, x, y & 0x0f, z, blockId, meta);
        } catch (ChunkException e) {
            this.setInternalSection(Y, getProviderHandle().getSubChunkFactory().create(Y));
            return this.sections[Y].setBlock(layer, x, y & 0x0f, z, blockId, meta);
        } finally {
            removeInvalidTile(x, y, z);
        }
    }

    @Override
    public void setBlockId(int layer, int x, int y, int z, int id) {
        int Y = y >> 4;
        try {
            this.sections[Y].setBlockId(layer, x, y & 0x0f, z, id);
            setChanged();
        } catch (ChunkException e) {
            this.setInternalSection(Y, getProviderHandle().getSubChunkFactory().create(Y));
            this.sections[Y].setBlockId(layer, x, y & 0x0f, z, id);
        } finally {
            removeInvalidTile(x, y, z);
        }
    }

    @Override
    public int getBlockId(int layer, int x, int y, int z) {
        return this.sections[y >> 4].getBlockId(layer, x, y & 0x0f, z);
    }

    @Override
    public int getBlockData(int layer, int x, int y, int z) {
        return this.sections[y >> 4].getBlockData(layer, x, y & 0x0f, z);
    }

    @Override
    public void setBlockData(int layer, int x, int y, int z, int data) {
        int Y = y >> 4;
        try {
            this.sections[Y].setBlockData(layer, x, y & 0x0f, z, data);
            setChanged();
        } catch (ChunkException e) {
            this.setInternalSection(Y, getProviderHandle().getSubChunkFactory().create(Y));
            this.sections[Y].setBlockData(layer, x, y & 0x0f, z, data);
        } finally {
            removeInvalidTile(x, y, z);
        }
    }

    @Override
    public int getBlockSkyLight(int x, int y, int z) {
        return this.sections[y >> 4].getBlockSkyLight(x, y & 0x0f, z);
    }

    @Override
    public void setBlockSkyLight(int x, int y, int z, int level) {
        int Y = y >> 4;
        try {
            this.sections[Y].setBlockSkyLight(x, y & 0x0f, z, level);
            setChanged();
        } catch (ChunkException e) {
            this.setInternalSection(Y, getProviderHandle().getSubChunkFactory().create(Y));
            this.sections[Y].setBlockSkyLight(x, y & 0x0f, z, level);
        }
    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        return this.sections[y >> 4].getBlockLight(x, y & 0x0f, z);
    }

    @Override
    public void setBlockLight(int x, int y, int z, int level) {
        int Y = y >> 4;
        try {
            this.sections[Y].setBlockLight(x, y & 0x0f, z, level);
            setChanged();
        } catch (ChunkException e) {
            this.setInternalSection(Y, getProviderHandle().getSubChunkFactory().create(Y));
            this.sections[Y].setBlockLight(x, y & 0x0f, z, level);
        }
    }

    @Override
    public boolean isSectionEmpty(int chunkY) {
        return this.sections[chunkY] instanceof EmptyChunkSection;
    }

    @Override
    public ChunkSection getSection(int chunkY) {
        return this.sections[chunkY];
    }

    @Override
    public boolean setSection(int chunkY, ChunkSection section) {
        if (Arrays.equals(EmptyChunkSection.EMPTY_BLOCK_ARR, section.getIdArray()) && Arrays.equals(EmptyChunkSection.EMPTY_META_ARR, section.getDataArray())) {
            this.sections[chunkY] = EmptyChunkSection.EMPTY[chunkY];
        } else {
            this.sections[chunkY] = section;
        }
        setChanged();
        return true;
    }

    protected void setInternalSection(int chunkY, ChunkSection section) {
        this.sections[chunkY] = section;
        setChanged();
    }

    @Override
    public byte[] getBlockIdArray() {
        ByteBuffer buffer = ByteBuffer.allocate(4096 * SECTION_COUNT);
        for (int y = 0; y < SECTION_COUNT; y++) {
            buffer.put(this.sections[y].getIdArray());
        }
        return buffer.array();
    }

    @Override
    public byte[] getBlockDataArray() {
        ByteBuffer buffer = ByteBuffer.allocate(2048 * SECTION_COUNT);
        for (int y = 0; y < SECTION_COUNT; y++) {
            buffer.put(this.sections[y].getDataArray());
        }
        return buffer.array();
    }

    @Override
    public byte[] getBlockSkyLightArray() {
        ByteBuffer buffer = ByteBuffer.allocate(2048 * SECTION_COUNT);
        for (int y = 0; y < SECTION_COUNT; y++) {
            buffer.put(this.sections[y].getSkyLightArray());
        }
        return buffer.array();
    }

    @Override
    public byte[] getBlockLightArray() {
        ByteBuffer buffer = ByteBuffer.allocate(2048 * SECTION_COUNT);
        for (int y = 0; y < SECTION_COUNT; y++) {
            buffer.put(this.sections[y].getLightArray());
        }
        return buffer.array();
    }

    @Override
    public ChunkSection[] getSections() {
        return sections;
    }

    @Override
    public boolean isEmpty() {
        for (ChunkSection section : this.sections) {
            if (section.isEmpty()) {
                return true;
            }
        }
        return false;
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
            if (getBlockId(0, x, y, z) != BlockID.AIR || getBlockId(1, x, y, z) != BlockID.AIR) {
                this.setHeightMap(x, z, y);
                return y;
            }
        }
        return 0;
    }
}
