package cn.nukkit.level.biome.impl.forest;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class ForestHillsBiome extends ForestBiome {
    public ForestHillsBiome() {
        this(TYPE_NORMAL);
    }

    public ForestHillsBiome(int type) {
        super(type);

        if (type != TYPE_BIRCH_TALL) {
            this.setBaseHeight(0.45f);
            this.setHeightVariation(0.3f);
        } else {
            this.setBaseHeight(0.55f);
            this.setHeightVariation(0.5f);
        }
    }

    @Override
    public String getName() {
        switch (this.type) {
            case TYPE_BIRCH:
                return "Birch Forest Hills";
            case TYPE_BIRCH_TALL:
                return "Birch Forest Hills M";
            default:
                return "Forest Hills";
        }
    }
}
