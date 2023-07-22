package cn.nukkit.level.biome.impl.swamp;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
//porktodo: this should be flat in most places, and only rise up in a few
public class SwamplandMBiome extends SwampBiome {

    public SwamplandMBiome() {
        super();

        this.setBaseHeight(-0.1f);
        this.setHeightVariation(0.3f);
    }

    @Override
    public String getName() {
        return "Swampland M";
    }
}
