package cn.nukkit.level;

import cn.nukkit.level.generator.Generator;

import javax.annotation.Nullable;

public enum Dimension {
    OVERWORLD("Overworld", "minecraft:overworld", Generator.TYPE_INFINITE, HeightRange.blockY(-64, 320)),
    NETHER("Nether", "minecraft:nether", Generator.TYPE_NETHER, HeightRange.blockY(0, 128)),
    END("TheEnd", "minecraft:the_end", Generator.TYPE_END, HeightRange.blockY(0, 256)),
    ;

    private static final Dimension[] VALUES = values();

    private final String name;
    private final String identifier;
    private final int generator;
    private final HeightRange heightRange;

    Dimension(String name, String identifier, int generator, HeightRange heightRange) {
        this.name = name;
        this.identifier = identifier;
        this.generator = generator;
        this.heightRange = heightRange;
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
