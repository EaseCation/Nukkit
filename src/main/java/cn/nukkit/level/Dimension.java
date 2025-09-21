package cn.nukkit.level;

import cn.nukkit.level.generator.GeneratorID;

import javax.annotation.Nullable;

//TODO: data-driven custom dimensions
public enum Dimension {
    OVERWORLD("Overworld", DimensionFullNames.OVERWORLD, GeneratorID.OVERWORLD, HeightRange.blockY(-64, 320), -1),
    NETHER("Nether", DimensionFullNames.NETHER, GeneratorID.NETHER, HeightRange.blockY(0, 128), Level.TIME_MIDNIGHT),
    THE_END("TheEnd", DimensionFullNames.THE_END, GeneratorID.THE_END, HeightRange.blockY(0, 256), Level.TIME_NOON),
    ;

    private static final Dimension[] VALUES = values();

    private final String name;
    private final String identifier;
    private final int generator;
    private final HeightRange heightRange;
    private final int fixedTime;

    Dimension(String name, String identifier, int generator, HeightRange heightRange, int fixedTime) {
        this.name = name;
        this.identifier = identifier;
        this.generator = generator;
        this.heightRange = heightRange;
        this.fixedTime = fixedTime;
    }

    public int getId() {
        return ordinal();
    }

    @Override
    public String toString() {
        return name;
    }

    public String getIdentifier() {
        return identifier;
    }

    /**
     * Default generator type
     */
    public int getGenerator() {
        return generator;
    }

    /**
     * Default height range
     */
    public HeightRange getHeightRange() {
        return heightRange;
    }

    public int getFixedTime() {
        return fixedTime;
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
