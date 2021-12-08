package cn.nukkit.level.util;

import cn.nukkit.block.Block;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BinaryStream;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

public class PalettedSubChunkStorage {

    private static final int SIZE = 16 * 16 * 16;

    private final IntList palette; //HACK: block 的 cache 版本用的是 fullId 而不是 runtimeId
    private BitArray bitArray;

    private PalettedSubChunkStorage(BitArrayVersion version, int firstId) {
        this.bitArray = version.createPalette(SIZE);
        if (version == BitArrayVersion.EMPTY) {
            this.palette = IntLists.EMPTY_LIST;
            return;
        }
        this.palette = new IntArrayList(version.isSingleton() ? 1 : 16);
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

    public static PalettedSubChunkStorage ofBiome() {
        return new PalettedSubChunkStorage(BitArrayVersion.V2, EnumBiome.OCEAN.id);
    }

    public static PalettedSubChunkStorage ofBiome(BitArrayVersion version) {
        return new PalettedSubChunkStorage(version, EnumBiome.OCEAN.id);
    }

    public static PalettedSubChunkStorage ofBiome(int biomeId) {
        return new PalettedSubChunkStorage(BitArrayVersion.V2, biomeId);
    }

    public static PalettedSubChunkStorage ofBiome(BitArrayVersion version, int biomeId) {
        return new PalettedSubChunkStorage(version, biomeId);
    }

    private static int getPaletteHeader(BitArrayVersion version, boolean runtime) {
//        if (version == BitArrayVersion.EMPTY) {
//            runtime = true;
//        }
        return (version.getId() << 1) | (runtime ? 1 : 0);
    }

    public void set(int index, int value) {
        try {
            int id = this.idFor(value);
            this.bitArray.set(index, id);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unable to set value: " + value + ", palette: " + palette, e);
        }
    }

    public void writeTo(BinaryStream stream) {
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

        palette.forEach((IntConsumer) stream::putVarInt);
    }

    public void writeToCache(BinaryStream stream) {
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

        List<CompoundTag> tagList = new ArrayList<>();
        for (int legacyId : palette) {
            //tagList.add(GlobalBlockPalette.getState(runtimeId));
            tagList.add(new CompoundTag() // 1.12 的格式, 可向前兼容
                    .putString("name", GlobalBlockPalette.getNameByBlockId(legacyId >> 4))
                    .putShort("val", legacyId & 0xf));
        }
        try {
            stream.put(NBTIO.write(tagList, ByteOrder.LITTLE_ENDIAN, true));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void onResize(BitArrayVersion version) {
        BitArray newBitArray = version.createPalette(SIZE);

        for (int i = 0; i < SIZE; i++) {
            newBitArray.set(i, this.bitArray.get(i));
        }
        this.bitArray = newBitArray;
    }

    private int idFor(int runtimeId) {
        int index = this.palette.indexOf(runtimeId);
        if (index != -1) {
            return index;
        }

        index = this.palette.size();
        BitArrayVersion version = this.bitArray.getVersion();
        if (index > version.getMaxEntryValue()) {
            BitArrayVersion next = version.next();
            if (next != null) {
                this.onResize(next);
            }
        }
        this.palette.add(runtimeId);
        return index;
    }

    public boolean isEmpty() {
        if (this.palette.size() == 1) {
            return true;
        }
        for (int word : this.bitArray.getWords()) {
            if (Integer.toUnsignedLong(word) != 0L) {
                return false;
            }
        }
        return true;
    }

    public PalettedSubChunkStorage copy() {
        return new PalettedSubChunkStorage(this.bitArray.copy(), new IntArrayList(this.palette));
    }
}
