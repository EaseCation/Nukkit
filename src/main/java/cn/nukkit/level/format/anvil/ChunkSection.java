package cn.nukkit.level.format.anvil;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.Blocks;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.GlobalBlockPaletteInterface.StaticVersion;
import cn.nukkit.level.format.anvil.util.BlockStorage;
import cn.nukkit.level.format.anvil.util.NibbleArray;
import cn.nukkit.level.format.generic.EmptyChunkSection;
import cn.nukkit.level.util.BitArrayVersion;
import cn.nukkit.level.util.PalettedSubChunkStorage;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.*;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.IntFunction;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@Log4j2
public class ChunkSection implements cn.nukkit.level.format.ChunkSection {

    private final int y;

    private final BlockStorage storage;

    protected byte[] blockLight;
    protected byte[] skyLight;
    protected byte[] compressedLight;
    protected boolean hasBlockLight;
    protected boolean hasSkyLight;

    private ChunkSection(int y, BlockStorage storage, byte[] blockLight, byte[] skyLight, byte[] compressedLight,
                         boolean hasBlockLight, boolean hasSkyLight) {
        this.y = y;
        this.storage = storage;
        this.blockLight = blockLight;
        this.skyLight = skyLight;
        this.compressedLight = compressedLight;
        this.hasBlockLight = hasBlockLight;
        this.hasSkyLight = hasSkyLight;
    }

    public ChunkSection(int y) {
        this.y = y;

        hasBlockLight = false;
        hasSkyLight = false;

        storage = new BlockStorage();
    }

    public ChunkSection(CompoundTag nbt) {
        this.y = nbt.getByte("Y");

        byte[] blocks = nbt.getByteArray("Blocks");
        NibbleArray data = new NibbleArray(nbt.getByteArray("Data"));

        storage = new BlockStorage();

        // Convert YZX to XZY
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 16; y++) {
                    int index = getAnvilIndex(x, y, z);
                    storage.setBlockId(x, y, z, blocks[index]);
                    storage.setBlockData(x, y, z, data.get(index));
                }
            }
        }

        this.blockLight = nbt.getByteArray("BlockLight");
        this.skyLight = nbt.getByteArray("SkyLight");
    }

    private static int getAnvilIndex(int x, int y, int z) {
        return (y << 8) + (z << 4) + x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getBlockId(int layer, int x, int y, int z) {
        synchronized (storage) {
            return storage.getBlockId(x, y, z);
        }
    }

    @Override
    public void setBlockId(int layer, int x, int y, int z, int id) {
        synchronized (storage) {
            storage.setBlockId(x, y, z, id);
        }
    }

    @Override
    public boolean setFullBlockId(int layer, int x, int y, int z, int fullId) {
        synchronized (storage) {
            storage.setFullBlock(x, y, z, (char) fullId);
        }
        return true;
    }

    @Override
    public int getBlockData(int layer, int x, int y, int z) {
        synchronized (storage) {
            return storage.getBlockData(x, y, z);
        }
    }

    @Override
    public void setBlockData(int layer, int x, int y, int z, int data) {
        synchronized (storage) {
            storage.setBlockData(x, y, z, data);
        }
    }

    @Override
    public int getFullBlock(int layer, int x, int y, int z) {
        synchronized (storage) {
            return storage.getFullBlock(x, y, z);
        }
    }

    @Override
    public boolean setBlock(int layer, int x, int y, int z, int blockId) {
        synchronized (storage) {
            return setBlock(0, x, y, z, blockId, 0);
        }
    }

    public Block getAndSetBlock(int layer, int x, int y, int z, Block block) {
        synchronized (storage) {
            int fullId = storage.getAndSetFullBlock(x, y, z, block.getFullId());
            return Block.fromFullId(fullId);
        }
    }

    @Override
    public boolean setBlock(int layer, int x, int y, int z, int blockId, int meta) {
        int newFullId = (blockId << 4) + meta;
        synchronized (storage) {
            int previousFullId = storage.getAndSetFullBlock(x, y, z, newFullId);
            return (newFullId != previousFullId);
        }
    }

    @Override
    public int getBlockSkyLight(int x, int y, int z) {
        if (this.skyLight == null) {
            if (!hasSkyLight) {
                return 0;
            } else if (compressedLight == null) {
                return 15;
            }
        }
        this.skyLight = getSkyLightArray();
        int sl = this.skyLight[(y << 7) | (z << 3) | (x >> 1)] & 0xff;
        if ((x & 1) == 0) {
            return sl & 0x0f;
        }
        return sl >> 4;
    }

    @Override
    public void setBlockSkyLight(int x, int y, int z, int level) {
        if (this.skyLight == null) {
            if (hasSkyLight && compressedLight != null) {
                this.skyLight = getSkyLightArray();
            } else if (level == (hasSkyLight ? 15 : 0)) {
                return;
            } else {
                this.skyLight = new byte[2048];
                if (hasSkyLight) {
                    Arrays.fill(this.skyLight, (byte) 0xFF);
                }
            }
        }
        int i = (y << 7) | (z << 3) | (x >> 1);
        int old = this.skyLight[i] & 0xff;
        if ((x & 1) == 0) {
            this.skyLight[i] = (byte) ((old & 0xf0) | (level & 0x0f));
        } else {
            this.skyLight[i] = (byte) (((level & 0x0f) << 4) | (old & 0x0f));
        }
    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        if (blockLight == null && !hasBlockLight) return 0;
        this.blockLight = getLightArray();
        int l = blockLight[(y << 7) | (z << 3) | (x >> 1)] & 0xff;
        if ((x & 1) == 0) {
            return l & 0x0f;
        }
        return l >> 4;
    }

    @Override
    public void setBlockLight(int x, int y, int z, int level) {
        if (this.blockLight == null) {
            if (hasBlockLight) {
                this.blockLight = getLightArray();
            } else if (level == 0) {
                return;
            } else {
                this.blockLight = new byte[2048];
            }
        }
        int i = (y << 7) | (z << 3) | (x >> 1);
        int old = this.blockLight[i] & 0xff;
        if ((x & 1) == 0) {
            this.blockLight[i] = (byte) ((old & 0xf0) | (level & 0x0f));
        } else {
            this.blockLight[i] = (byte) (((level & 0x0f) << 4) | (old & 0x0f));
        }
    }

    @Override
    public byte[] getIdArray() {
        synchronized (storage) {
            byte[] anvil = new byte[4096];
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < 16; y++) {
                        int index = getAnvilIndex(x, y, z);
                        anvil[index] = (byte) storage.getBlockId(x, y, z);
                    }
                }
            }
            return anvil;
        }
    }

    @Override
    public byte[] getDataArray() {
        synchronized (storage) {
            NibbleArray anvil = new NibbleArray(4096);
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < 16; y++) {
                        int index = getAnvilIndex(x, y, z);
                        anvil.set(index, (byte) storage.getBlockData(x, y, z));
                    }
                }
            }
            return anvil.getData();
        }
    }

    @Override
    public byte[] getSkyLightArray() {
        if (this.skyLight != null) return skyLight;
        if (hasSkyLight) {
            if (compressedLight != null) {
                inflate();
                return this.skyLight;
            }
            return EmptyChunkSection.EMPTY_SKY_LIGHT_ARR;
        } else {
            return EmptyChunkSection.EMPTY_LIGHT_ARR;
        }
    }

    private void inflate() {
        try {
            if (compressedLight != null && compressedLight.length != 0) {
                byte[] inflated = Zlib.inflate(compressedLight);
                blockLight = Arrays.copyOfRange(inflated, 0, 2048);
                if (inflated.length > 2048) {
                    skyLight = Arrays.copyOfRange(inflated, 2048, 4096);
                } else {
                    skyLight = new byte[2048];
                    if (hasSkyLight) {
                        Arrays.fill(skyLight, (byte) 0xFF);
                    }
                }
                compressedLight = null;
            } else {
                blockLight = new byte[2048];
                skyLight = new byte[2048];
                if (hasSkyLight) {
                    Arrays.fill(skyLight, (byte) 0xFF);
                }
            }
        } catch (IOException e) {
            Server.getInstance().getLogger().logException(e);
        }
    }

    @Override
    public byte[] getLightArray() {
        if (this.blockLight != null) return blockLight;
        if (hasBlockLight) {
            inflate();
            return this.blockLight;
        } else {
            return EmptyChunkSection.EMPTY_LIGHT_ARR;
        }
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean hasLayer(int layer) {
        return layer == 0;
    }

    @Override
    public void writeToLegacy(BinaryStream stream) {
        synchronized (storage) {
            byte[] ids = storage.getBlockIds();
            byte[] data = storage.getBlockData();
            byte[] merged = new byte[ids.length + data.length];

            System.arraycopy(ids, 0, merged, 0, ids.length);
            System.arraycopy(data, 0, merged, ids.length, data.length);
            stream.put(merged);
        }
    }

    @Override
    public void writeTo(BinaryStream stream, StaticVersion version) {
        if (version.getProtocol() >= StaticVersion.V1_18.getProtocol()) {
            stream.putByte((byte) 9);
            stream.putByte((byte) 2);
            stream.putByte((byte) this.y);
        } else {
            stream.putByte((byte) 8);
            stream.putByte((byte) 2);
        }
        synchronized (storage) {
            this.storage.writeTo(stream, version);
        }
        PalettedSubChunkStorage.ofBlock(BitArrayVersion.V1, GlobalBlockPalette.getStaticBlockPalette(version).getOrCreateRuntimeId0(Block.AIR, 0))
                .writeTo(stream);
    }

    @Override
    public void writeTo(BinaryStream stream) {
        stream.putByte((byte) 8);
        stream.putByte((byte) 2);
        synchronized (storage) {
            this.storage.writeTo(stream);
        }
        EmptyChunkSection.EMPTY_STORAGE.writeTo(stream);
    }

    @Override
    public boolean writeToCache(BinaryStream stream) {
        return writeToCache(stream, Blocks::getBlockFullNameById);
    }

    @Override
    public boolean writeToCache(BinaryStream stream, IntFunction<String> blockIdToName) {
        stream.putByte((byte) 8);
        stream.putByte((byte) 2);
        boolean allAir;
        synchronized (storage) {
            allAir = this.storage.writeToCache(stream, blockIdToName);
        }
        EmptyChunkSection.EMPTY_STORAGE.writeToCache(stream);
        return allAir;
    }

    @Override
    public void writeToDisk(BinaryStream stream) {
        stream.putByte((byte) 8);
        stream.putByte((byte) 1);
        synchronized (this.storage) {
            this.storage.writeToDisk(stream);
        }
    }

    @Override
    public boolean compress() {
        if (blockLight != null) {
            byte[] arr1 = blockLight;
            hasBlockLight = !Utils.isByteArrayEmpty(arr1);
            byte[] arr2;
            if (skyLight != null) {
                arr2 = skyLight;
                hasSkyLight = !Utils.isByteArrayEmpty(arr2);
            } else if (hasSkyLight) {
                arr2 = EmptyChunkSection.EMPTY_SKY_LIGHT_ARR;
            } else {
                arr2 = EmptyChunkSection.EMPTY_LIGHT_ARR;
                //hasSkyLight = false;
            }
            blockLight = null;
            skyLight = null;
            byte[] toDeflate = null;
            if (hasBlockLight && hasSkyLight && arr2 != EmptyChunkSection.EMPTY_SKY_LIGHT_ARR) {
                toDeflate = Binary.appendBytes(arr1, arr2);
            } else if (hasBlockLight) {
                toDeflate = arr1;
            }
            if (toDeflate != null) {
                try {
                    compressedLight = Zlib.deflate(toDeflate, 1);
                } catch (Exception e) {
                    Server.getInstance().getLogger().logException(e);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    @Override
    public void setDirty() {
    }

    @Override
    public ChunkSection copy() {
        return new ChunkSection(
                this.y,
                this.storage.copy(),
                this.blockLight == null ? null : this.blockLight.clone(),
                this.skyLight == null ? null : this.skyLight.clone(),
                this.compressedLight == null ? null : this.compressedLight.clone(),
                this.hasBlockLight,
                this.hasSkyLight
        );
    }
}
