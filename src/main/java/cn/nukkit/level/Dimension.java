package cn.nukkit.level;

import javax.annotation.Nullable;

public enum Dimension {
    OVERWORLD("Overworld", 255, 0),
    NETHER("Nether", 127, 0),
    END("End", 255, 0),
    ;

    private static final Dimension[] VALUES = values();

    private final String name;
    private final int maxHeight;
    private final int minHeight;

    Dimension(String name, int maxHeight, int minHeight) {
        this.name = name;
        this.maxHeight = maxHeight;
        this.minHeight = minHeight;
    }

    @Override
    public String toString() {
        return name;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public int getMinHeight() {
        return minHeight;
    }

    @Nullable
    public static Dimension byId(int id) {
        if (id < 0 || id >= VALUES.length) {
            return null;
        }
        return VALUES[id];
    }

    public static Dimension byIdOrDefault(int id) {
        Dimension dimension = byId(id);
        return dimension != null ? dimension : OVERWORLD;
    }

    public static Dimension[] getValues() {
        return VALUES;
    }
}
