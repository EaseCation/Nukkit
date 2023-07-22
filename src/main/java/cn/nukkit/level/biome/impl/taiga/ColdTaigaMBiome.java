package cn.nukkit.level.biome.impl.taiga;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
//porktodo: this biome has very steep cliffs
public class ColdTaigaMBiome extends ColdTaigaBiome {
    public ColdTaigaMBiome() {
        super();

        this.setBaseHeight(0.3f);
        this.setHeightVariation(0.4f);
    }

    @Override
    public String getName() {
        return "Cold Taiga M";
    }

    @Override
    public boolean doesOverhang() {
        return true;
    }
}
