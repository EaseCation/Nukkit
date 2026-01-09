package cn.nukkit.level.biome.impl.mountain;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.biome.type.SnowyBiome;
import cn.nukkit.level.generator.object.ore.OreType;
import cn.nukkit.level.generator.populator.impl.PopulatorOre;
import cn.nukkit.level.generator.populator.impl.WaterIcePopulator;

public class FrozenPeaksBiome extends SnowyBiome {
    public FrozenPeaksBiome() {
        super();

//        WaterIcePopulator ice = new WaterIcePopulator();
//        this.addPopulator(ice);

        this.addPopulator(new PopulatorOre(
                new OreType(Block.get(BlockID.EMERALD_ORE), 11, 1, 0, 32),
                new OreType(Block.get(BlockID.INFESTED_STONE), 7, 9, 0, 63)));

        this.setBaseHeight(1.5f);
        this.setHeightVariation(0.4f);
    }

    @Override
    public String getName() {
        return "Frozen Peaks";
    }

    @Override
    public int getSurfaceBlock(int y) {
        return SNOW;
    }

    @Override
    public int getGroundBlock(int y) {
        return SNOW;
    }

    @Override
    public boolean isFreezing() {
        return true;
    }
}
