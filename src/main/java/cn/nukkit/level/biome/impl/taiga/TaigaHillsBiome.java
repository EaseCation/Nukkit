package cn.nukkit.level.biome.impl.taiga;

/**
 * @author DaPorkchop_
 * Nukkit Project
 */
public class TaigaHillsBiome extends TaigaBiome {

    public TaigaHillsBiome() {
        super();

        this.setBaseHeight(0.45f);
        this.setHeightVariation(0.3f);
    }

    @Override
    public String getName() {
        return "Taiga Hills";
    }
}
