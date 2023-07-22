package cn.nukkit.level.biome.impl.ocean;

import cn.nukkit.level.generator.populator.impl.WaterIcePopulator;

public class NewFrozenOceanBiome extends OceanBiome {
    public NewFrozenOceanBiome() {
        super();

        //TODO: ice mountains

        WaterIcePopulator ice = new WaterIcePopulator();
        this.addPopulator(ice);
    }

    @Override
    public String getName() {
        return "Frozen Ocean";
    }

    @Override
    public boolean isFreezing() {
        return true;
    }

    @Override
    public boolean canRain() {
        return false;
    }
}
