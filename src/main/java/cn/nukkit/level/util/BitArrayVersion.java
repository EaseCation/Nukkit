package cn.nukkit.level.util;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static cn.nukkit.SharedConstants.*;

public enum BitArrayVersion {
    V16(16, 2, null),
    V8(8, 4, V16),
    /**
     * 2 bit padding.
     */
    V6(6, 5, V8, true),
    /**
     * 2 bit padding.
     */
    V5(5, 6, DISABLE_SUB_CHUNK_STORAGE_PADDING ? V8 : V6, true),
    V4(4, 8, DISABLE_SUB_CHUNK_STORAGE_PADDING ? V8 : V5),
    /**
     * 2 bit padding.
     */
    V3(3, 10, V4, true),
    V2(2, 16, DISABLE_SUB_CHUNK_STORAGE_PADDING ? V4 : V3),
    V1(1, 32, V2) {
        @Override
        public boolean isSingleton() {
            return true;
        }
    },
    /**
     * 1 element.
     * @since 1.18.0
     */
    V0(0, 0, V2) {
        @Override
        public BitArray createPalette(int size) {
            return new SingletonBitArray(size);
        }

        @Override
        public BitArray createPalette(int size, int[] words) {
            return new SingletonBitArray(size);
        }

        @Override
        public int getWordsForSize(int size) {
            return 0;
        }

        @Override
        public boolean isSingleton() {
            return true;
        }
    },
    /**
     * No element. mainly used for 3D biomes.
     * @since 1.18.0
     */
    EMPTY(-1, -1, V0) {
        @Override
        public BitArray createPalette(int size) {
            return EmptyBitArray.INSTANCE;
        }

        @Override
        public BitArray createPalette(int size, int[] words) {
            return EmptyBitArray.INSTANCE;
        }

        @Override
        public int getWordsForSize(int size) {
            return 0;
        }
    };

    private static final BitArrayVersion[] VALUES = values();
    private static final BitArrayVersion[] VERSIONS;
    private static final BitArrayVersion[] UNIVERSAL_VERSIONS;

    static {
        List<BitArrayVersion> universalVersions = ObjectArrayList.wrap(VALUES);
        Collections.reverse(universalVersions);
        VERSIONS = universalVersions.toArray(new BitArrayVersion[0]);

        universalVersions = Arrays.stream(VALUES)
                .filter(ver -> ver.bits > 0)
                .collect(Collectors.toList());
        Collections.reverse(universalVersions);
        UNIVERSAL_VERSIONS = universalVersions.toArray(new BitArrayVersion[0]);
    }

    final byte bits;
    final byte entriesPerWord;
    final int maxEntryValue;
    final BitArrayVersion next;
    final boolean padding;
    final BitArrayFactory bitArrayFactory;

    BitArrayVersion(int bits, int entriesPerWord, BitArrayVersion next) {
        this(bits, entriesPerWord, next, false);
    }

    BitArrayVersion(int bits, int entriesPerWord, BitArrayVersion next, boolean padding) {
        this.bits = (byte) bits;
        this.entriesPerWord = (byte) entriesPerWord;
        this.maxEntryValue = bits < 0 ? bits : (1 << this.bits) - 1;
        this.next = next;
        this.padding = padding;
        if (padding) {
            // Padded palettes aren't able to use bitwise operations due to their padding.
            bitArrayFactory = PaddedBitArray::new;
        } else {
            bitArrayFactory = Pow2BitArray::new;
        }
    }

    public static BitArrayVersion get(int version, boolean read) {
        for (BitArrayVersion ver : VERSIONS) {
            if (read) {
                if (ver.bits == version) {
                    return ver;
                }
                continue;
            }

            if (ver.entriesPerWord <= version && (!DISABLE_SUB_CHUNK_STORAGE_PADDING || !ver.padding)) {
                return ver;
            }
        }
        throw new IllegalArgumentException("Invalid palette version: " + version);
    }

    public static BitArrayVersion getUniversal(int paletteLength) {
        for (BitArrayVersion ver : UNIVERSAL_VERSIONS) {
            if (paletteLength <= ver.maxEntryValue && (!DISABLE_SUB_CHUNK_STORAGE_PADDING || !ver.padding)) {
                return ver;
            }
        }
        throw new IndexOutOfBoundsException("Too many elements: " + paletteLength);
    }

    public BitArray createPalette(int size) {
        return this.createPaletteInternal(size, new int[this.getWordsForSizeInternal(size)]);
    }

    public BitArray createPalette(int size, int[] words) {
        return this.createPaletteInternal(size, words);
    }

    public byte getId() {
        return bits;
    }

    public int getWordsForSize(int size) {
        return this.getWordsForSizeInternal(size);
    }

    private int getWordsForSizeInternal(int size) {
        return (size / entriesPerWord) + (size % entriesPerWord == 0 ? 0 : 1);
    }

    public int getMaxEntryValue() {
        return maxEntryValue;
    }

    public BitArrayVersion next() {
        return next;
    }

    private BitArray createPaletteInternal(int size, int[] words) {
        return bitArrayFactory.create(this, size, words);
    }

    public boolean isSingleton() {
        return false;
    }

    /**
     * @return versions in ascending order
     */
    public static BitArrayVersion[] getVersions() {
        return VERSIONS;
    }

    /**
     * @return universal versions (block and biome) in ascending order
     */
    public static BitArrayVersion[] getUniversalVersions() {
        return UNIVERSAL_VERSIONS;
    }

    /**
     * @return versions in descending order
     */
    public static BitArrayVersion[] getValues() {
        return VALUES;
    }

    @FunctionalInterface
    private interface BitArrayFactory {
        BitArray create(BitArrayVersion version, int size, int[] words);
    }
}
