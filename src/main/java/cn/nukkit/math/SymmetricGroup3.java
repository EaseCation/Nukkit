package cn.nukkit.math;

public enum SymmetricGroup3 {
    P123(0, 1, 2),
    P213(1, 0, 2),
    P132(0, 2, 1),
    P231(1, 2, 0),
    P312(2, 0, 1),
    P321(2, 1, 0);

    private static final SymmetricGroup3[] VALUES = values();

    private final int[] permutation;

    SymmetricGroup3(int x, int y, int z) {
        permutation = new int[]{x, y, z};
    }

    public int permutation(int index) {
        return permutation[index];
    }

    public static SymmetricGroup3[] getValues() {
        return VALUES;
    }
}
