package cn.nukkit.level.biome.impl.ocean;

public class LukewarmOceanBiome extends OceanBiome {
    public LukewarmOceanBiome() {
        super();
    }

    @Override
    public String getName() {
        return "Lukewarm Ocean";
    }

    @Override
    public int getGroundBlock(int y) {
        return SAND;
    }
}
