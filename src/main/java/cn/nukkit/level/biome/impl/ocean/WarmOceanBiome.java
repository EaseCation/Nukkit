package cn.nukkit.level.biome.impl.ocean;

public class WarmOceanBiome extends OceanBiome {
    public WarmOceanBiome() {
        super();
    }

    @Override
    public String getName() {
        return "Warm Ocean";
    }

    @Override
    public int getGroundBlock(int y) {
        return SAND;
    }
}
