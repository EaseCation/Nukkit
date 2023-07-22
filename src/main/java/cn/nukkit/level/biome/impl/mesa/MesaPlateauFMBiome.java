package cn.nukkit.level.biome.impl.mesa;

/**
 * @author DaPorkchop_
 */
//porktodo: this biome has much smaller and more frequent plateaus than the normal mesa plateau (which is all one giant one)
public class MesaPlateauFMBiome extends MesaPlateauFBiome {
    public MesaPlateauFMBiome() {
        super();

        this.setBaseHeight(0.45f);
        this.setHeightVariation(0.3f);
    }

    @Override
    public String getName() {
        return "Mesa Plateau F M";
    }
}
