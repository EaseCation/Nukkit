package cn.nukkit.level.util;

public class EmptyBitArray implements BitArray {

    public static final EmptyBitArray INSTANCE = new EmptyBitArray();

    private static final int[] EMPTY_WORDS = new int[0];

    private EmptyBitArray() {

    }

    @Override
    public void set(int index, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int get(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public int[] getWords() {
        return EMPTY_WORDS;
    }

    @Override
    public BitArrayVersion getVersion() {
        return BitArrayVersion.EMPTY;
    }

    @Override
    public BitArray copy() {
        return INSTANCE;
    }
}
