package cn.nukkit.level.biome;

public class BiomeHeight {
    public static final BiomeHeight DEFAULT = new BiomeHeight(0.1f, 0.2f);
    public static final BiomeHeight DEFAULT_MUTATED = new BiomeHeight(0.2f, 0.4f);
    public static final BiomeHeight RIVER = new BiomeHeight(-0.5f, 0);
    public static final BiomeHeight OCEAN = new BiomeHeight(-1f, 0.1f);
    public static final BiomeHeight DEEP_OCEAN = new BiomeHeight(-1.8f, 0.1f);
    public static final BiomeHeight LOWLANDS = new BiomeHeight(0.125f, 0.05f);
    public static final BiomeHeight TAIGA = new BiomeHeight(0.2f, 0.2f);
    public static final BiomeHeight MOUNTAINS = new BiomeHeight(0.45f, 0.3f);
    public static final BiomeHeight HIGHLANDS = new BiomeHeight(1.5f, 0.025f);
    public static final BiomeHeight EXTREME = new BiomeHeight(1, 0.5f);
    public static final BiomeHeight LESS_EXTREME = EXTREME.less();
    public static final BiomeHeight BEACH = new BiomeHeight(0, 0.025f);
    public static final BiomeHeight STONE_BEACH = new BiomeHeight(0.1f, 0.8f);
    public static final BiomeHeight MUSHROOM = new BiomeHeight(0.2f, 0.3f);
    public static final BiomeHeight SWAMP = new BiomeHeight(-0.2f, 0.1f);

    public final float depth;
    public final float scale;

    public BiomeHeight(float depth, float scale) {
        this.depth = depth;
        this.scale = scale;
    }

    public BiomeHeight less() {
        return new BiomeHeight(depth * 0.8f, scale * 0.8f);
    }

    public void apply(Biome biome) {
        biome.setBaseHeight(depth);
        biome.setHeightVariation(scale);
    }
}
