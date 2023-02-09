package cn.nukkit.level.util;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockSerializer;
import cn.nukkit.block.BlockUpgrader;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.ChunkException;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.List;
import java.util.function.IntFunction;

import static cn.nukkit.SharedConstants.*;
import static cn.nukkit.level.format.leveldb.LevelDbConstants.*;

@Log4j2
public class PalettedSubChunkStorage {
    private static final CompoundTag UNKNOWN_BLOCK_TAG = new CompoundTag()
            .putString("name", "minecraft:info_update")
            .putShort("val", 0);

    private static final int SIZE = 16 * 16 * 16;

    private IntList palette;
    private BitArray bitArray;

    private PalettedSubChunkStorage(BitArrayVersion version, int firstId) {
        this.bitArray = version.createPalette(SIZE);
        if (version == BitArrayVersion.EMPTY) {
            this.palette = IntLists.EMPTY_LIST;
            return;
        }
        this.palette = new IntArrayList(version.isSingleton() ? 1 : IntArrayList.DEFAULT_INITIAL_CAPACITY);
        this.palette.add(firstId); // Air is at the start of every block palette.
    }

    private PalettedSubChunkStorage(BitArray bitArray, IntList palette) {
        this.palette = palette;
        this.bitArray = bitArray;
    }

    public static PalettedSubChunkStorage ofBlock() {
        return new PalettedSubChunkStorage(BitArrayVersion.V2, Block.AIR);
    }

    public static PalettedSubChunkStorage ofBlock(BitArrayVersion version) {
        return new PalettedSubChunkStorage(version, Block.AIR);
    }

    public static PalettedSubChunkStorage ofBlock(int airBlockId) {
        return new PalettedSubChunkStorage(BitArrayVersion.V2, airBlockId);
    }

    public static PalettedSubChunkStorage ofBlock(BitArrayVersion version, int airBlockId) {
        return new PalettedSubChunkStorage(version, airBlockId);
    }

    @Nullable
    public static PalettedSubChunkStorage ofBlock(BinaryStream stream) {
        byte header = stream.getSingedByte();

        if (header == -1) {
            return null;
        }

        BitArrayVersion version = BitArrayVersion.get(header >> 1, true);
        int expectedWordSize = version.getWordsForSize(SUB_CHUNK_SIZE);
        int[] words = new int[expectedWordSize];
        for (int i = 0; i < expectedWordSize; ++i) {
            words[i] = stream.getLInt();
        }
        BitArray bitArray = version.createPalette(SUB_CHUNK_SIZE, words);

        int paletteSize = stream.getLInt();
        int[] palette = new int[paletteSize];
        ByteArrayInputStream bais = new ByteArrayInputStream(stream.getBufferUnsafe());
        bais.skip(stream.getOffset());
        for (int i = 0; i < paletteSize; ++i) {
            CompoundTag tag;
            try {
                tag = NBTIO.read(bais, ByteOrder.LITTLE_ENDIAN, false);
            } catch (IOException e) {
                throw new ChunkException("Invalid blockstate NBT at offset " + i + " in paletted storage", e);
            }

            if (ENABLE_BLOCK_STATE_PERSISTENCE) {
                BlockUpgrader.upgrade(tag);
                palette[i] = BlockSerializer.deserializeRuntime(tag);
                continue;
            }

            int meta;
            String name = tag.getString("name");
            int id = GlobalBlockPalette.getBlockIdByName(name);
            if (id == -1) {
                id = Block.INFO_UPDATE;
                meta = 0;
                log.warn("Unmapped block name: {}", name);
            } else {
                meta = tag.getShort("val");
            }
            palette[i] = (id << Block.BLOCK_META_BITS) | (meta & Block.BLOCK_META_MASK);
        }
        stream.setOffset(stream.getCount() - bais.available());

        return new PalettedSubChunkStorage(bitArray, IntArrayList.wrap(palette));
    }

    public static PalettedSubChunkStorage ofBiome(int biomeId) {
        return new PalettedSubChunkStorage(BitArrayVersion.V0, biomeId);
    }

    public static PalettedSubChunkStorage ofBiome(BitArrayVersion version, int biomeId) {
        return new PalettedSubChunkStorage(version, biomeId);
    }

    @Nullable
    public static PalettedSubChunkStorage ofBiome(BinaryStream stream) {
        byte header = stream.getSingedByte();

        if (header == -1) {
            return null;
        }

        BitArrayVersion version = BitArrayVersion.get(header >> 1, true);
        int expectedWordSize = version.getWordsForSize(SUB_CHUNK_SIZE);
        int[] words = new int[expectedWordSize];
        for (int i = 0; i < expectedWordSize; ++i) {
            words[i] = stream.getLInt();
        }
        BitArray bitArray = version.createPalette(SUB_CHUNK_SIZE, words);

        int paletteSize = stream.getLInt();
        int[] palette = new int[paletteSize];
        for (int i = 0; i < paletteSize; ++i) {
            palette[i] = stream.getLInt();
        }

        return new PalettedSubChunkStorage(bitArray, IntArrayList.wrap(palette));
    }

    private static int getPaletteHeader(BitArrayVersion version, boolean runtime) {
        return (version.getId() << 1) | (runtime ? 1 : 0);
    }

    public int get(int index) {
        return this.palette.getInt(this.bitArray.get(index));
    }

    public int get(int x, int y, int z) {
        return this.get(elementIndex(x, y, z));
    }

    public int get(BlockVector3 pos) {
        return this.get(elementIndex(pos.x, pos.y, pos.z));
    }

    public void set(int index, int value) {
        try {
            int paletteIndex = this.getOrAdd(value);
            this.bitArray.set(index, paletteIndex);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unable to set value: " + value + ", palette: " + palette, e);
        }
    }

    public void set(int x, int y, int z, int value) {
        this.set(elementIndex(x, y, z), value);
    }

    public void set(BlockVector3 pos, int value) {
        this.set(elementIndex(pos.x, pos.y, pos.z), value);
    }

    /**
     * Fast check.
     */
    public boolean has(int id) {
        return this.palette.contains(id);
    }

    public int indexOf(int id) {
        return this.palette.indexOf(id);
    }

    public void writeTo(BinaryStream stream) {
        this.writeTo(stream, null);
    }

    public void writeTo(BinaryStream stream, Int2IntFunction idConvert) {
        BitArrayVersion version = bitArray.getVersion();
        stream.putByte((byte) getPaletteHeader(version, true));
        if (version == BitArrayVersion.EMPTY) {
            return;
        }

        if (version != BitArrayVersion.V0) {
            for (int word : bitArray.getWords()) {
                stream.putLInt(word);
            }

            stream.putVarInt(palette.size());
        }

        for (int i = 0; i < this.palette.size(); i++) {
            int id = this.palette.getInt(i);
            if (idConvert != null) {
                id = idConvert.applyAsInt(id);
            }
            stream.putVarInt(id);
        }
    }

    public void writeToCache(BinaryStream stream) {
        writeToCache(stream, GlobalBlockPalette::getNameByBlockId);
    }

    public void writeToCache(BinaryStream stream, IntFunction<String> blockIdToName) {
        if (blockIdToName == null) {
            blockIdToName = GlobalBlockPalette::getNameByBlockId;
        }

        BitArrayVersion version = bitArray.getVersion();
        stream.putByte((byte) getPaletteHeader(version, false));
        if (version == BitArrayVersion.EMPTY) {
            return;
        }

        if (version != BitArrayVersion.V0) {
            for (int word : bitArray.getWords()) {
                stream.putLInt(word);
            }

            stream.putVarInt(palette.size());
        }

        List<CompoundTag> tagList = new ObjectArrayList<>();
        for (int i = 0; i < this.palette.size(); i++) {
            int fullId = this.palette.getInt(i);
            if (ENABLE_BLOCK_STATE_PERSISTENCE) {
                tagList.add(BlockSerializer.serializeRuntime(fullId));
                continue;
            }

            int id = fullId >> Block.BLOCK_META_BITS;
            String name = blockIdToName.apply(id);
            if (name == null) {
                tagList.add(UNKNOWN_BLOCK_TAG);
                log.warn("Unmapped block ID: {}", id);
                continue;
            }
            tagList.add(new CompoundTag()
                    .putString("name", name)
                    .putShort("val", fullId & Block.BLOCK_META_MASK));
        }
        try {
            stream.put(NBTIO.write(tagList, ByteOrder.LITTLE_ENDIAN, true));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeToDisk(BinaryStream stream) {
        BitArrayVersion version = bitArray.getVersion();
        stream.putByte((byte) getPaletteHeader(version, false));
        if (version == BitArrayVersion.EMPTY) {
            return;
        }

        if (version != BitArrayVersion.V0) {
            for (int word : bitArray.getWords()) {
                stream.putLInt(word);
            }

            stream.putLInt(palette.size());
        }

        List<CompoundTag> tagList = new ObjectArrayList<>();
        for (int i = 0; i < this.palette.size(); i++) {
            int fullId = this.palette.getInt(i);
            if (ENABLE_BLOCK_STATE_PERSISTENCE) {
                tagList.add(BlockSerializer.serializeRuntime(fullId));
                continue;
            }

            int id = fullId >> Block.BLOCK_META_BITS;
            String name = GlobalBlockPalette.getNameByBlockId(id);
            if (name == null) {
                tagList.add(UNKNOWN_BLOCK_TAG);
                log.warn("Unmapped block ID: {}", id);
                continue;
            }
            tagList.add(new CompoundTag()
                    .putString("name", name)
                    .putShort("val", fullId & Block.BLOCK_META_MASK));
        }
        try {
            stream.put(NBTIO.write(tagList, ByteOrder.LITTLE_ENDIAN, false));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeToDiskBiome(BinaryStream stream) {
        BitArrayVersion version = bitArray.getVersion();
        stream.putByte((byte) getPaletteHeader(version, true));
        if (version == BitArrayVersion.EMPTY) {
            return;
        }

        if (version != BitArrayVersion.V0) {
            for (int word : bitArray.getWords()) {
                stream.putLInt(word);
            }

            stream.putLInt(palette.size());
        }

        for (int i = 0; i < this.palette.size(); i++) {
            stream.putLInt(this.palette.getInt(i));
        }
    }

    private void grow(BitArrayVersion version) {
        BitArray newBitArray = version.createPalette(SIZE);
        for (int i = 0; i < SIZE; i++) {
            newBitArray.set(i, this.bitArray.get(i));
        }
        this.bitArray = newBitArray;
    }

    /**
     * @return palette index
     */
    private int getOrAdd(int id) {
        int index = this.palette.indexOf(id);
        if (index != -1) {
            return index;
        }

        index = this.palette.size();
        BitArrayVersion version = this.bitArray.getVersion();
        if (index > version.getMaxEntryValue()) {
            BitArrayVersion next = version.next();
            if (next != null) {
                this.grow(next);
            } else if (!this.compress()) {
                throw new IndexOutOfBoundsException("too many elements");
            }
        }
        this.palette.add(id);
        return index;
    }

    public boolean isEmpty() {
        return this.isEmpty(false);
    }

    public boolean isEmpty(boolean fast) {
        if (this.palette.isEmpty()) {
            return true;
        }

        boolean hasBlock = false;
        for (int i = 0; i < this.palette.size(); i++) {
            int id = this.palette.getInt(i);
            if (id != BlockID.AIR) {
                hasBlock = true;
                break;
            }
        }

        if (!hasBlock) {
            return true;
        }
        if (fast) {
            return false;
        }

        if (this.palette.getInt(0) != BlockID.AIR) {
            // Hive Chunker...
            return false;
        }

        for (int word : this.bitArray.getWords()) {
            if (word != 0) {
                return false;
            }
        }
        this.palette.clear();
        return true;
    }

    /**
     * @return dirty
     */
    public boolean compress() {
        if (this.palette.isEmpty()) {
            return false;
        }

        int count = this.palette.size();
        if (count == 1 && this.palette.getInt(0) == BlockID.AIR) {
            return false;
        }

        boolean noBlock = true;
        for (int i = 1; i < count; i++) {
            int id = this.palette.getInt(i);
            if (id == BlockID.AIR) {
                continue;
            }
            noBlock = false;
            break;
        }
        if (noBlock) {
            int firstId = this.palette.getInt(0);
            this.palette.clear();
            this.palette.add(firstId);

//            Arrays.fill(this.bitArray.getWords(), 0);
            this.bitArray = BitArrayVersion.V1.createPalette(SIZE);
            return true;
        }

        BitArrayVersion version = BitArrayVersion.V2;
        BitArray newArray = version.createPalette(SIZE);
        IntList newPalette = new IntArrayList(count);
        newPalette.add(this.palette.getInt(0));
        for (int i = 0; i < SIZE; i++) {
            int paletteIndex = this.bitArray.get(i);
            int id = this.palette.getInt(paletteIndex);
            int newIndex = newPalette.indexOf(id);

            if (newIndex == -1) {
                newIndex = newPalette.size();
                newPalette.add(id);

                if (newIndex > version.getMaxEntryValue()) {
                    version = version.next();
                    BitArray growArray = version.createPalette(SIZE);
                    for (int j = 0; j < i; j++) {
                        growArray.set(j, newArray.get(j));
                    }
                    newArray = growArray;
                }
            }

            newArray.set(i, newIndex);
        }
        this.bitArray = newArray;
        this.palette = newPalette;
        return true;
    }

    public PalettedSubChunkStorage copy() {
        return new PalettedSubChunkStorage(this.bitArray.copy(), new IntArrayList(this.palette));
    }

    protected static int elementIndex(int x, int y, int z) {
        int index = (x << 8) | (z << 4) | y;
        if (index < 0 || index >= SUB_CHUNK_SIZE) {
            throw new IllegalArgumentException("Invalid index: " + x + ", " + y + ", " + z );
        }
        return index;
    }
}
