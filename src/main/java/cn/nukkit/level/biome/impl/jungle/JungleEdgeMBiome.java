package cn.nukkit.level.biome.impl.jungle;

/**
 * @author DaPorkchpo_
 */
//porktodo: this biome has steep cliffs and flat plains
public class JungleEdgeMBiome extends JungleEdgeBiome {
    public JungleEdgeMBiome() {
        super();

        this.setBaseHeight(0.2f);
        this.setHeightVariation(0.4f);
    }

    @Override
    public String getName() {
        return "Jungle Edge M";
    }
}
