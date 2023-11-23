package cn.nukkit.level.format.leveldb;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.Blocks;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.GlobalBlockPaletteInterface.StaticVersion;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.anvil.util.NibbleArray;
import cn.nukkit.level.format.generic.EmptyChunkSection;
import cn.nukkit.level.util.BitArrayVersion;
import cn.nukkit.level.util.PalettedSubChunkStorage;
import cn.nukkit.utils.BinaryStream;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.IntFunction;

import static cn.nukkit.level.GlobalBlockPalette.*;
import static cn.nukkit.level.format.generic.EmptyChunkSection.*;

public class LevelDbSubChunk implements ChunkSection {
    protected static final PalettedSubChunkStorage[] EMPTY = new PalettedSubChunkStorage[0];

    private LevelDbChunk parent;

    protected final int y;
    protected PalettedSubChunkStorage[] storages;
    protected NibbleArray skyLight; //TODO: lighting
    protected NibbleArray blockLight; //TODO: lighting
    protected boolean dirty;

    protected ReadWriteLock lock = new ReentrantReadWriteLock();
    protected Lock readLock = lock.readLock();
    protected Lock writeLock = lock.writeLock();

    public LevelDbSubChunk(int y) {
        this(null, y);
    }

    public LevelDbSubChunk(LevelDbChunk parent, int y) {
        this.parent = parent;
        this.y = y;
        this.storages = EMPTY;
    }

    public LevelDbSubChunk(int y, @Nullable PalettedSubChunkStorage[] storages) {
        this(null, y, storages);
    }

    public LevelDbSubChunk(LevelDbChunk parent, int y, @Nullable PalettedSubChunkStorage[] storages) {
        this.parent = parent;
        this.y = y;

        if (storages == null || storages.length == 0) {
            this.storages = EMPTY;
            return;
        }

        int maxLayer = -1;
        for (int i = storages.length - 1; i >= 0; i--) {
            if (storages[i] != null) {
                if (maxLayer == -1) {
                    maxLayer = i;
                }
                continue;
            }

            if (maxLayer == -1) {
                continue;
            }

            storages[i] = PalettedSubChunkStorage.ofBlock();
        }

        if (maxLayer == -1) {
            this.storages = EMPTY;
            return;
        }

        int count = maxLayer + 1;
        if (count == storages.length) {
            this.storages = storages;
        }

        this.storages = Arrays.copyOf(this.storages, count);
    }

    @Nullable
    public LevelDbChunk getParent() {
        return parent;
    }

    public void setParent(LevelDbChunk parent) {
        this.parent = parent;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getBlockId(int layer, int x, int y, int z) {
        try {
            this.readLock.lock();

            if (!this.hasLayerUnsafe(layer)) {
                return BlockID.AIR;
            }

            return (this.storages[layer].get(x, y, z)) >> Block.BLOCK_META_BITS;
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public void setBlockId(int layer, int x, int y, int z, int id) {
        try {
            this.writeLock.lock();

            if (!this.hasLayerUnsafe(layer)) {
                if (id == BlockID.AIR) {
                    return;
                }

                this.createLayerUnsafe(layer);
            }

            PalettedSubChunkStorage storage = this.storages[layer];
            int previous = storage.get(x, y, z);
            int fullId = (id << Block.BLOCK_META_BITS) | (previous & Block.BLOCK_META_MASK);

            if (previous == fullId) {
                return;
            }

            storage.set(x, y, z, fullId);

            dirty = true;
            parent.onSubChunkBlockChanged(this, layer, x, y, z, previous, fullId);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public int getBlockData(int layer, int x, int y, int z) {
        try {
            this.readLock.lock();

            if (!this.hasLayerUnsafe(layer)) {
                return 0;
            }

            return (this.storages[layer].get(x, y, z)) & Block.BLOCK_META_MASK;
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public void setBlockData(int layer, int x, int y, int z, int data) {
        try {
            this.writeLock.lock();

            if (!this.hasLayerUnsafe(layer)) {
                return;
            }

            PalettedSubChunkStorage storage = this.storages[layer];
            int previous = storage.get(x, y, z);
            int fullId = (previous & Block.FULL_BLOCK_ID_MASK) | (data & Block.BLOCK_META_MASK);

            if (previous == fullId) {
                return;
            }

            storage.set(x, y, z, fullId);

            dirty = true;
            parent.onSubChunkBlockChanged(this, layer, x, y, z, previous, fullId);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public int getFullBlock(int layer, int x, int y, int z) {
        try {
            this.readLock.lock();

            if (!this.hasLayerUnsafe(layer)) {
                return BlockID.AIR;
            }

            return this.storages[layer].get(x, y, z);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public Block getAndSetBlock(int layer, int x, int y, int z, Block block) {
        int fullId;
        int previous;
        try {
            this.writeLock.lock();

            PalettedSubChunkStorage storage;
            if (!this.hasLayerUnsafe(layer)) {
                if (block.isAir()) {
                    return Blocks.air();
                }
                previous = BlockID.AIR;

                this.createLayerUnsafe(layer);

                storage = this.storages[layer];

                fullId = block.getFullId();
            } else {
                storage = this.storages[layer];
                previous = storage.get(x, y, z);

                fullId = block.getFullId();

                if (previous == fullId) {
                    return Block.fromFullId(previous);
                }
            }

            storage.set(x, y, z, fullId);

            dirty = true;
            parent.onSubChunkBlockChanged(this, layer, x, y, z, previous, fullId);
        } finally {
            this.writeLock.unlock();
        }
        return Block.fromFullId(previous);
    }

    @Override
    public boolean setFullBlockId(int layer, int x, int y, int z, int fullId) {
        try {
            this.writeLock.lock();

            PalettedSubChunkStorage storage;
            int previous;
            if (!this.hasLayerUnsafe(layer)) {
                if (fullId == BlockID.AIR) {
                    return false;
                }

                this.createLayerUnsafe(layer);

                storage = this.storages[layer];
                previous = BlockID.AIR;
            } else {
                storage = this.storages[layer];
                previous = storage.get(x, y, z);

                if (previous == fullId) {
                    return false;
                }
            }

            storage.set(x, y, z, fullId);

            dirty = true;
            parent.onSubChunkBlockChanged(this, layer, x, y, z, previous, fullId);
            return true;
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public boolean setBlock(int layer, int x, int y, int z, int blockId, int meta) {
        return setFullBlockId(layer, x, y, z, (blockId << Block.BLOCK_META_BITS) | (meta & Block.BLOCK_META_MASK));
    }

    @Override
    public int getBlockSkyLight(int x, int y, int z) {
        //TODO: lighting
        return this.parent != null && ((this.y << 4) | y) >= this.parent.getHighestBlockAt(x, z) ? 15 : 0;
    }

    @Override
    public void setBlockSkyLight(int x, int y, int z, int level) {
        //TODO: lighting
    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        //TODO: lighting
        return Block.light[this.getBlockId(0, x, y, z)];
    }

    @Override
    public void setBlockLight(int x, int y, int z, int level) {
        //TODO: lighting
    }

    @Deprecated
    @Override
    public byte[] getIdArray() {
        // no longer supported
        return EMPTY_BLOCK_ARR;
    }

    @Deprecated
    @Override
    public byte[] getDataArray() {
        // no longer supported
        return EMPTY_META_ARR;
    }

    @Override
    public byte[] getSkyLightArray() {
        //TODO: lighting
        return EMPTY_SKY_LIGHT_ARR;
    }

    @Override
    public byte[] getLightArray() {
        //TODO: lighting
        return EMPTY_LIGHT_ARR;
    }

    @Override
    public boolean isEmpty() {
        return this.isEmpty(true);
    }

    public boolean isEmpty(boolean fast) {
        try {
            this.readLock.lock();

            for (PalettedSubChunkStorage storage : this.storages) {
                if (storage == null) {
                    continue;
                }

                if (!storage.isEmpty(fast)) {
                    return false;
                }
            }

            return true;
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public boolean hasLayer(int layer) {
        try {
            this.readLock.lock();

            return this.hasLayerUnsafe(layer);
        } finally {
            this.readLock.unlock();
        }
    }

    protected boolean hasLayerUnsafe(int layer) {
        if (layer >= this.storages.length) {
            return false;
        }
        return this.storages[layer] != null;
    }

    protected void createLayer(int layer) {
        try {
            this.writeLock.lock();

            this.createLayerUnsafe(layer);
        } finally {
            this.writeLock.unlock();
        }
    }

    protected void createLayerUnsafe(int layer) {
        PalettedSubChunkStorage[] storages;
        if (this.storages.length <= layer) {
            storages = Arrays.copyOf(this.storages, layer + 1);
            this.storages = storages;
        } else {
            storages = this.storages;
        }

        for (int i = layer; i >= 0; i--) {
            if (storages[i] != null) {
                continue;
            }
            storages[i] = PalettedSubChunkStorage.ofBlock();
        }
    }

    @Deprecated
    @Override
    public void writeToLegacy(BinaryStream stream) {
        // no longer supported
        stream.put(EmptyChunkSection.EMPTY_BLOCK_ARR);
        stream.put(EmptyChunkSection.EMPTY_META_ARR);
    }

    @Override
    public void writeTo(BinaryStream stream, StaticVersion version) {
        try {
            this.readLock.lock();

            int layers = Math.max(2, this.storages.length);

            if (version.getProtocol() >= StaticVersion.V1_18.getProtocol()) {
                stream.putByte((byte) 9);
                stream.putByte((byte) layers);
                stream.putByte((byte) this.y);
            } else {
                stream.putByte((byte) 8);
                stream.putByte((byte) layers);
            }

            for (int i = 0; i < layers; i++) {
                if (!this.hasLayerUnsafe(i)) {
//                    EMPTY_STORAGE.writeTo(stream, (id) -> getStaticBlockPalette(version)
//                            .getOrCreateRuntimeId0(id >> Block.BLOCK_META_BITS, id & Block.BLOCK_META_MASK));
                    PalettedSubChunkStorage.ofBlock(BitArrayVersion.V1, GlobalBlockPalette.getStaticBlockPalette(version).getOrCreateRuntimeId0(Block.AIR, 0))
                            .writeTo(stream);
                    continue;
                }

                this.storages[i].writeTo(stream, (id) -> getStaticBlockPalette(version)
                        .getOrCreateRuntimeId0(id >> Block.BLOCK_META_BITS, id & Block.BLOCK_META_MASK));
            }
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public void writeTo(BinaryStream stream) {
        stream.putByte((byte) 8);
        try {
            this.readLock.lock();

            int layers = Math.max(2, this.storages.length);
            stream.putByte((byte) layers);

            for (int i = 0; i < layers; i++) {
                if (!this.hasLayerUnsafe(i)) {
                    EMPTY_STORAGE.writeTo(stream);
                    continue;
                }

                this.storages[i].writeTo(stream);
            }
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public boolean writeToCache(BinaryStream stream) {
        return writeToCache(stream, Blocks::getBlockFullNameById);
    }

    @Override
    public boolean writeToCache(BinaryStream stream, IntFunction<String> blockIdToName) {
        boolean hasBlock = false;
        stream.putByte((byte) 8);

        try {
            this.readLock.lock();

            int layers = Math.max(2, this.storages.length);
            stream.putByte((byte) layers);

            for (int i = 0; i < layers; i++) {
                if (!this.hasLayerUnsafe(i)) {
                    EMPTY_STORAGE.writeToCache(stream);
                    continue;
                }

                PalettedSubChunkStorage storage = this.storages[i];
                storage.writeToCache(stream, blockIdToName);

                if (!hasBlock) {
                    hasBlock = !storage.isEmpty(true);
                }
            }
        } finally {
            this.readLock.unlock();
        }

        return !hasBlock;
    }

    @Override
    public void writeToDisk(BinaryStream stream) {
//        stream.putByte((byte) 9);
        stream.putByte((byte) 8);

        try {
            this.readLock.lock();

            stream.putByte((byte) this.storages.length);
//            stream.putByte((byte) this.y);

            for (PalettedSubChunkStorage storage : this.storages) {
                if (storage != null) {
                    storage.writeToDisk(stream);
                    continue;
                }
                EMPTY_STORAGE.writeToDisk(stream);
            }
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public boolean compress() {
        try {
            this.writeLock.lock();

            if (this.isEmpty(false)) {
                return false;
            }

            boolean dirty = false;
            boolean checkRemove = true;
            for (int i = this.storages.length - 1; i >= 0; i--) {
                PalettedSubChunkStorage storage = this.storages[i];
                if (storage == null) {
                    continue;
                }
                dirty |= storage.compress();

                if (checkRemove) {
                    if (storage.isEmpty(true) && i > 0) {
                        this.storages = Arrays.copyOfRange(this.storages, 0, i);
                    } else {
                        checkRemove = false;
                    }
                }
            }
            this.dirty |= dirty;
            return dirty;
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public ChunkSection copy() {
        try {
            this.readLock.lock();

            int count = this.storages.length;
            PalettedSubChunkStorage[] storages = new PalettedSubChunkStorage[count];
            for (int i = 0; i < count; i++) {
                PalettedSubChunkStorage storage = this.storages[i];
                if (storage == null) {
                    continue;
                }
                storages[i] = storage.copy();
            }
            return new LevelDbSubChunk(null, this.y, storages);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public boolean isDirty() {
        return this.dirty;
    }

    @Override
    public void setDirty() {
        this.dirty = true;
    }
}
